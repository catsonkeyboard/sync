package org.catsonkeyboard;

import com.google.gson.*;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import org.apache.cayenne.util.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import org.catsonkeyboard.Utils.PackageUtil;
import org.catsonkeyboard.annotation.NotServer;
import org.catsonkeyboard.common.DeqMap;
import org.catsonkeyboard.config.JpaEntityManagerFactory;
import org.catsonkeyboard.dao.QueryWrap;
import org.catsonkeyboard.entities.SyncTag;
import org.catsonkeyboard.http.HttpClientWrap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Sync {
    private EntityManager entityManager;
    private String entityPackage = "org.catsonkeyboard.entities";
    private final HashMap<String, Class<?>> topics = new HashMap<>();
    private final LinkedHashMap<String, String> topicPrimaryKeyFields = new LinkedHashMap<>();
    private final ConcurrentLinkedHashMap<String, Subscriber<?>> subscribers = new ConcurrentLinkedHashMap.Builder<String, Subscriber<?>>().maximumWeightedCapacity(100).build();
    private final ConcurrentLinkedQueue<Map.Entry<String, Runnable>> queue = new ConcurrentLinkedQueue<>();
    private static ThreadPoolExecutor pool;

    static {
        pool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    public void Start() throws Exception {
        loadTopics();
        List<Map.Entry<String,Subscriber<?>>> syncs = this.subscribers.entrySet().stream().filter(p -> p.getValue().getSyncToServer()).collect(Collectors.toList());
        JsonObject requestBody = new JsonObject();
        for(Map.Entry<String, Subscriber<?>> sync : syncs) {
            String topic = sync.getValue().getTopic();
            JsonObject reqParam = new JsonObject();
            //查询本地SyncTag表中最新时间戳
            QueryWrap<SyncTag> syncTagQueryWrap = new QueryWrap<>(entityManager) { };
            List<SyncTag> syncTags = syncTagQueryWrap.findAll();
            Optional<SyncTag> syncTag = syncTags.stream().filter(p -> topic.equals(p.getTopic())).findFirst();
            if(syncTag.isEmpty()) {
                reqParam.addProperty("lut", 0);
            } else {
                reqParam.addProperty("lut",syncTag.get().getLut());
            }
            String filter = "true";
            reqParam.addProperty("filter","(" + filter + ")");
            requestBody.add(topic, reqParam);
        }
        Gson gson = new Gson();
        String requestBodyStr = requestBody.toString();
        String responseStr = HttpClientWrap.post(System.getenv("HOST"),requestBodyStr);
        JsonObject jsonObject = new JsonParser().parse(responseStr).getAsJsonObject();
        for(var entry : jsonObject.entrySet()) {
            String topic = entry.getKey();
            JsonElement value = entry.getValue();
            if(value.isJsonArray()) {
                JsonArray jsonArray = value.getAsJsonArray();
                save(topic, topicPrimaryKeyFields.get(topic),jsonArray, () -> {});
            }
        }
    }

    public void save(String topic, String key, JsonArray data, Runnable runnable) throws Exception {
        Class modelClass = topics.get(topic);
//        Metamodel metamodel = entityManager.getMetamodel();
//        Set<EntityType<?>> entities = metamodel.getEntities();
        Optional<Field> keyField = Arrays.stream(modelClass.getDeclaredFields()).filter(p -> p.getName().equals(key)).findFirst();
        Gson gson = new Gson();
        if(keyField.isEmpty()) {
            throw new Exception("entity no key");
        }
        QueryWrap<?> modelQuery = new QueryWrap<>(entityManager,modelClass);
        List<Object> commitObjects = new ArrayList<>();
        for(JsonElement item : data) {
            JsonObject itemValue = item.getAsJsonObject();
            keyField.get().setAccessible(true);
//            Class<?> keyFieldType = keyField.get().getType();
//            Constructor<?> constructor = keyFieldType.getDeclaredConstructor(keyFieldType);
//            Object keyValue = constructor.newInstance(itemValue.get(key));
            Object keyValue = gson.fromJson(itemValue.get(key), keyField.get().getType());
            Object savedObj = modelQuery.findByKey(key, keyValue);
            if(savedObj == null) {
                savedObj = modelClass.getDeclaredConstructor().newInstance();
            }
            //遍历各个json节点
            for(Map.Entry<String,JsonElement> kv : itemValue.entrySet()) {
                List<Field> fields = new ArrayList<>();
                //获取父类fields
                fields.addAll(List.of(modelClass.getSuperclass().getDeclaredFields()));
                fields.addAll(List.of(modelClass.getDeclaredFields()));
                Optional<Field> field = fields.stream().filter(p -> {
                            if(p.isAnnotationPresent(Column.class)) {
                                return p.getAnnotation(Column.class).name().equals(kv.getKey());
                            } else {
                                return p.getName().equals(kv.getKey());
                            }
                        }
                ).findFirst();
                if(field.isPresent()) {
                    field.get().setAccessible(true);
                    if(field.get().isAnnotationPresent(NotServer.class)) {
                        continue;
                    }
                    Object fieldValue = null;
                    if(field.get().getClass().equals(LocalDateTime.class)) {
                        //JsonElement的时间戳格式转为LocalDateTime
                        Long longValue = gson.fromJson(kv.getValue(), Long.class);
                        fieldValue = getDateTimeOfTimestamp(longValue);//时间戳转LocalDateTime
                    } else {
                        fieldValue = gson.fromJson(kv.getValue(), field.get().getType());
                    }

                    field.get().setAccessible(true);
                    field.get().set(savedObj, fieldValue);
                }
            }
            commitObjects.add(savedObj);
        }
        //data commit
        entityManager.getTransaction().begin();
        for(var commit : commitObjects) {
            entityManager.persist(commit);
        }
        //save SyncTag
        entityManager.getTransaction().commit();
        System.out.println("commit success");
    }

    public  LocalDateTime getDateTimeOfTimestamp(long timestamp) {
        return LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.ofHours(8));
    }

    /**
     * 加载所有的topic
     */
    private void loadTopics() {
        List<String> classNames = PackageUtil.getClassName(entityPackage, true);
        if(classNames != null) {
            for (String className : classNames) {
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(Table.class)) {
                        Table topic = clazz.getAnnotation(Table.class);
                        topics.put(topic.name(), clazz);
                        //获取主键字段
                        Field[] fields = clazz.getDeclaredFields();
                        Arrays.stream(fields).filter(p -> p.isAnnotationPresent(Id.class)).findFirst().ifPresent(p ->
                            topicPrimaryKeyFields.put(topic.name(), p.getName())
                        );
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            var factory = new JpaEntityManagerFactory(topics.values().toArray(new Class[topics.values().size()]));
            this.entityManager = factory.getEntityManager();
        }
    }

    public <T> String subscribeTopic(Class<T> clazz,String topic, Supplier<String> serverFilter, Function<T, Boolean> clientFilter, Runnable action, Boolean syncToServer) {
        var uuid = UUID.randomUUID().toString().replace("-","");
        subscribers.put(uuid, new Subscriber(topic, clazz, serverFilter, clientFilter, action, syncToServer));
        return uuid;
    }
}

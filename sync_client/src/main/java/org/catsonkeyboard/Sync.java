package org.catsonkeyboard;

import com.google.gson.JsonObject;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.apache.cayenne.util.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import org.catsonkeyboard.Utils.PackageUtil;
import org.catsonkeyboard.common.DeqMap;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Sync {

    private String entityPackage = "org.catsonkeyboard.entities";
    private DeqMap<String, Type> topics = new DeqMap<>();
    private LinkedHashMap<String, String> topicPrimaryKeyFields = new LinkedHashMap<>();
    private ConcurrentLinkedHashMap<String, Subscriber> subscribers = new ConcurrentLinkedHashMap.Builder<String, Subscriber>().maximumWeightedCapacity(100).build();
    private ConcurrentLinkedQueue<Map.Entry<String, Runnable>> queue = new ConcurrentLinkedQueue<>();

    private static ThreadPoolExecutor pool;
    static {
        pool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    public void Start() {
//        SyncTask task = new SyncTask();
//        pool.submit(task);
        List<Map.Entry<String,Subscriber>> syncs = this.subscribers.entrySet().stream().filter(p -> p.getValue().getSyncToServer()).collect(Collectors.toList());
        for(Map.Entry<String, Subscriber> sync : syncs) {
            String topic = sync.getKey();
            JsonObject reqParam = new JsonObject();
            //查询本地SyncTag表中最新时间戳
            reqParam.addProperty("lut", 0);
        }
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
        }
    }

    public <T> String subscribeTopic(String topic, Supplier<String> serverFilter, Function<T, Boolean> clientFilter, Runnable action, Boolean syncToServer) {
        var uuid = UUID.randomUUID().toString();
        subscribers.put(uuid, new Subscriber(topic, serverFilter, clientFilter, action, syncToServer));
        return uuid;
    }
}

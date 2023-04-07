package org.catsonkeyboard;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.catsonkeyboard.config.JpaEntityManagerFactory;
import org.catsonkeyboard.dao.QueryWrap;
import org.catsonkeyboard.entities.SyncTag;
import org.catsonkeyboard.entities.User;
import org.catsonkeyboard.http.HttpClientWrap;

import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
//          EntityManager entityManager = new JpaEntityManagerFactory(
//          new Class[]{User.class, SyncTag.class}).getEntityManager();

//        EntityManager entityManager = new JpaEntityManagerFactory(
//                new Class[]{User.class}).getEntityManager();
//        entityManager.getTransaction().begin();
//        Student student = new Student();
////        student.setId(1L);
//        student.setName("张三");
//        student.setSex(true);
//        entityManager.persist(student);
//        entityManager.getTransaction().commit();
//        entityManager.clear();
//        Student foundStudent = entityManager.find(Student.class, 1L);
//        Query query = entityManager.createNativeQuery("select * from Student");
//        var result = query.getResultList();


//        String responseStr = HttpClientWrap.post("","{\"user\": { \"lut\": 0, \"filter\": \"(true)\" }}");
//        Gson gson = new Gson();
//        JsonObject jsonObject = new JsonParser().parse(responseStr).getAsJsonObject();
//        for(var entry : jsonObject.entrySet()) {
//            JsonElement value = entry.getValue();
//            if(value.isJsonArray()) {
//                JsonArray jsonArray = value.getAsJsonArray();
//                if(jsonArray.size() == 0) {
//
//                } else {
//                    List<User> users = gson.fromJson(jsonArray,new TypeToken<List<User>>(){}.getType());
//                    System.out.println("test");
//                }
//            }
//        }
//        Sync sync = new Sync();
//        sync.Start();
//        System.out.println("test");

        //QueryWrap<User> queryWrap = new QueryWrap<>(User.class);
//        QueryWrap<User> queryWrap = new QueryWrap<>() { };
//        var list = queryWrap.find();

//        EntityManager entityManager = new JpaEntityManagerFactory(
//                new Class[]{User.class, SyncTag.class}).getEntityManager();
//        QueryWrap<User> queryWrap = new QueryWrap<>(entityManager, User.class);

        Sync sync = new Sync();
        sync.subscribeTopic(User.class,"user",() -> "true",(u) -> { return true; },() -> {},true);
        sync.Start();
        //sync.test();
    }
}
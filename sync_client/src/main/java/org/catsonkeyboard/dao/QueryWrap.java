package org.catsonkeyboard.dao;

import com.google.gson.reflect.TypeToken;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.catsonkeyboard.config.JpaEntityManagerFactory;
import org.catsonkeyboard.entities.User;

import java.lang.reflect.Type;
import java.util.List;

public class QueryWrap<T> {

    private Type type;
    private EntityManager entityManager;

    public QueryWrap() {
        entityManager = new JpaEntityManagerFactory(
                new Class[]{ User.class }).getEntityManager();
    }

    public List find() {
        Query query = entityManager.createNativeQuery("select * from user");
        return query.getResultList();
    }

}

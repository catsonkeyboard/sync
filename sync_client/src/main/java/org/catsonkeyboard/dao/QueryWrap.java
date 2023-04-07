package org.catsonkeyboard.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.catsonkeyboard.config.JpaEntityManagerFactory;
import org.hibernate.Session;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public class QueryWrap<T> {
    private Class<T> clazz;
    private EntityManager entityManager;

    public QueryWrap(EntityManager entityManager, Class<T> clazz) {
        this.clazz = clazz;
        this.entityManager = entityManager;
//        entityManager = new JpaEntityManagerFactory(
//                new Class[]{ clazz }).getEntityManager();
    }

    public QueryWrap(EntityManager entityManager) {
        clazz = (Class<T>) ((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.entityManager = entityManager;
        //        entityManager = new JpaEntityManagerFactory(
//                new Class[]{ clazz }).getEntityManager();
    }

//    public T findOne() {
//
//    }

    public List<T> find(Predicate[] predicates) {
        //Session session = HibernateUtil.getHibernateSession();
        Session session = entityManager.unwrap(Session.class);
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<T> cr = cb.createQuery(clazz);
        Root<T> root = cr.from(clazz);
        cr.select(root).where(predicates);
        Query query = session.createQuery(cr);
        List<T> results = query.getResultList();
        return results;
    }

    public List<T> findAll() {
        Session session = entityManager.unwrap(Session.class);
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<T> cr = cb.createQuery(clazz);
        Root<T> root = cr.from(clazz);
        cr.select(root);
        Query query = session.createQuery(cr);
        List<T> results = query.getResultList();
        return results;
    }

    public T findByKey(String keyName, Object keyValue) {
        Session session = entityManager.unwrap(Session.class);
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<T> cr = cb.createQuery(clazz);
        Root<T> root = cr.from(clazz);
        cr.select(root).where(cb.equal(root.get(keyName), keyValue));
        Query query = session.createQuery(cr);
        List<T> result =  query.getResultList();
        var first = result.stream().findFirst();
        if(first.isEmpty()) {
            return null;
        } else {
            return first.get();
        }
    }

//    public int update() {
//
//    }
//
//    public int insert() {
//
//    }
//
//    public int delete() {
//
//    }

}

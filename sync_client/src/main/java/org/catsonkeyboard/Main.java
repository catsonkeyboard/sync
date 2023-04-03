package org.catsonkeyboard;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.catsonkeyboard.config.JpaEntityManagerFactory;
import org.catsonkeyboard.entities.Student;

public class Main {
    public static void main(String[] args) {
        EntityManager entityManager = new JpaEntityManagerFactory(
                new Class[]{Student.class}).getEntityManager();
        entityManager.getTransaction().begin();
        Student student = new Student();
//        student.setId(1L);
        student.setName("张三");
        student.setSex(true);
        entityManager.persist(student);
        entityManager.getTransaction().commit();
        entityManager.clear();
        Student foundStudent = entityManager.find(Student.class, 1L);
//        Query query = entityManager.createNativeQuery("select * from Student");
//        var result = query.getResultList();
    }
}
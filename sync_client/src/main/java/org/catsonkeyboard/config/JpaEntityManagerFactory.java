package org.catsonkeyboard.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.spi.PersistenceUnitInfo;
import org.h2.jdbcx.JdbcDataSource;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

public class JpaEntityManagerFactory {
    private final String DB_URL = "jdbc:h2:file:./db";
    private final String DB_USER_NAME = "admin";
    private final String DB_PASSWORD = "12345";
    private final Class[] entityClasses;

    public JpaEntityManagerFactory(Class[] entityClasses) {
        this.entityClasses = entityClasses;
    }

    public EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    protected EntityManagerFactory getEntityManagerFactory() {
        PersistenceUnitInfo persistenceUnitInfo = getPersistenceUnitInfo(getClass().getSimpleName());
        Map<String, Object> configuration = new HashMap<>();
        return new EntityManagerFactoryBuilderImpl(new PersistenceUnitInfoDescriptor(persistenceUnitInfo), configuration)
                .build();
    }

    protected HibernatePersistenceUnitInfo getPersistenceUnitInfo(String name) {
        return new HibernatePersistenceUnitInfo(name, getEntityClassNames(), getProperties());
    }

    protected List<String> getEntityClassNames() {
        return Arrays.asList(getEntities())
                .stream()
                .map(Class::getName)
                .collect(Collectors.toList());
    }

    protected Properties getProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.put("hibernate.id.new_generator_mappings", false);
        properties.put("hibernate.connection.datasource", getH2DataSource());
//        properties.put("javax.persistence.schema-generation.database.action","create");
        return properties;
    }

    protected Class[] getEntities() {
        return entityClasses;
    }

    protected DataSource getH2DataSource() {
        org.h2.jdbcx.JdbcDataSource h2DataSource = new JdbcDataSource();
        h2DataSource.setURL(DB_URL);
        h2DataSource.setUser(DB_USER_NAME);
        h2DataSource.setPassword(DB_PASSWORD);
        return h2DataSource;
    }
}

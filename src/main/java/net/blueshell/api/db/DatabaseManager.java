package net.blueshell.api.db;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManagerFactory;

@Component
public class DatabaseManager {

    private static SessionFactory sessionFactory;

    @Autowired
    public DatabaseManager(EntityManagerFactory entityManagerFactory) {
        if(entityManagerFactory.unwrap(SessionFactory.class) == null) {
            throw new NullPointerException("Factory is not a Hibernate factory");
        }
        sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
    }

    public static Session getSession() {
        return sessionFactory.openSession();
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}

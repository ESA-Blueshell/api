package net.blueshell.api.db;

import net.blueshell.api.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 12/12/2017.
 */
@Component
public class DatabaseManager {
    private static SessionFactory sessionFactory;
    private static Configuration config;


    public static void init() {
        Configuration configuration = new Configuration().configure();
        config = configuration;
        configuration.setProperty("autoReconnect", "true");
        Class[] dbClasses = new Class[] {
                Billable.class,
                Committee.class,
                Event.class,
                EventFeedback.class,
                Picture.class,
                Registration.class,
                Sponsor.class,
                News.class,
                User.class,
                CommitteeMembership.class,
        };
        for(Class clazz : dbClasses) {
            configuration.addAnnotatedClass(clazz);
        }
        sessionFactory = configuration.buildSessionFactory();
    }

    public static Session getSession() {
        return sessionFactory.openSession();
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static Configuration getConfig() {
        return config;
    }

    @Bean
    public static DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        org.hibernate.cfg.Configuration config = DatabaseManager.getConfig();
        dataSource.setDriverClassName(config.getProperty("connection.driver_class"));
        dataSource.setUrl(config.getProperty("hibernate.connection.url"));
        dataSource.setUsername(config.getProperty("hibernate.connection.username"));
        dataSource.setPassword(config.getProperty("hibernate.connection.password"));
        return dataSource;
    }
}

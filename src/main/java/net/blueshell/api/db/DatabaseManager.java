package net.blueshell.api.db;

import net.blueshell.api.business.committee.Committee;
import net.blueshell.api.business.committee.CommitteeMembership;
import net.blueshell.api.business.event.Event;
import net.blueshell.api.business.event.EventFeedback;
import net.blueshell.api.business.event.EventSignUp;
import net.blueshell.api.business.news.News;
import net.blueshell.api.business.picture.Picture;
import net.blueshell.api.business.sponsor.Sponsor;
import net.blueshell.api.business.user.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created on 12/12/2017.
 */
@Component
public class DatabaseManager {
    private static SessionFactory sessionFactory;
    private static Configuration config;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    public void init() {
        Configuration configuration = new Configuration()
                .setProperty("hibernate.connection.url", url)
                .setProperty("hibernate.connection.username", username)
                .setProperty("hibernate.connection.password", password)
                .setProperty("connection.driver_class", "com.mysql.cj.jdbc.Driver")
                .setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect")
                .setProperty("connection.pool_size", "100")
                .setProperty("connection.autoReconnect", "true")
                .setProperty("connection.autoReconnectForPools", "true")
                .setProperty("connection.is-connection-validation-required", "true")
                .setProperty("hibernate.enable_lazy_load_no_trans", "true")
                .setProperty("hibernate.show_sql", "false")
                .setProperty("hibernate.format_sql", "true")
                .setProperty("hibernate.use_sql_comments", "true");

        List.of(
                Committee.class,
                Event.class,
                EventSignUp.class,
                EventFeedback.class,
                Picture.class,
                Sponsor.class,
                News.class,
                User.class,
                CommitteeMembership.class
        ).forEach(configuration::addAnnotatedClass);

        config = configuration;
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
    public DataSource dataSource() {
        if (config == null || sessionFactory == null) {
            System.out.println("[DB] Init");
            init();
            System.out.println("[DB] Done");
        }

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        org.hibernate.cfg.Configuration config = DatabaseManager.getConfig();
        dataSource.setDriverClassName(config.getProperty("connection.driver_class"));
        dataSource.setUrl(config.getProperty("hibernate.connection.url"));
        dataSource.setUsername(config.getProperty("hibernate.connection.username"));
        dataSource.setPassword(config.getProperty("hibernate.connection.password"));
        return dataSource;
    }
}

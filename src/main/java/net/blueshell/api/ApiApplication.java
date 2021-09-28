package net.blueshell.api;

import net.blueshell.api.business.user.UserController;
import net.blueshell.api.db.DatabaseManager;
import net.blueshell.api.event.EventConstants;
import net.blueshell.api.event.EventManager;
import net.blueshell.api.storage.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.concurrent.TimeUnit;


@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class ApiApplication {

    public static void main(String[] args) {
        System.out.println("[DB] Init");
        DatabaseManager.init();
        System.out.println("[DB] Done");
        System.setProperty("server.servlet.context-path", "/api");
        SpringApplication.run(ApiApplication.class, args);
        initEvents();
    }

    private static void initEvents() {
        EventManager.addFixedRateEvent(UserController::clearCache, EventConstants.CLEAR_CACHE_HOUR_DELAY, EventConstants.CLEAR_CACHE_HOUR_DELAY, TimeUnit.HOURS);
    }

}

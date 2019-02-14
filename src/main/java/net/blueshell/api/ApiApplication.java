package net.blueshell.api;

import net.blueshell.api.db.DatabaseManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiApplication {

    public static void main(String[] args) {
        System.out.println("[DB] Init");
        DatabaseManager.init();
        System.out.println("[DB] Done");
        SpringApplication.run(ApiApplication.class, args);
    }

}


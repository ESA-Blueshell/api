package net.blueshell.api;

import net.blueshell.api.config.StorageConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableConfigurationProperties(StorageConfig.class)
@EnableJpaAuditing
@EnableAsync
@ComponentScan(basePackages = {"net.blueshell.api", "net.blueshell.common", "net.blueshell.db"})
public class ApiApplication {

    public static void main(String[] args) {
        System.setProperty("server.servlet.context-path", "/api");
        SpringApplication.run(ApiApplication.class, args);
    }
}

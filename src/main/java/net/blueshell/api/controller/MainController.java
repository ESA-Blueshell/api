package net.blueshell.api.controller;

import lombok.extern.slf4j.Slf4j;
import net.blueshell.common.identity.UserDetailsProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class MainController extends UserDetailsProvider {
    @GetMapping("/health")
    public Boolean healthCheck() {
        return true;
    }
}

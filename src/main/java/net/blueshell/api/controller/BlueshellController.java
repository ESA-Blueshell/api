package net.blueshell.api.controller;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public class BlueshellController {

    protected User getPrincipal() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    protected String getAuthorizedUsername() {
        return getPrincipal().getUsername();
    }

    protected boolean hasAuthorization(String authorization) {
        User principal = getPrincipal();
        return principal.getAuthorities().stream().anyMatch(ga -> ga.getAuthority().equalsIgnoreCase(authorization));
    }
}

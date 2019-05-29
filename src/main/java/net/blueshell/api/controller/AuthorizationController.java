package net.blueshell.api.controller;

import net.blueshell.api.model.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AuthorizationController {

    protected User getPrincipal() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    protected String getAuthorizedUsername() {
        return getPrincipal().getUsername();
    }

    protected boolean isAuthedForUser(net.blueshell.api.model.User user) {
        return hasAuthorization(Role.ADMIN)
                || hasAuthorization(Role.MEMBER) && getAuthorizedUsername().equalsIgnoreCase(user.getUsername());
    }

    protected boolean hasAuthorization(Role role) {
        return getPrincipal().getAuthorities()
                .stream()
                .anyMatch(r -> Role.valueOf(r.toString())
                .matchesRole(role));
    }

    public Set<Role> getRoles() {
        return getPrincipal().getAuthorities()
                .stream()
                .map(ga -> Role.valueOf(ga.toString()))
                .collect(Collectors.toCollection(HashSet::new));
    }
}

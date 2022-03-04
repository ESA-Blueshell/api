package net.blueshell.api.controller;

import net.blueshell.api.business.user.User;
import net.blueshell.api.business.user.UserDao;
import net.blueshell.api.business.user.Role;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AuthorizationController {

    protected User getPrincipal() {
        Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (obj instanceof User) {
            return (User) obj;
        }
        return null;
    }

    protected String getAuthorizedUsername() {
        if (getPrincipal() == null) {
            return null;
        }
        return getPrincipal().getUsername();
    }

    protected boolean isAuthedForUser(net.blueshell.api.business.user.User user) {
        return hasAuthorization(Role.ADMIN)
                || hasAuthorization(Role.MEMBER) && getAuthorizedUsername().equalsIgnoreCase(user.getUsername());
    }

    protected boolean hasAuthorization(Role role) {
        if (getPrincipal() == null) {
            return false;
        }
        return getPrincipal().getAuthorities()
                .stream()
                .anyMatch(r -> Role.valueOf(r.toString())
                .matchesRole(role));
    }

    public Set<Role> getRoles() {
        if (getPrincipal() == null) {
            return new HashSet<>();
        }
        return getPrincipal().getAuthorities()
                .stream()
                .map(ga -> Role.valueOf(ga.toString()))
                .collect(Collectors.toCollection(HashSet::new));
    }
}

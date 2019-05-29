package net.blueshell.api.controller;

import net.blueshell.api.daos.UserDao;
import net.blueshell.api.model.Role;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AuthorizationController {

    private final UserDao userDao = new UserDao();

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

    protected boolean isAuthedForUser(net.blueshell.api.model.User user) {
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

    protected net.blueshell.api.model.User getAuthedUser() {
        if (getPrincipal() == null) {
            return null;
        }
        return userDao.getByUsername(getAuthorizedUsername());
    }
}

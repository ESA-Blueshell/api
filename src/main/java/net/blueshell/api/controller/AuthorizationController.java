package net.blueshell.api.controller;

import net.blueshell.api.business.user.UserDao;
import net.blueshell.api.business.user.Role;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AuthorizationController {

    private static final Map<User, net.blueshell.api.business.user.User> principalToUsers = new HashMap<>();

    private static final UserDao userDao = new UserDao();

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

    protected net.blueshell.api.business.user.User getAuthedUser() {
        var principal = getPrincipal();
        if (principal == null) {
            return null;
        }

        return getUserByPrincipal(principal);
    }

    private net.blueshell.api.business.user.User getUserByPrincipal(User principal) {
        if (principalToUsers.containsKey(principal)) {
            return principalToUsers.get(principal);
        }

        var user = userDao.getByUsername(getAuthorizedUsername());
        principalToUsers.put(principal, user);
        return user;
    }

    public static void clearCache() {
        principalToUsers.clear();
    }
}

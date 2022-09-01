package net.blueshell.api.controller;

import net.blueshell.api.business.user.Role;
import net.blueshell.api.business.user.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.Set;

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

    protected boolean isAuthedForUser(User user) {
        return hasAuthorization(Role.ADMIN)
                || getAuthorizedUsername().equalsIgnoreCase(user.getUsername());
    }

    protected boolean hasAuthorization(Role role) {
        if (getPrincipal() == null) {
            return false;
        }
        return getPrincipal().hasRole(role);
    }

    public Set<Role> getRoles() {
        if (getPrincipal() == null) {
            return new HashSet<>();
        }
        return getPrincipal().getRoles();
    }
}

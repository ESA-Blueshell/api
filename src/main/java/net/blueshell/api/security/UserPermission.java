package net.blueshell.api.security;

import net.blueshell.api.common.enums.Role;
import net.blueshell.api.model.Event;
import net.blueshell.api.model.User;
import net.blueshell.api.service.EventService;
import net.blueshell.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class UserPermission extends BasePermissionEvaluator<User, Long, UserService> {

    @Autowired
    public UserPermission(UserService service) {
        super(service);
    }

    public boolean hasPermission(Authentication authentication, Object object, String permission) {
        if (authentication == null || object == null || permission == null) {
            return false;
        }
        User user = (User) object;
        User principal = (User) authentication.getPrincipal();
        return switch (permission) {
            case "read", "write" -> principal.hasRole(Role.BOARD) || principal.getId() == user.getId();
            default -> false;
        };
    }

    public boolean hasPermissionId(Authentication authentication, Object targetId, String permission) {
        if (authentication == null || targetId == null || permission == null) {
            return false;
        }

        User targetUser = service.findById((Long) targetId);
        return targetUser != null && hasPermission(authentication, targetUser, permission);
    }

}

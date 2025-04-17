package net.blueshell.api.permission;

import lombok.extern.slf4j.Slf4j;
import net.blueshell.common.enums.Role;
import net.blueshell.api.model.User;
import net.blueshell.common.identity.SharedUserDetails;
import net.blueshell.db.permission.BasePermissionEvaluator;
import net.blueshell.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
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
        SharedUserDetails principal = getPrincipal();
        return switch (permission) {
            case "read", "write", "delete" -> principal.hasRole(Role.BOARD) || principal.getId() == user.getId();
            case "changeRole", "getBrevo" -> principal.hasRole(Role.BOARD);
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

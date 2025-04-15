package net.blueshell.api.permission;

import net.blueshell.api.common.enums.Role;
import net.blueshell.api.model.Event;
import net.blueshell.api.model.EventSignUp;
import net.blueshell.api.model.Guest;
import net.blueshell.api.model.User;
import net.blueshell.api.permission.base.BasePermissionEvaluator;
import net.blueshell.api.repository.EventRepository;
import net.blueshell.api.repository.GuestRepository;
import net.blueshell.api.service.GuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class GuestPermission extends BasePermissionEvaluator<Guest, Long, GuestService> {


    @Autowired
    public GuestPermission(GuestService service) {
        super(service);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, String permission) {
        if (authentication == null || targetDomainObject == null || permission == null) {
            return false;
        }

        Guest guest = (Guest) targetDomainObject;

        EventSignUp signUp = guest.getEventSignUp();
        return switch (permission) {
            case "read", "write", "delete" -> signUp != null || (getPrincipal() != null && hasAuthority(Role.BOARD));
            default -> false;
        };
    }

    @Override
    public boolean hasPermissionId(Authentication authentication, Object accessToken, String permission) {
        if (authentication == null || accessToken == null || permission == null) {
            return false;
        }
        Guest guest = service.findByAccessToken((String) accessToken);
        return guest != null && hasPermission(authentication, guest, permission);
    }
}

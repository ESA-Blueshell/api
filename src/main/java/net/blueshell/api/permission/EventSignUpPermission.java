package net.blueshell.api.permission;

import net.blueshell.api.common.enums.Role;
import net.blueshell.api.model.EventSignUp;
import net.blueshell.api.model.User;
import net.blueshell.api.permission.base.BasePermissionEvaluator;
import net.blueshell.api.service.EventSignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class EventSignUpPermission extends BasePermissionEvaluator<EventSignUp, Long, EventSignUpService> {

    @Autowired
    public EventSignUpPermission(EventSignUpService service) {
        super(service);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, String permission) {
        if (authentication == null || targetDomainObject == null || permission == null) {
            return false;
        }

        EventSignUp signUp = (EventSignUp) targetDomainObject;
        System.out.println("has permission: " + signUp.getId());
        if (getPrincipal() == null) {
            return false;
        }
        System.out.println("has Authority: " + hasAuthority(Role.BOARD));
        System.out.println("signUp.getUser().equals(getPrincipal()): " + signUp.getUser().equals(getPrincipal()));

        return switch (permission) {
            case "read" -> hasAuthority(Role.BOARD) || signUp.getUser().equals(getPrincipal()) || signUp.getEvent().getCommittee().hasMember(getPrincipal());
            case "write", "delete" -> hasAuthority(Role.BOARD) || signUp.getUser().equals(getPrincipal());
            default -> false;
        };
    }

    @Override
    public boolean hasPermissionId(Authentication authentication, Object targetId, String permission) {
        if (authentication == null || targetId == null || permission == null) {
            return false;
        }
        System.out.println("service find by id: " + targetId);
        EventSignUp signUp = service.findById((Long) targetId);
        System.out.println("found signup: " + signUp.getId());
        return signUp != null && hasPermission(authentication, signUp, permission);
    }
}
package net.blueshell.api.security;

import net.blueshell.api.common.enums.Role;
import net.blueshell.api.model.EventSignUp;
import net.blueshell.api.model.User;
import net.blueshell.api.security.base.BasePermissionEvaluator;
import net.blueshell.api.service.EventSignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class EventSignupPermission extends BasePermissionEvaluator<EventSignUp, Long, EventSignUpService> {

    @Autowired
    public EventSignupPermission(EventSignUpService service) {
        super(service);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, String permission) {
        if (authentication == null || targetDomainObject == null || permission == null) {
            return false;
        }
        EventSignUp signUp = (EventSignUp) targetDomainObject;
        User principal = (User) authentication.getPrincipal();
        if (principal == null) {
            return false;
        }
        return switch (permission) {
            case "read" -> principal.hasRole(Role.BOARD) || signUp.getUser().equals(principal) || signUp.getEvent().getCommittee().hasMember(principal);
            case "write", "delete" -> principal.hasRole(Role.BOARD) || signUp.getUser().equals(principal);
            default -> false;
        };
    }

    @Override
    public boolean hasPermissionId(Authentication authentication, Object targetId, String permission) {
        if (authentication == null || targetId == null || permission == null) {
            return false;
        }
        EventSignUp signUp = service.findById((Long) targetId);
        return signUp != null && hasPermission(authentication, signUp, permission);
    }
}
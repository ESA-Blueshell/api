package net.blueshell.api.permission;

import net.blueshell.api.common.enums.Role;
import net.blueshell.api.model.Event;
import net.blueshell.api.model.User;
import net.blueshell.api.permission.base.BasePermissionEvaluator;
import net.blueshell.api.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class EventPermission extends BasePermissionEvaluator<Event, Long, EventService> {

    @Autowired
    public EventPermission(EventService service) {
        super(service);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, String permission) {
        if (authentication == null || targetDomainObject == null || permission == null) {
            return false;
        }
        Event event = (Event) targetDomainObject;
        User principal = (User) authentication.getPrincipal();
        return switch (permission) {
            case "read" -> event.isVisible() || principal.hasRole(Role.BOARD) || event.getCommittee().hasMember(principal);
            case "write", "delete" -> principal.hasRole(Role.BOARD) || event.getCommittee().hasMember(principal);
            case "signUp" -> canSignUp(event, principal);
            default -> false;
        };
    }

    private boolean canSignUp(Event event, User user) {
        if (!event.isVisible()) return false;
        return !event.isMembersOnly() || user.hasRole(Role.MEMBER);
    }

    @Override
    public boolean hasPermissionId(Authentication authentication, Object targetId, String permission) {
        if (authentication == null || targetId == null || permission == null) {
            return false;
        }
        Event event = service.findById((Long) targetId);
        return event != null && hasPermission(authentication, event, permission);
    }
}
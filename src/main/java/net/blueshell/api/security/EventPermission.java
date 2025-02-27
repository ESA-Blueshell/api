package net.blueshell.api.security;

import net.blueshell.api.common.enums.Role;
import net.blueshell.api.model.Event;
import net.blueshell.api.model.User;
import net.blueshell.api.repository.EventRepository;
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
    boolean canSee(Event event, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return event.isVisible() || user.hasRole(Role.BOARD) || event.getCommittee().hasMember(user);
    }

    @Override
    boolean canEdit(Event event, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return user.hasRole(Role.BOARD) || event.getCommittee().hasMember(user);
    }

    @Override
    boolean canDelete(Event event, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return user.hasRole(Role.BOARD) || event.getCommittee().hasMember(user);
    }

    boolean canSignUp(Event event, Authentication authentication) {
        if (!event.isVisible()) return false;
        if (!event.isMembersOnly()) return true;

        User user = (User) authentication.getPrincipal();
        return user.hasRole(Role.MEMBER);
    }

    @Override
    protected void registerPermissions() {
        super.registerPermissions();
        permissionsMap.put("signUp", this::canSignUp);
    }
}

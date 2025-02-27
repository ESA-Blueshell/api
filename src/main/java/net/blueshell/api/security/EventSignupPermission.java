package net.blueshell.api.security;

import net.blueshell.api.common.enums.Role;
import net.blueshell.api.model.Event;
import net.blueshell.api.model.EventSignUp;
import net.blueshell.api.model.User;
import net.blueshell.api.repository.EventSignUpRepository;
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
    boolean canSee(EventSignUp signUp, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Event event = signUp.getEvent();
        return user.hasRole(Role.BOARD) || signUp.getUser() == user || event.getCommittee().hasMember(user);
    }

    @Override
    boolean canEdit(EventSignUp signUp, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return user.hasRole(Role.BOARD) || signUp.getUser() == user;
    }

    @Override
    boolean canDelete(EventSignUp signUp, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return user.hasRole(Role.BOARD) || signUp.getUser() == user;
    }
}

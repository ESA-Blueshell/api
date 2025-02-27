package net.blueshell.api.security;

import net.blueshell.api.common.enums.Role;
import net.blueshell.api.model.Event;
import net.blueshell.api.model.Guest;
import net.blueshell.api.model.User;
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
    boolean canSee(Guest guest, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
//        Event event = signUp.getEvent();
//        return user.hasRole(Role.BOARD) || signUp.getUser() == user || event.getCommittee().hasMember(user);
        return true;
    }

    @Override
    boolean canEdit(Guest guest, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
//        return user.hasRole(Role.BOARD) || signUp.getUser() == user;
        return true;
    }

    @Override
    boolean canDelete(Guest guest, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
//        return user.hasRole(Role.BOARD) || signUp.getUser() == user;
        return true;
    }
}

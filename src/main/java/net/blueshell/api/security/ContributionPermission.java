package net.blueshell.api.security;

import net.blueshell.api.common.enums.Role;
import net.blueshell.api.model.Contribution;
import net.blueshell.api.model.User;
import net.blueshell.api.service.ContributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class ContributionPermission extends BasePermissionEvaluator<Contribution, Long, ContributionService> {

    @Autowired
    public ContributionPermission(ContributionService service) {
        super(service);
    }

    @Override
    boolean canSee(Contribution contribution, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return user.hasRole(Role.BOARD) || contribution.getUser().equals(user);
    }

    @Override
    boolean canEdit(Contribution contribution, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return user.hasRole(Role.BOARD);
    }

    @Override
    boolean canDelete(Contribution contribution, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return user.hasRole(Role.ADMIN);
    }
}

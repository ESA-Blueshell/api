package net.blueshell.api.permission;

import net.blueshell.enums.Role;
import net.blueshell.api.model.Contribution;
import net.blueshell.identity.Identity;
import net.blueshell.permission.BasePermissionEvaluator;
import net.blueshell.api.service.ContributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ContributionPermission extends BasePermissionEvaluator<Contribution, Long, ContributionService> {

    @Autowired
    public ContributionPermission(ContributionService service) {
        super(service);
    }

    public boolean hasPermission(Authentication authentication, Object object, String permission) {
        if (authentication == null || object == null || permission == null) {
            return false;
        }
        Contribution contribution = (Contribution) object;
        Identity principal = getPrincipal();
        return switch (permission) {
            case "read", "write" -> principal.hasRole(Role.BOARD) || Objects.equals(principal.getId(), contribution.getUserId());
            default -> false;
        };
    }

    public boolean hasPermissionId(Authentication authentication, Object targetId, String permission) {
        if (authentication == null || targetId == null || permission == null) {
            return false;
        }

        Contribution targetContribution = service.findById((Long) targetId);
        return targetContribution != null && hasPermission(authentication, targetContribution, permission);
    }
}

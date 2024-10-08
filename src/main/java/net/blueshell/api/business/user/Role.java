package net.blueshell.api.business.user;

import java.util.*;
import java.util.stream.Collectors;

public enum Role {
    GUEST("GUEST"),
    COMPANY("COMPANY"),
    MEMBER("MEMBER", GUEST),
    VEGAN("VEGAN"),
    COMMITTEE("COMMITTEE", MEMBER),
    BOARD("BOARD", COMMITTEE),
    TREASURER("TREASURER", BOARD),
    ADMIN("ADMIN", TREASURER),
    ;

    private String reprString;
    private Role[] inheritedRoles;

    Role(String reprString, Role... inheritedRoles) {
        this.reprString = reprString;
        this.inheritedRoles = inheritedRoles;
    }

    public String getReprString() {
        return reprString;
    }

    public boolean matchesRole(Role role) {
        return role == this || Arrays.stream(inheritedRoles).anyMatch(r -> r.matchesRole(role));
    }

    public Role[] getInheritedRoles() {
        return inheritedRoles;
    }

    /**
     * Depth- (or breadth idk) first search for all inherited roles of this Role.
     */
    public Set<Role> getAllInheritedRoles() {
        Set<Role> res = new HashSet<>();
        res.add(this);
        ArrayDeque<Role> unexplored = new ArrayDeque<>(List.of(inheritedRoles));
        while (!unexplored.isEmpty()) {
            Role currentRole = unexplored.remove();
            res.add(currentRole);
            unexplored.addAll(Arrays.stream(currentRole.inheritedRoles).filter(role -> !res.contains(role)).collect(Collectors.toList()));
        }
        return res;
    }
}

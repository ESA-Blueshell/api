package net.blueshell.api.business.user;

import java.util.Arrays;

public enum Role {
    GUEST("GUEST"),
    COMPANY("COMPANY"),
    MEMBER("MEMBER", GUEST),
    VEGAN("VEGAN", MEMBER),
    EHBO("EHBO", MEMBER),
    BHV("BHV", MEMBER),
    BOARD("BOARD", MEMBER),
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
}

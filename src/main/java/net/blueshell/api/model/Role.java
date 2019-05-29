package net.blueshell.api.model;

import java.util.Arrays;

public enum Role {
    GUEST("Guest"),
    MEMBER("Member", GUEST),
    VEGAN("Vegan", MEMBER),
    BHV("BHV", MEMBER),
    EHBO("EHBO", MEMBER),
    BOARD("Board", MEMBER),
    COMPANY("Company", MEMBER),
    TREASURER("Treasurer", BOARD),
    ADMIN("Admin", TREASURER),
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
}

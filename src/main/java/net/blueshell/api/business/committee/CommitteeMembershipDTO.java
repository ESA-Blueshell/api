package net.blueshell.api.business.committee;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.blueshell.api.business.user.SimpleUserDTO;

public class CommitteeMembershipDTO {

    @JsonProperty("role")
    private String role;

    @JsonProperty("user")
    private SimpleUserDTO user;

    private CommitteeMembershipDTO() {
    }

    public CommitteeMembership toMembership() {
        CommitteeMembership res = new CommitteeMembership();
        res.setRole(role);
        res.setUser(user.toUser());
        return res;
    }

    public static CommitteeMembershipDTO fromCommittee(CommitteeMembership membership) {
        CommitteeMembershipDTO res = new CommitteeMembershipDTO();
        res.role = membership.getRole();
        res.user = SimpleUserDTO.fromUser(membership.getUser());
        return res;
    }
}


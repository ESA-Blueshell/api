package net.blueshell.api.business.committee;

import net.blueshell.api.business.user.User;

import java.io.Serializable;

public class CommitteeMembershipId implements Serializable {

    private User user;

    private Committee committee;

    public CommitteeMembershipId() {
    }

    public CommitteeMembershipId(User user, Committee committee) {
        this.user = user;
        this.committee = committee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommitteeMembershipId that = (CommitteeMembershipId) o;

        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        return committee != null ? committee.equals(that.committee) : that.committee == null;
    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (committee != null ? committee.hashCode() : 0);
        return result;
    }
}

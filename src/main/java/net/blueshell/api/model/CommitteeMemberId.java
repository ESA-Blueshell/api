package net.blueshell.api.model;

import java.io.Serializable;
import java.util.Objects;

public class CommitteeMemberId implements Serializable {

    private User user;
    private Committee committee;

    public CommitteeMemberId() {
    }

    public CommitteeMemberId(User user, Committee committee) {
        this.user = user;
        this.committee = committee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommitteeMemberId that = (CommitteeMemberId) o;

        if (!Objects.equals(user, that.user)) return false;
        return Objects.equals(committee, that.committee);
    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (committee != null ? committee.hashCode() : 0);
        return result;
    }
}

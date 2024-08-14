package net.blueshell.api.business.committee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.blueshell.api.business.user.User;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "committee_members")
@Data
@IdClass(CommitteeMembershipId.class)
public class CommitteeMembership implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "committee_id")
    @JsonIgnore
    private Committee committee;

    private String role;

    public CommitteeMembership() {
    }

    @JsonProperty("user")
    public long getUserId() {
        return getUser() == null ? 0 : getUser().getId();
    }

    @JsonProperty("committee")
    public long getCommitteeId() {
        return getCommittee() == null ? 0 : getCommittee().getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommitteeMembership that = (CommitteeMembership) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(committee, that.committee);
    }

    @Override
    public String toString() {
        return String.format("CommitteeMembership={userId: %d, committee: %d, role: %s}", user.getId(), committee.getId(), role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, committee);
    }
}

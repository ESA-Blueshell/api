package net.blueshell.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "committee_members")
@Data
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
    public int hashCode() {
        return Objects.hash(user, committee);
    }
}

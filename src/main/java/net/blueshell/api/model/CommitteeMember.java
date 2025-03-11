package net.blueshell.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import net.blueshell.api.base.BaseModel;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "committee_members")
@Data
@IdClass(CommitteeMemberId.class)
@SQLDelete(sql = "UPDATE committee_members SET deleted_at = CURRENT_TIMESTAMP WHERE user_id = ? AND committee_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class CommitteeMember implements BaseModel {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "user_id")
    private Long userId;

    @Id
    @ManyToOne
    @JoinColumn(name = "committee_id", insertable = false, updatable = false)
    private Committee committee;

    @Column(name = "committee_id")
    private Long committeeId;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    private String role;

    public CommitteeMember() {
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

        CommitteeMember that = (CommitteeMember) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(committee, that.committee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, committee);
    }

    @Override
    public String toString() {
        return String.format("CommitteeMember={userId: %d, committeeId: %d, role: %s}",
                userId, committeeId, role);
    }
}

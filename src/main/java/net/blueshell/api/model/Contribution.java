package net.blueshell.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import net.blueshell.api.base.BaseModel;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.sql.Timestamp;

@Entity
@Table(name = "contributions")
@Data
@SQLDelete(sql = "UPDATE contributions SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Contribution implements BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    private User user;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "paid")
    private Boolean paid;

    @Column(name = "reminded_at")
    private Timestamp remindedAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "contribution_period_id")
    private ContributionPeriod contributionPeriod;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    public Contribution(User user, ContributionPeriod contributionPeriod) {
        this.user = user;
        this.contributionPeriod = contributionPeriod;
    }

    public Contribution() {
    }

    @JsonProperty("userId")
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }
}

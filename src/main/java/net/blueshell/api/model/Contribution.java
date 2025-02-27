package net.blueshell.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.blueshell.api.base.BaseModel;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.sql.Timestamp;

@Entity
@Table(name = "contributions")
@Getter
@SQLDelete(sql = "UPDATE contributions SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Contribution implements BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    @Setter
    @JsonIgnore
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "membership_id")
    @Setter
    @JsonIgnore
    private Membership membership;

    @Column(name = "paid")
    @Setter
    private Boolean paid;

    @Column(name = "reminded_at")
    @Setter
    private Timestamp remindedAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "contribution_period_id")
    @Setter
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

package net.blueshell.api.business.contribution;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import net.blueshell.api.business.payment.Payment;
import net.blueshell.api.business.payment.PaymentSource;
import net.blueshell.api.business.user.User;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "contributions")
@Getter
@Where(clause = "deleted_at IS NULL")
public class Contribution implements PaymentSource {
    // Getters and setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    @Setter
    @JsonIgnore
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "contribution_period_id")
    @Setter
    @JsonIgnore
    private ContributionPeriod contributionPeriod;

    @Column(name="paid")
    @Setter
    private boolean paid = false;

    public Contribution(User user, ContributionPeriod contributionPeriod) {
        this.user = user;
        this.contributionPeriod = contributionPeriod;
    }
    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @JsonProperty("userId")
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }


    public Contribution() {
    }
}

package net.blueshell.api.business.contribution;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import net.blueshell.api.business.user.User;

import javax.persistence.*;

@Entity
@Table(name = "contributions")
@Getter
public class Contribution {
    // Getters and setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name= "user_id")
    @Setter
    @JsonIgnore
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name= "contribution_period_id")
    @Setter
    private ContributionPeriod contributionPeriod;

    @Column(name="paid")
    @Setter
    private boolean paid = false;

    public Contribution(User user, ContributionPeriod contributionPeriod) {
        this.user = user;
        this.contributionPeriod = contributionPeriod;
    }

    public Contribution() {}
}

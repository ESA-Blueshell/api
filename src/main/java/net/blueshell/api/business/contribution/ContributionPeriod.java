package net.blueshell.api.business.contribution;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Entity
@Table(name = "contribution_periods")
@Where(clause = "deleted_at IS NULL")
public class ContributionPeriod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "contributionPeriod", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Contribution> contributions;

    @Column(name="start_date")
    @Setter
    private LocalDate startDate;

    @Column(name="end_date")
    @Setter
    private LocalDate endDate;

    @Column(name="half_year_fee")
    @Setter
    private double halfYearFee;

    @Column(name="full_year_fee")
    @Setter
    private double fullYearFee;

    @Column(name="alumni_fee")
    @Setter
    private double alumniFee;

    @Column(name="list_id")
    @Setter
    private Long listId;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;
}

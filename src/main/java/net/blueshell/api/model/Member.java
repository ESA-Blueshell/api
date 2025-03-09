package net.blueshell.api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import net.blueshell.api.common.enums.MemberType;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.sql.Date;
import java.util.Set;

@Entity
@Table(name = "members")
@SQLDelete(sql = "UPDATE members SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Data
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JoinColumn(name = "user_id")
    @OneToOne
    @ToString.Exclude
    private User user;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private MemberType type;

    @JoinColumn(name = "signature_id")
    @OneToOne
    private File signature;

    @OneToMany(mappedBy = "member")
    private Set<Contribution> contributions;

    @Column(name = "incasso", nullable = false)
    private boolean incasso;
}

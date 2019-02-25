package net.blueshell.api.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "registrations")
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private RegistrationState registrationState;

    @OneToOne
    @JoinColumn(name = "accepted_by_user_id")
    private User acceptBy;

    @Column(name = "created_at")
    private Timestamp createdAt;
}

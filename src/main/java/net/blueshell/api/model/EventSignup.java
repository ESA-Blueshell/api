package net.blueshell.api.model;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "event_signups")
public class EventSignup implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    private String options;

    @Column(name = "signed_up_at")
    private Timestamp signupDate;

}

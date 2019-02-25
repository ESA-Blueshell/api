package net.blueshell.api.model;

import javax.persistence.*;

@Entity
@Table(name = "event_feedback")
public class EventFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String feedback;

    @OneToOne
    @JoinColumn(name = "event_id")
    private Event event;
}

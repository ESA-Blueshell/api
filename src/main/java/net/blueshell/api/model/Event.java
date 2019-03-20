package net.blueshell.api.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "events")
public class Event {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @OneToOne
    @JoinColumn(name = "last_editor_id")
    private User lastEditor;

    @OneToOne
    @JoinColumn(name = "committee_id")
    private Committee committee;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private Visibility visibilty;

    private String location;

    @Column(name = "start_time")
    private Timestamp startTime;

    @OneToOne
    @JoinColumn(name = "banner_id")
    private Picture banner;

    @Column(name = "price_member")
    private double memberPrice;

    @Column(name = "price_pulic")
    private double publicPrice;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "event")
    private Set<EventFeedback> feedbacks;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "event")
    private Set<Billable> billables;
}

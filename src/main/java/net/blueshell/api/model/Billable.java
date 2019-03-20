package net.blueshell.api.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "billables")
public class Billable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "source_id")
    private User source;

    private String description;

    private int quantity;

    private double price;

    private boolean paid;

    @OneToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "created_at")
    private Timestamp createdAt;

}

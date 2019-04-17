package net.blueshell.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "billables")
@Data
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
    @JsonIgnore
    private Event event;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @JsonProperty("event")
    public long getEventId() {
        Event event = getEvent();
        return event == null ? 0 : event.getId();
    }


}

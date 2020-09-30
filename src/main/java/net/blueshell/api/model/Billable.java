package net.blueshell.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "billables")
@Data
public class Billable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "source_id")
    @JsonIgnore
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

    public Billable() {
    }

    @JsonProperty("source")
    public long getSourceId() {
        return getSource() == null ? 0 : getSource().getId();
    }

    @JsonProperty("event")
    public long getEventId() {
        Event event = getEvent();
        return event == null ? 0 : event.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Billable billable = (Billable) o;
        return id == billable.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

package net.blueshell.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "event_feedback")
@Data
public class EventFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String feedback;

    @OneToOne
    @JoinColumn(name = "event_id")
    @JsonIgnore
    private Event event;

    @JsonProperty("event")
    public long getEventId() {
        return getEvent() == null ? 0 : getEvent().getId();
    }
}

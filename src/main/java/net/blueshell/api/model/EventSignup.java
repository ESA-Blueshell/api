package net.blueshell.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "event_signups")
@Data
public class EventSignup implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "event_id")
    @JsonIgnore
    private Event event;

    private String options;

    @Column(name = "signed_up_at")
    private Timestamp signupDate;

    @JsonProperty("user")
    public long getUserId() {
        return getUser() == null ? 0 : getUser().getId();
    }

    @JsonProperty("event")
    public long getEventId() {
        return getEvent() == null ? 0 : getUser().getId();
    }

}

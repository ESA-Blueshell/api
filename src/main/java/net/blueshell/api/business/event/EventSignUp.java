package net.blueshell.api.business.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.blueshell.api.business.user.User;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "event_signups")
@Data
@IdClass(EventSignUpId.class)
public class EventSignUp implements Serializable {

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

    @Column(name = "options")
    private String options;

    @Column(name = "signed_up_at")
    private LocalDateTime signedUpAt;

    public EventSignUp() {
    }

    public EventSignUp(User user, Event event, String options, LocalDateTime signedUpAt) {
        this.user = user;
        this.event = event;
        this.options = options;
        this.signedUpAt = signedUpAt;
    }

    @JsonProperty("user")
    public long getUserId() {
        return getUser() == null ? 0 : getUser().getId();
    }

    @JsonProperty("event")
    public long getEventId() {
        return getEvent() == null ? 0 : getEvent().getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventSignUp that = (EventSignUp) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(event, that.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, event);
    }
}

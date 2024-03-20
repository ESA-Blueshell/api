package net.blueshell.api.business.eventsignups;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.blueshell.api.business.event.Event;
import net.blueshell.api.business.guest.Guest;
import net.blueshell.api.business.user.User;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "event_signups")
@Data
public class EventSignUp implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    @JsonIgnore
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @OneToOne
    @JoinColumn(name = "guest_id")
    @JsonIgnore
    private Guest guest;

    @Column(name = "form_answers")
    private String formAnswers;

    @Column(name = "signed_up_at")
    private LocalDateTime signedUpAt;

    public EventSignUp() {
    }

    public EventSignUp(Event event, User user, Guest guest, String formAnswers, LocalDateTime signedUpAt) {
        this.user = user;
        this.guest = guest;
        this.event = event;
        this.formAnswers = formAnswers;
        this.signedUpAt = signedUpAt;
    }

    public EventSignUp(long id, Event event, User user, Guest guest, String formAnswers, LocalDateTime signedUpAt) {
        this.id = id;
        this.user = user;
        this.guest = guest;
        this.event = event;
        this.formAnswers = formAnswers;
        this.signedUpAt = signedUpAt;
    }

    @JsonProperty("event")
    public Long getEventId() {
        return getEvent() == null ? null : getEvent().getId();
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

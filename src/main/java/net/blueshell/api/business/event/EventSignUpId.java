package net.blueshell.api.business.event;

import net.blueshell.api.business.user.User;

import java.io.Serializable;

public class EventSignUpId implements Serializable {

    private User user;

    private Event event;

    public EventSignUpId() {
    }

    public EventSignUpId(User user, Event event) {
        this.user = user;
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventSignUpId that = (EventSignUpId) o;

        if (!user.equals(that.user)) return false;
        return event.equals(that.event);
    }

    @Override
    public int hashCode() {
        int result = user.hashCode();
        result = 31 * result + event.hashCode();
        return result;
    }
}

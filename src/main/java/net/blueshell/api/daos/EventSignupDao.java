package net.blueshell.api.daos;

import net.blueshell.api.db.SessionWrapper;
import net.blueshell.api.model.EventSignup;

public class EventSignupDao extends SessionWrapper<EventSignup> implements Dao<EventSignup> {

    public EventSignupDao() {
        super(EventSignup.class);
    }
}

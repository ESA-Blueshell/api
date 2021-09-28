package net.blueshell.api.business.event;

import net.blueshell.api.daos.Dao;
import net.blueshell.api.db.SessionWrapper;

public class EventSignupDao extends SessionWrapper<EventSignup> implements Dao<EventSignup> {

    public EventSignupDao() {
        super(EventSignup.class);
    }
}

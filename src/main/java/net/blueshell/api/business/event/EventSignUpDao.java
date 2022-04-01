package net.blueshell.api.business.event;

import net.blueshell.api.daos.Dao;
import net.blueshell.api.db.SessionWrapper;

public class EventSignUpDao extends SessionWrapper<EventSignUp> implements Dao<EventSignUp> {

    public EventSignUpDao() {
        super(EventSignUp.class);
    }
}

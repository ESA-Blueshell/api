package net.blueshell.api.daos;

import net.blueshell.api.db.SessionWrapper;
import net.blueshell.api.model.Event;

public class EventDao extends SessionWrapper<Event> implements Dao<Event>  {

    public EventDao() {
        super(Event.class);
    }
}

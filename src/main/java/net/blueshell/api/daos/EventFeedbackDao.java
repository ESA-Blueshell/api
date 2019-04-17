package net.blueshell.api.daos;

import net.blueshell.api.db.SessionWrapper;
import net.blueshell.api.model.EventFeedback;

public class EventFeedbackDao extends SessionWrapper<EventFeedback> implements Dao<EventFeedback> {

    public EventFeedbackDao() {
        super(EventFeedback.class);
    }

}

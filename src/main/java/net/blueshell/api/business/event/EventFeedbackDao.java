package net.blueshell.api.business.event;

import net.blueshell.api.daos.Dao;
import net.blueshell.api.db.SessionWrapper;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("dataSource")
public class EventFeedbackDao extends SessionWrapper<EventFeedback> implements Dao<EventFeedback> {

    public EventFeedbackDao() {
        super(EventFeedback.class);
    }

}

package net.blueshell.api.business.event;

import net.blueshell.api.db.AbstractDAO;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("dataSource")
public class EventFeedbackDao extends AbstractDAO<EventFeedback>
{

    public EventFeedbackDao() {
    }

}

package net.blueshell.api.business.event;

import net.blueshell.api.db.AbstractDAO;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("dataSource")
public class EventDao extends AbstractDAO<Event>
{

    public EventDao() {
    }
}

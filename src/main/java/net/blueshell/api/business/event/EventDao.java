package net.blueshell.api.business.event;

import net.blueshell.api.db.AbstractDAO;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class EventDao extends AbstractDAO<Event>
{

    public EventDao() {
    }

    public Event getById(long eventId)
    {

        throw new NotYetImplementedException();
    }

    public void create(Event event)
    {

        throw new NotYetImplementedException();
    }

    public Collection<Event> list()
    {

        throw new NotYetImplementedException();
    }

    public void delete(long l)
    {

        throw new NotYetImplementedException();
    }

    public void update(Event newEvent)
    {

        throw new NotYetImplementedException();
    }
}

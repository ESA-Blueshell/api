package net.blueshell.api.business.event;

import net.blueshell.api.db.AbstractDAO;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@DependsOn("dataSource")
public class EventSignUpDao extends AbstractDAO<EventSignUp> {

    public EventSignUpDao() {
    }

    public EventSignUp getById(EventSignUpId id) {
        throw new NotYetImplementedException();
    }

    public void delete(EventSignUpId id) {
        throw new NotYetImplementedException();
    }

    public Collection<EventSignUp> list()
    {

        throw new NotYetImplementedException();
    }

    public void update(EventSignUp signUp)
    {

        throw new NotYetImplementedException();
    }

    public void create(EventSignUp signUp)
    {

        throw new NotYetImplementedException();
    }
}

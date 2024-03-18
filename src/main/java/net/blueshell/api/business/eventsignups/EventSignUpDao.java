package net.blueshell.api.business.eventsignups;

import net.blueshell.api.business.event.Event;
import net.blueshell.api.business.user.User;
import net.blueshell.api.daos.Dao;
import net.blueshell.api.db.SessionWrapper;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("dataSource")
public class EventSignUpDao extends SessionWrapper<EventSignUp> implements Dao<EventSignUp> {

    public EventSignUpDao() {
        super(EventSignUp.class);
    }

    public EventSignUp getByUserAndEvent(User authedUser, Event event) {
        EventSignUp obj;
        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            var query = session.createQuery("from EventSignUp where user = :user and event = :event");
            query.setParameter("user", authedUser);
            query.setParameter("event", event);
            var objs = query.list();
            obj = objs.isEmpty() ? null : (EventSignUp) objs.get(0);
            t.commit();
            session.close();
        }
        return obj;
    }

    public EventSignUp getByHashedId(String hashedId) {
        EventSignUp obj;
        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            var query = session.createQuery("from EventSignUp where md5(id) = :hashedId");
            query.setParameter("hashedId", hashedId);
            var objs = query.list();
            obj = objs.isEmpty() ? null : (EventSignUp) objs.get(0);
            t.commit();
            session.close();
        }
        return obj;
    }
}

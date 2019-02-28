package net.blueshell.api.daos;

import net.blueshell.api.db.DatabaseManager;
import net.blueshell.api.model.Event;
import org.hibernate.SessionFactory;

import java.util.List;

public class EventDao implements Dao<Event>  {

    private SessionFactory sessionFactory = DatabaseManager.getSessionFactory();

    public List<Event> list() {
        String hql = "FROM Event as event ORDER BY event.id";
        return (List<Event>) sessionFactory.openSession().createQuery(hql).getResultList();
    }

    public Event getById(long id) {
        return sessionFactory.openSession().find(Event.class, id);
    }

    public Event create(Event event) {
        sessionFactory.openSession().save(event);
        return event;
    }

    public void update(Event event) {
        sessionFactory.openSession().update(event);
    }

    public void delete(int id) {
        sessionFactory.openSession().remove(getById(id));
    }

}

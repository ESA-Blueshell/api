package net.blueshell.api.business.event;

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

    @Override
    public EventSignUp getById(long id) {
        System.err.println("THIS METHOD SHOULD NOT BE USED, AS EventSignUp DOES NOT HAVE AN ID OF TYPE LONG");
        return null;
    }

    public EventSignUp getById(EventSignUpId id) {
        EventSignUp obj = null;
        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            obj = session.find(EventSignUp.class, id);
            t.commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public void delete(long id) {
        System.err.println("THIS METHOD SHOULD NOT BE USED, AS EventSignUp DOES NOT HAVE AN ID OF TYPE LONG");
    }

    public void delete(EventSignUpId id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            session.remove(getById(id));
            t.commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

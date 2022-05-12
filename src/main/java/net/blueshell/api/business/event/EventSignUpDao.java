package net.blueshell.api.business.event;

import net.blueshell.api.daos.Dao;
import net.blueshell.api.db.SessionWrapper;
import org.hibernate.Session;
import org.hibernate.Transaction;

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
        boolean done = false;
        while (!done) {
            try (Session session = getSessionFactory().openSession()) {
                Transaction t = session.beginTransaction();
                obj = session.find(EventSignUp.class, id);
                t.commit();
                done = true;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Well that didn't work, let's try again");
            }
        }
        return obj;
    }

    @Override
    public void delete(long id) {
        System.err.println("THIS METHOD SHOULD NOT BE USED, AS EventSignUp DOES NOT HAVE AN ID OF TYPE LONG");
    }

    public void delete(EventSignUpId id) {
        boolean done = false;
        while (!done) {
            try (Session session = getSessionFactory().openSession()) {
                Transaction t = session.beginTransaction();
                session.remove(getById(id));
                t.commit();
                done = true;
            } catch (Exception e) {
                System.out.println("Well that didn't work, let's try again");
            }
        }
    }

}

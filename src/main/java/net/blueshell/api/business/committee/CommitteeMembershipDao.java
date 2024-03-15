package net.blueshell.api.business.committee;

import net.blueshell.api.daos.Dao;
import net.blueshell.api.db.SessionWrapper;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("dataSource")
public class CommitteeMembershipDao extends SessionWrapper<CommitteeMembership> implements Dao<CommitteeMembership> {

    public CommitteeMembershipDao() {
        super(CommitteeMembership.class);
    }

    @Override
    public CommitteeMembership getById(long id) {
        System.err.println("THIS METHOD SHOULD NOT BE USED, AS CommitteeMembership DOES NOT HAVE AN ID OF TYPE LONG");
        return null;
    }

    public CommitteeMembership getById(CommitteeMembershipId id) {
        CommitteeMembership obj = null;
        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            obj = session.find(CommitteeMembership.class, id);
            t.commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public void delete(long id) {
        System.err.println("THIS METHOD SHOULD NOT BE USED, AS CommitteeMembership DOES NOT HAVE AN ID OF TYPE LONG");
    }

    public void delete(CommitteeMembershipId id) {
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

package net.blueshell.api.daos;

import net.blueshell.api.db.DatabaseManager;
import net.blueshell.api.model.Billable;
import net.blueshell.api.model.Committee;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class CommitteeDao extends Dao<Committee> {

    private SessionFactory sessionFactory = DatabaseManager.getSessionFactory();

    @Override
    public List<Committee> list() {
        Session session = sessionFactory.openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Committee> criteriaQuery = builder.createQuery(Committee.class);
        Root<Committee> committeeRoot = criteriaQuery.from(Committee.class);
        criteriaQuery.select(committeeRoot);
        return session.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public Committee getById(long id) {
        return sessionFactory.openSession().find(Committee.class, id);
    }

    @Override
    public Committee create(Committee committee) {
        sessionFactory.openSession().save(committee);
        return committee;
    }

    @Override
    public void update(Committee committee) {
        sessionFactory.openSession().update(committee);
    }

    @Override
    public void delete(long id) {
        sessionFactory.openSession().remove(getById(id));
    }
}

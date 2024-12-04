package net.blueshell.api.business.contribution;

import net.blueshell.api.business.user.User;
import net.blueshell.api.daos.Dao;
import net.blueshell.api.db.SessionWrapper;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@DependsOn("dataSource")
public class ContributionDao extends SessionWrapper<Contribution> implements Dao<Contribution> {

    public ContributionDao() {
        super(Contribution.class);
    }

    public void deleteByContributionPeriod(ContributionPeriod contributionPeriod) {
        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            var query = session.createQuery("delete from Contribution where contributionPeriod = :contributionPeriod");
            query.setParameter("contributionPeriod", contributionPeriod);
            query.executeUpdate();
            t.commit();
            session.close();
        }
    }

    public List<Contribution> getContributionsByPeriod(ContributionPeriod contributionPeriod) {
        try (Session session = sessionFactory.openSession()) {
            var query = session.createQuery("from Contribution where contributionPeriod = :contributionPeriod", Contribution.class);
            query.setParameter("contributionPeriod", contributionPeriod);
            List<Contribution> contributions = query.getResultList();
            session.close();
            return contributions;
        }
    }
}

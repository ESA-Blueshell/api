package net.blueshell.api.business.contribution;

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

    public void createAll(List<Contribution> contributions) {
        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            int batchSize = 20;
            for (int i = 0; i < contributions.size(); i++) {
                session.save(contributions.get(i));
                if (i % batchSize == 0) {
                    session.flush();
                    session.clear();
                }
            }
            t.commit();
            session.close();
        }
    }

    public void getContributionsByPeriod(ContributionPeriod contributionPeriod) {

    }
}

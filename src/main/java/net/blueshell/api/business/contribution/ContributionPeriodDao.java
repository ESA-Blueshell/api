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
public class ContributionPeriodDao extends SessionWrapper<ContributionPeriod> implements Dao<ContributionPeriod> {

    public ContributionPeriodDao() {
        super(ContributionPeriod.class);
    }

    public List<ContributionPeriod> list() {
        List<ContributionPeriod> periods;
        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            var query = session.createQuery("from ContributionPeriod");
            periods = query.list();
            t.commit();
            session.close();
        }
        return periods;
    }

}

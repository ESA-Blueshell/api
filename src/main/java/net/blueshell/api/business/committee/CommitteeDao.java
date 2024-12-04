package net.blueshell.api.business.committee;

import net.blueshell.api.daos.Dao;
import net.blueshell.api.db.SessionWrapper;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@DependsOn("dataSource")
public class CommitteeDao extends SessionWrapper<Committee> implements Dao<Committee> {

    public CommitteeDao() {
        super(Committee.class);
    }

    public List<Committee> list() {
        List<Committee> obj = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            var query = session.createQuery("from Committee");
            obj.addAll(query.list());
            t.commit();
            session.close();
        }
        return obj;
    }
}

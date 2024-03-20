package net.blueshell.api.business.guest;

import net.blueshell.api.daos.Dao;
import net.blueshell.api.db.SessionWrapper;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("dataSource")
public class GuestDao extends SessionWrapper<Guest> implements Dao<Guest> {

    public GuestDao() {
        super(Guest.class);
    }

    @Override
    public Guest getById(long id) {
        System.err.println("DO NOT USE THIS METHOD, GETTING THE GUEST BY ID IS NOT VERY SECURE");
        return null;
    }

    public Guest getByAccessToken(String accessToken) {
        Guest obj;
        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            var query = session.createQuery("from Guest where accessToken = :accessToken");
            query.setParameter("accessToken", accessToken);
            var objs = query.list();
            obj = objs.isEmpty() ? null : (Guest) objs.get(0);
            t.commit();
            session.close();
        }
        return obj;
    }
}

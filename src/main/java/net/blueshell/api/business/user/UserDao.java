package net.blueshell.api.business.user;

import net.blueshell.api.daos.Dao;
import net.blueshell.api.db.SessionWrapper;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class UserDao extends SessionWrapper<User> implements Dao<User> {

    public UserDao() {
        super(User.class);
    }

    public User getByUsername(String username) {
        User obj;
        try (Session session = getSessionFactory().openSession()) {
            Transaction t = session.beginTransaction();
            var query = session.createQuery("from User where username = :name");
            query.setParameter("name", username);
            var objs = query.list();
            obj = objs.size() == 0 ? null : (User) objs.get(0);
            t.commit();
        }
        return obj;
    }
}

package net.blueshell.api.daos;

import net.blueshell.api.db.DatabaseManager;
import net.blueshell.api.db.SessionWrapper;
import net.blueshell.api.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class UserDao extends SessionWrapper<User> implements Dao<User>  {

    public UserDao() {
        super(User.class);
    }

    public User getByUsername(String username) {
        User obj;
        try (Session session = DatabaseManager.getSession()) {
            Transaction t = session.beginTransaction();
            Query query = session.createQuery("from users where username = :name");
            query.setParameter("name", username);
            List objs = query.list();
            obj = objs.size() == 0 ? null : (User) objs.get(0);
            t.commit();
        }
        return obj;
    }
}

package net.blueshell.api.daos;

import net.blueshell.api.db.DatabaseManager;
import net.blueshell.api.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class UserDao implements Dao<User> {

    private SessionFactory sessionFactory = DatabaseManager.getSessionFactory();

    public List<User> list() {
        Session session = sessionFactory.openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> criteria = builder.createQuery(User.class);
        Query<User> query = session.createQuery(criteria);
        return query.getResultList();
    }

    public User getById(long id) {
        return sessionFactory.openSession().find(User.class, id);
    }

    public User create(User user) {
        sessionFactory.openSession().save(user);
        return user;
    }

    public void update(User user) {
        sessionFactory.openSession().update(user);
    }

    public void delete(long id) {
        sessionFactory.openSession().remove(getById(id));
    }

}

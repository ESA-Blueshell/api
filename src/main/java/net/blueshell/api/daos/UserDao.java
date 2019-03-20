package net.blueshell.api.daos;

import net.blueshell.api.db.DatabaseManager;
import net.blueshell.api.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class UserDao implements Dao<User> {

    private SessionFactory sessionFactory = DatabaseManager.getSessionFactory();

    public List<User> list() {
        Session session = sessionFactory.openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = builder.createQuery(User.class);
        Root<User> userRoot = criteriaQuery.from(User.class);
        criteriaQuery.select(userRoot);
        return session.createQuery(criteriaQuery).getResultList();
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

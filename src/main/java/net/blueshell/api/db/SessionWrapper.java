package net.blueshell.api.db;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class SessionWrapper<T> {

    private SessionFactory sessionFactory;
    private Class<T> clazz;

    public SessionWrapper(Class<T> clazz) {
        this.clazz = clazz;
        sessionFactory = DatabaseManager.getSessionFactory();
    }

    public List<T> list() {
        List<T> list;
        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = builder.createQuery(clazz);
            Root<T> tRoot = criteriaQuery.from(clazz);
            criteriaQuery.select(tRoot);
            list = session.createQuery(criteriaQuery).getResultList();
            t.commit();
        }
        return list;
    }

    public T getById(long id) {
        T obj;
        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            obj = session.find(clazz, id);
            t.commit();
        }
        return obj;
    }

    public T create(T object) {
        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            session.save(object);
            t.commit();
        }
        return object;
    }

    public void update(T object) {
        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            session.update(object);
            t.commit();
        }
    }

    public void delete(long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            session.remove(getById(id));
            t.commit();
        }
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}

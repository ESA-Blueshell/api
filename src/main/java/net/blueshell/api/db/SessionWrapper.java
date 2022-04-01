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
        List<T> list = null;
        boolean done = false;
        while (!done) {
            try (Session session = sessionFactory.openSession()) {
                Transaction t = session.beginTransaction();
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<T> criteriaQuery = builder.createQuery(clazz);
                Root<T> tRoot = criteriaQuery.from(clazz);
                criteriaQuery.select(tRoot);
                list = session.createQuery(criteriaQuery).getResultList();
                t.commit();
                done = true;
            } catch (Exception e) {
                System.out.println("Well that didn't work, let's try again");
            }
        }
        return list;
    }

    public T getById(Object id) {
        T obj = null;
        boolean done = false;
        while (!done) {
            try (Session session = sessionFactory.openSession()) {
                Transaction t = session.beginTransaction();
                obj = session.find(clazz, id);
                t.commit();
                done = true;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Well that didn't work, let's try again");
            }
        }
        return obj;
    }

    public T create(T object) {
        boolean done = false;
        while (!done) {
            try (Session session = sessionFactory.openSession()) {
                Transaction t = session.beginTransaction();
                session.save(object);
                t.commit();
                done = true;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Well that didn't work, let's try again");
            }
        }
        return object;
    }

    public void update(T object) {
        boolean done = false;
        while (!done) {
            try (Session session = sessionFactory.openSession()) {
                Transaction t = session.beginTransaction();
                session.update(object);
                t.commit();
                done = true;
            } catch (Exception e) {
                System.out.println("Well that didn't work, let's try again");
            }
        }
    }

    public void delete(Object id) {
        boolean done = false;
        while (!done) {
            try (Session session = sessionFactory.openSession()) {
                Transaction t = session.beginTransaction();
                session.remove(getById(id));
                t.commit();
                done = true;
            } catch (Exception e) {
                System.out.println("Well that didn't work, let's try again");
            }
        }
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}

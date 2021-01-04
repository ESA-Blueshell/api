package net.blueshell.api.db;

import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class SessionWrapper<T> {

    private Class<T> clazz;

    public SessionWrapper(Class<T> clazz) {
        this.clazz = clazz;
    }

    public List<T> list() {
        List<T> list = null;
//        boolean done = false;
//        while (!done) {
        try (Session session = DatabaseManager.getSession()) {
            session.close();
            Transaction t = session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = builder.createQuery(clazz);
            Root<T> tRoot = criteriaQuery.from(clazz);
            criteriaQuery.select(tRoot);
            list = session.createQuery(criteriaQuery).getResultList();
            t.commit();

//            } catch (Exception e) {
//                System.out.println("Well that didn't work, let's try again");
//            }
        } catch (Exception e) {
            System.out.println("OK WHAT THE ACTUAL FUCKKKK");
        }
        return list;
    }

    public T getById(long id) {
        T obj = null;
//        boolean done = false;
//        while (!done) {
        try (Session session = DatabaseManager.getSession()) {
            Transaction t = session.beginTransaction();
            obj = session.find(clazz, id);
            t.commit();

//            } catch (Exception e) {
//                System.out.println("Well that didn't work, let's try again");
//            }
        } catch (Exception e) {
            System.out.println("OK WHAT THE ACTUAL FUCKKKK");
        }
        return obj;
    }

    public T create(T object) {
//        boolean done = false;
//        while (!done) {
        try (Session session = DatabaseManager.getSession()) {
            Transaction t = session.beginTransaction();
            session.save(object);
            t.commit();

//            } catch (Exception e) {
//                System.out.println("Well that didn't work, let's try again");
//            }
        } catch (Exception e) {
            System.out.println("OK WHAT THE ACTUAL FUCKKKK");
            e.printStackTrace();

        }
        return object;
    }

    public void update(T object) {
//        boolean done = false;
//        while (!done) {
        try (Session session = DatabaseManager.getSession()) {
            Transaction t = session.beginTransaction();
            session.update(object);
            t.commit();

//            } catch (Exception e) {
//                System.out.println("Well that didn't work, let's try again");
//            }
        } catch (Exception e) {
            System.out.println("OK WHAT THE ACTUAL FUCKKKK");
            e.printStackTrace();
        }

    }

    public void delete(long id) {
//        boolean done = false;
//        while (!done) {
        try (Session session = DatabaseManager.getSession()) {
            Transaction t = session.beginTransaction();
            session.remove(getById(id));
            t.commit();

//            } catch (Exception e) {
//                System.out.println("Well that didn't work, let's try again");
//            }
        } catch (Exception e) {
            System.out.println("OK WHAT THE ACTUAL FUCKKKK");
            e.printStackTrace();
        }
    }

}

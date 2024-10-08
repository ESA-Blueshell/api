package net.blueshell.api.business.user;

import net.blueshell.api.daos.Dao;
import net.blueshell.api.db.SessionWrapper;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@DependsOn("dataSource")
public class UserDao extends SessionWrapper<User> implements Dao<User> {

    public UserDao() {
        super(User.class);
    }

    public User getByUsername(String username) {
        User obj;
        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            var query = session.createQuery("from User where username = :name");
            query.setParameter("name", username);
            var objs = query.list();
            obj = objs.size() == 0 ? null : (User) objs.get(0);
            t.commit();
            session.close();
        }
        return obj;
    }

    public User getByEmail(String email) {
        User obj;
        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            var query = session.createQuery("from User where email = :email");
            query.setParameter("email", email);
            var objs = query.list();
            obj = objs.size() == 0 ? null : (User) objs.get(0);
            t.commit();
            session.close();
        }
        return obj;
    }

    public User getByPhoneNumber(String phoneNumber) {
        User obj;
        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            var query = session.createQuery("from User where phoneNumber = :phoneNumber");
            query.setParameter("phoneNumber", phoneNumber);
            var objs = query.list();
            obj = objs.size() == 0 ? null : (User) objs.get(0);
            t.commit();
            session.close();
        }
        return obj;
    }


    public List<User> list(Boolean isMember) {
        List<User> obj = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            var query = session.createQuery("from User");
            obj.addAll(query.list());
            if (isMember != null) {
                // Just do this like this for now, idk how to do it properly in HQL
                obj = obj.stream().filter(u -> isMember == u.hasRole(Role.MEMBER)).collect(Collectors.toList());
            }
            t.commit();
            session.close();
        }
        return obj;
    }
}

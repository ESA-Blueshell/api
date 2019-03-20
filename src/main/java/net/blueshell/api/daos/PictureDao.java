package net.blueshell.api.daos;

import net.blueshell.api.db.DatabaseManager;
import net.blueshell.api.model.Picture;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class PictureDao implements Dao<Picture> {

    private SessionFactory sessionFactory = DatabaseManager.getSessionFactory();

    public List<Picture> list() {
        Session session = sessionFactory.openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Picture> criteria = builder.createQuery(Picture.class);
        Query<Picture> query = session.createQuery(criteria);
        return query.getResultList();
    }

    public Picture getById(long id) {
        return sessionFactory.openSession().find(Picture.class, id);
    }

    public Picture create(Picture picture) {
        sessionFactory.openSession().save(picture);
        return picture;
    }

    public void update(Picture picture) {
        sessionFactory.openSession().update(picture);
    }

    public void delete(long id) {
        sessionFactory.openSession().remove(getById(id));
    }

}

package net.blueshell.api.daos;

import net.blueshell.api.db.DatabaseManager;
import net.blueshell.api.model.Picture;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class PictureDao implements Dao<Picture> {

    private SessionFactory sessionFactory = DatabaseManager.getSessionFactory();

    public List<Picture> list() {
        Session session = sessionFactory.openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Picture> criteriaQuery = builder.createQuery(Picture.class);
        Root<Picture> pictureRoot = criteriaQuery.from(Picture.class);
        criteriaQuery.select(pictureRoot);
        return session.createQuery(criteriaQuery).getResultList();
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

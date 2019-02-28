package net.blueshell.api.daos;

import net.blueshell.api.db.DatabaseManager;
import net.blueshell.api.model.Picture;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PictureDao implements Dao<Picture> {

    private SessionFactory sessionFactory = DatabaseManager.getSessionFactory();

    public List<Picture> list() {
        String hql = "FROM Picture as picture ORDER BY picture.id";
        return (List<Picture>) sessionFactory.openSession().createQuery(hql).getResultList();
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

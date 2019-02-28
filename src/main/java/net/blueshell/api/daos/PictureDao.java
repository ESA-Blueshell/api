package net.blueshell.api.daos;

import net.blueshell.api.model.Picture;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PictureDao implements Dao<Picture> {

    @Autowired
    SessionFactory sessionFactory;

    public List<Picture> list() {
        String hql = "FROM Picture as picture ORDER BY picture.id";
        return (List<Picture>) sessionFactory.openSession().createQuery(hql).getResultList();
    }

    public Picture getById(int id) {
        return sessionFactory.openSession().find(Picture.class, id);
    }

    public Picture create(Picture picture) {
        sessionFactory.openSession().save(picture);
        return picture;
    }

    public void update(Picture picture) {
        sessionFactory.openSession().update(picture);
    }

    public void delete(int id) {
        sessionFactory.openSession().remove(getById(id));
    }

}

package net.blueshell.api.business.picture;

import net.blueshell.api.daos.Dao;
import net.blueshell.api.db.SessionWrapper;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("dataSource")
public class PictureDao extends SessionWrapper<Picture> implements Dao<Picture> {


    public PictureDao() {
        super(Picture.class);
    }
}

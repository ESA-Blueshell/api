package net.blueshell.api.business.picture;

import net.blueshell.api.daos.Dao;
import net.blueshell.api.db.SessionWrapper;

public class PictureDao extends SessionWrapper<Picture> implements Dao<Picture> {


    public PictureDao() {
        super(Picture.class);
    }
}

package net.blueshell.api.daos;

import net.blueshell.api.db.SessionWrapper;
import net.blueshell.api.model.Picture;

public class PictureDao extends SessionWrapper<Picture> implements Dao<Picture> {


    public PictureDao() {
        super(Picture.class);
    }
}

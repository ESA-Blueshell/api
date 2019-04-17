package net.blueshell.api.daos;

import net.blueshell.api.db.SessionWrapper;
import net.blueshell.api.model.User;

public class UserDao extends SessionWrapper<User> implements Dao<User>  {

    public UserDao() {
        super(User.class);
    }

}

package net.blueshell.api.business.registration;

import net.blueshell.api.daos.Dao;
import net.blueshell.api.db.SessionWrapper;

public class RegistrationDao extends SessionWrapper<Registration> implements Dao<Registration> {

    public RegistrationDao() {
        super(Registration.class);
    }
}

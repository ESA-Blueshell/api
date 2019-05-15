package net.blueshell.api.daos;

import net.blueshell.api.db.SessionWrapper;
import net.blueshell.api.model.Registration;

public class RegistrationDao extends SessionWrapper<Registration> implements Dao<Registration> {

    public RegistrationDao() {
        super(Registration.class);
    }
}

package net.blueshell.api.business.guest;

import net.blueshell.api.daos.Dao;
import net.blueshell.api.db.SessionWrapper;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("dataSource")
public class GuestDao extends SessionWrapper<Guest> implements Dao<Guest> {

    public GuestDao() {
        super(Guest.class);
    }
}

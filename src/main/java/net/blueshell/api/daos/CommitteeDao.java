package net.blueshell.api.daos;

import net.blueshell.api.db.SessionWrapper;
import net.blueshell.api.model.Committee;

public class CommitteeDao extends SessionWrapper<Committee> implements Dao<Committee> {

    public CommitteeDao() {
        super(Committee.class);
    }
}

package net.blueshell.api.business.committee;

import net.blueshell.api.daos.Dao;
import net.blueshell.api.db.SessionWrapper;

public class CommitteeDao extends SessionWrapper<Committee> implements Dao<Committee> {

    public CommitteeDao() {
        super(Committee.class);
    }
}

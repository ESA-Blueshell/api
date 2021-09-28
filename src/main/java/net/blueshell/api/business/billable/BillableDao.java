package net.blueshell.api.business.billable;

import net.blueshell.api.daos.Dao;
import net.blueshell.api.db.SessionWrapper;

public class BillableDao extends SessionWrapper<Billable> implements Dao<Billable> {

    public BillableDao() {
        super(Billable.class);
    }

}

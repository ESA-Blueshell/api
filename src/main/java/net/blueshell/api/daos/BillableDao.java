package net.blueshell.api.daos;

import net.blueshell.api.db.SessionWrapper;
import net.blueshell.api.model.Billable;

public class BillableDao extends SessionWrapper<Billable> implements Dao<Billable> {

    public BillableDao() {
        super(Billable.class);
    }

}

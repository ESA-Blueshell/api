package net.blueshell.api.daos;

import net.blueshell.api.db.DatabaseManager;
import net.blueshell.api.model.Billable;
import net.blueshell.api.model.Picture;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class BillableDao extends  implements Dao<Billable> {

    public BillableDao() {
        super(Billable.class);
    }

}

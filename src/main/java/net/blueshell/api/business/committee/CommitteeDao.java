package net.blueshell.api.business.committee;

import net.blueshell.api.daos.Dao;
import net.blueshell.api.db.SessionWrapper;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("dataSource")
public class CommitteeDao extends SessionWrapper<Committee> implements Dao<Committee> {

    public CommitteeDao() {
        super(Committee.class);
    }
}

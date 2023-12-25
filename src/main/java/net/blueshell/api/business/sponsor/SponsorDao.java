package net.blueshell.api.business.sponsor;

import net.blueshell.api.daos.Dao;
import net.blueshell.api.db.SessionWrapper;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("dataSource")
public class SponsorDao extends SessionWrapper<Sponsor> implements Dao<Sponsor> {

    public SponsorDao() {
        super(Sponsor.class);
    }
}

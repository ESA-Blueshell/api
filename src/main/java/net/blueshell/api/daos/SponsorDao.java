package net.blueshell.api.daos;

import net.blueshell.api.db.SessionWrapper;
import net.blueshell.api.model.Sponsor;

public class SponsorDao extends SessionWrapper<Sponsor> implements Dao<Sponsor> {

    public SponsorDao() {
        super(Sponsor.class);
    }
}

package net.blueshell.api.business.sponsor;

import net.blueshell.api.daos.Dao;
import net.blueshell.api.db.SessionWrapper;

public class SponsorDao extends SessionWrapper<Sponsor> implements Dao<Sponsor> {

    public SponsorDao() {
        super(Sponsor.class);
    }
}

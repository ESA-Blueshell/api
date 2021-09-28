package net.blueshell.api.business.committee;

import net.blueshell.api.daos.Dao;
import net.blueshell.api.db.SessionWrapper;

public class CommitteeMembershipDao extends SessionWrapper<CommitteeMembership> implements Dao<CommitteeMembership> {

    public CommitteeMembershipDao() {
        super(CommitteeMembership.class);
    }

}

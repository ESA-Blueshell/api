package net.blueshell.api.daos;

import net.blueshell.api.db.SessionWrapper;
import net.blueshell.api.model.CommitteeMembership;

public class CommitteeMembershipDao extends SessionWrapper<CommitteeMembership> implements Dao<CommitteeMembership> {

    public CommitteeMembershipDao() {
        super(CommitteeMembership.class);
    }

}

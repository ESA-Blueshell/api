package net.blueshell.api.business.committee;

import net.blueshell.api.db.AbstractDAO;
import net.blueshell.api.tables.records.CommitteeMembersRecord;
import org.jooq.Result;
import org.springframework.stereotype.Component;

import static net.blueshell.api.tables.CommitteeMembers.COMMITTEE_MEMBERS;

@Component
public class CommitteeMembershipDao extends AbstractDAO<CommitteeMembership>
{

	public CommitteeMembershipDao()
	{
	}

	public Result<CommitteeMembersRecord> getById(CommitteeMembershipId id)
	{
		try (var con = getConnection())
		{
			var ctx = getContext(con);
			return ctx.selectFrom(COMMITTEE_MEMBERS)
					  .where(COMMITTEE_MEMBERS.COMMITTEE_ID.eq(id.getCommittee().getId()))
					  .and(COMMITTEE_MEMBERS.USER_ID.eq(id.getUser().getId()))
					  .fetch();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public void delete(CommitteeMembershipId id)
	{
		try (var con = getConnection())
		{
			var ctx = getContext(con);
			ctx.deleteFrom(COMMITTEE_MEMBERS)
			   .where(COMMITTEE_MEMBERS.COMMITTEE_ID.eq(id.getCommittee().getId()))
			   .and(COMMITTEE_MEMBERS.USER_ID.eq(id.getUser().getId()))
			   .execute();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}

package net.blueshell.api.business.committee;

import net.blueshell.api.business.user.User;
import net.blueshell.api.db.AbstractDAO;
import org.jetbrains.annotations.NotNull;
import org.jooq.Result;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import net.blueshell.api.tables.records.CommitteesRecord;

import java.sql.SQLException;

import static net.blueshell.api.tables.Committees.COMMITTEES;

@Component
@DependsOn("dataSource")
public class CommitteeDao extends AbstractDAO<CommitteesRecord>
{

	public CommitteeDao()
	{
	}

	public Result<CommitteesRecord> list()
	{
		try
		{
			var con = getConnection();
			var ctx = getContext(con);
			return ctx.selectFrom(COMMITTEES)
					  .fetch();
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public CommitteesRecord create(CommitteesRecord committee)
	{
		try
		{
			var con = getConnection();
			var ctx = getContext(con);
			var id = ctx.insertInto(COMMITTEES)
						.set(committee)
						.returningResult(COMMITTEES.ID)
						.fetchSingle();

			return getById(id.value1());
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public CommitteesRecord getById(long id)
	{
		try
		{
			var con = getConnection();
			var ctx = getContext(con);
			return ctx.selectFrom(COMMITTEES)
					  .where(COMMITTEES.ID.eq(id))
					  .fetchOne();
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void update(CommitteesRecord newCommittee)
	{
		try
		{
			var con = getConnection();
			var ctx = getContext(con);
			ctx.update(COMMITTEES)
			   .set(newCommittee)
			   .execute();
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void delete(long id)
	{
		try
		{
			var con = getConnection();
			var ctx = getContext(con);
			ctx.deleteFrom(COMMITTEES)
			   .where(COMMITTEES.ID.eq(id))
			   .execute();
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}
}

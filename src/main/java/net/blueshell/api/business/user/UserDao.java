package net.blueshell.api.business.user;

import net.blueshell.api.db.AbstractDAO;
import net.blueshell.api.tables.records.UsersRecord;
import org.hibernate.cfg.NotYetImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static net.blueshell.api.tables.Roles.ROLES;
import static net.blueshell.api.tables.Users.USERS;

@Component
@DependsOn("dataSource")
public class UserDao extends AbstractDAO<User>
{

	public UserDao()
	{
	}

	public User getById(long id)
	{
		try (var con = getConnection())
		{
			var ctx = DSL.using(con, SQLDialect.MYSQL);
			return UserMapper.fromRecord(ctx.selectFrom(USERS)
											.where(USERS.ID.eq(id))
											.fetchOne());
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public UsersRecord getRecordById(long id)
	{
		try (var con = getConnection())
		{
			var ctx = DSL.using(con, SQLDialect.MYSQL);
			return ctx.selectFrom(USERS)
					  .where(USERS.ID.eq(id))
					  .fetchOne();
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public User getByUsername(String username)
	{
		try (var con = getConnection())
		{
			var ctx = DSL.using(con, SQLDialect.MYSQL);
			return UserMapper.fromRecord(ctx.selectFrom(USERS)
											.where(USERS.USERNAME.eq(username))
											.fetchOne());
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public @NotNull List<User> list()
	{
		try (var con = getConnection())
		{
			var ctx = DSL.using(con, SQLDialect.MYSQL);
			return ctx.selectFrom(USERS)
					  .fetch()
					  .stream()
					  .map(UserMapper::fromRecord)
					  .collect(Collectors.toList());
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public List<User> list(Role role)
	{
		try (var con = getConnection())
		{
			var ctx = DSL.using(con, SQLDialect.MYSQL);
			return ctx.select(USERS)
					  .from(USERS)
					  .innerJoin(ROLES).on(ROLES.USER_ID.eq(USERS.ID))
					  .where(ROLES.ROLE.eq(role.getReprString()))
					  .fetch().stream().map(Record1::value1)
					  .map(UserMapper::fromRecord)
					  .collect(Collectors.toList());
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void delete(long id)
	{
		try (var con = getConnection())
		{
			var ctx = DSL.using(con, SQLDialect.MYSQL);
			ctx.deleteFrom(USERS)
			   .where(USERS.ID.eq(id))
			   .execute();
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void updateResetInfo(User user)
	{
		try (var con = getConnection())
		{
			var ctx = DSL.using(con, SQLDialect.MYSQL);
			ctx.update(USERS)
			   .set(USERS.RESET_KEY, user.getResetKey())
			   .set(USERS.RESET_TYPE, user.getResetType().toString())
			   .set(USERS.RESET_KEY_VALID_UNTIL, user.getResetKeyValidUntil())
			   .set(USERS.ENABLED, (byte) (user.isEnabled() ? 1 : 0))
			   .where(USERS.ID.eq(user.getId()))
			   .execute();
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void updateRoles(User user)
	{
		try (var con = getConnection())
		{
			var ctx = DSL.using(con, SQLDialect.MYSQL);

			ctx.deleteFrom(ROLES)
			   .where(ROLES.USER_ID.eq(user.getId()))
			   .execute();

			var insert = ctx.insertInto(ROLES, ROLES.USER_ID, ROLES.ROLE);
			for (var role : user.getRoles())
			{
				insert = insert.values(user.getId(), role.getReprString());
			}
			insert.execute();
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void create(net.blueshell.api.tables.records.UsersRecord user)
	{
		try (var con = getConnection())
		{
			var ctx = getContext(con);
			var result = ctx.insertInto(USERS)
							.set(user)
							.returningResult(USERS.ID)
							.fetchOne();
			if (result != null)
			{
				user.setId(result.value1());
			}
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void update(UsersRecord user)
	{
		try (var con = getConnection())
		{
			var ctx = DSL.using(con, SQLDialect.MYSQL);
			ctx.update(USERS)
			   .set(user)
			   .execute();
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public Set<Role> getRoles(long id)
	{
		try (var con = getConnection())
		{
			var ctx = getContext(con);
			return ctx.selectFrom(ROLES)
					  .where(ROLES.USER_ID.eq(id))
					  .stream()
					  .map(rec -> Role.valueOf(rec.getRole()))
					  .collect(Collectors.toSet());
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}
}

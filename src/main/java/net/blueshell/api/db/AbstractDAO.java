package net.blueshell.api.db;

import lombok.Getter;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;

@Getter
public abstract class AbstractDAO<T> {

	private HikariCPDataSource datasource;

	public Connection getConnection() throws SQLException
	{
		return getDatasource().getConnection();
	}

	protected DSLContext getContext(Connection connection)
	{
		return DSL.using(connection, SQLDialect.MYSQL);
	}


}

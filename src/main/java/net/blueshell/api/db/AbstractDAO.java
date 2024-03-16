package net.blueshell.api.db;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractDAO<T> {

	private HikariCPDataSource datasource;

	public HikariCPDataSource getDatasource() {
		return datasource;
	}

	public Connection getConnection() throws SQLException
	{
		return getDatasource().getConnection();
	}


}

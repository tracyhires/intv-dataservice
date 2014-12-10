package com.dendreon.intellivenge.dataservice;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import com.google.inject.Provider;

public class JDBCDataService implements DataService {
	
    private Provider<DataSource> dataSourceProvider;

    @Inject
    public JDBCDataService(@Named("CapacityJndi") Provider<DataSource> aDataSourceProvider) {
        dataSourceProvider = aDataSourceProvider;
    }

	public ResultSet findRecords(String tableName,
			QueryParameter... queryParameters) {
		
		String queryString = new OracleQuery.QueryBuilder()
		.addTableName(tableName)
		.addParameters(Arrays.asList(queryParameters))
		.build();
		
		DataSource dataSource = dataSourceProvider.get();
		try {
			Connection connection = dataSource.getConnection();
			Statement statement = connection.createStatement();
			statement.execute(queryString);
			return statement.getResultSet();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public ResultSet findRecords(String[] tableNames, QueryParameter join,
			QueryParameter[] queryParameters) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResultSetMetaData describeTable(String tabelname) {
		// TODO Auto-generated method stub
		return null;
	}

}

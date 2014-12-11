package com.dendreon.intellivenge.dataservice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.slf4j.LoggerFactory;

import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class JDBCDataService implements DataService {
	
    private Provider<DataSource> dataSourceProvider;

    @Inject
    public JDBCDataService(@Named("XxsapJndi") Provider<DataSource> aDataSourceProvider) {
        dataSourceProvider = aDataSourceProvider;
    }

	public ResultSet findRecords(String tableName,
			QueryParameter... queryParameters) {
		
		DataSource dataSource = dataSourceProvider.get();
		try {
			Connection connection = dataSource.getConnection();
			
			PreparedStatement pStatement = new OracleQuery.QueryBuilder(connection)
			.addTableName(tableName)
			.addParameters(Arrays.asList(queryParameters))
			.build();
			
			LoggerFactory.getLogger(getClass()).info(pStatement.toString());
			
			pStatement.execute();
			return pStatement.getResultSet();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public ResultSet findRecords(String[] tableNames, JoinParameter join,
			QueryParameter[] queryParameters) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResultSetMetaData describeTable(String tabelname) {
		// TODO Auto-generated method stub
		return null;
	}

}

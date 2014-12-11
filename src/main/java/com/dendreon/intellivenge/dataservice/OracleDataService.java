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
import javax.sql.rowset.CachedRowSet;

import oracle.jdbc.rowset.OracleCachedRowSet;

import org.slf4j.LoggerFactory;

import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class OracleDataService implements DataService {

	private Provider<DataSource> dataSourceProvider;

	@Inject
	public OracleDataService(@Named("XxsapJndi") Provider<DataSource> aDataSourceProvider) {
		dataSourceProvider = aDataSourceProvider;
	}
	
	private ResultSet findRecords(String aTableName, JoinParameter aJoin, QueryParameter... queryParameters) {
		CachedRowSet vRetVal = null;

		DataSource dataSource = dataSourceProvider.get();
		if (dataSource != null) {
			try {
				Connection connection = dataSource.getConnection();
				try {
					OracleQuery.QueryBuilder qb = new OracleQuery.QueryBuilder(connection)
					.addParameters(Arrays.asList(queryParameters));
					if (aTableName != null) {
						qb.addTableName(aTableName);
					}
					if (aJoin != null) {
						qb.addJoin(aJoin);
					}
					PreparedStatement pStatement = qb.build();
					try {
						pStatement.execute();
						vRetVal = new OracleCachedRowSet();
						ResultSet resultSet = pStatement.getResultSet();
						try{
							vRetVal.populate(resultSet);
						} catch (SQLException e) {
							LoggerFactory.getLogger(getClass()).error("Could not perform query as requested.", e);
						}
						finally {
							if (resultSet != null) {
								try {
									resultSet.close();
								} catch (SQLException e) {
									LoggerFactory.getLogger(getClass()).warn("The ResultSet could no be closed, it will be closed automatically.");
								}
							}
						}} catch (SQLException e) {
							LoggerFactory.getLogger(getClass()).error("Could not perform query as requested.", e);
						} finally {
							if (pStatement != null) {
								try {
									pStatement.close();
								} catch (SQLException e) {
									LoggerFactory.getLogger(getClass()).warn("The PreparedStatement could no be closed, it will be closed automatically.");
								}
							}
						}} catch (SQLException e) {
							LoggerFactory.getLogger(getClass()).error("Could not perform query as requested.", e);
						} finally {
							if (connection != null) {
								try {
									connection.close();
								} catch (SQLException e) {
									LoggerFactory.getLogger(getClass()).warn("The Connection could no be closed, it will be closed automatically.");
								}
							}
						}} catch (SQLException e) {
							LoggerFactory.getLogger(getClass()).error("Could not perform query as requested.", e);
						}
		}
		return vRetVal;
	}

	public ResultSet findRecords(String tableName, QueryParameter... queryParameters) {
		return findRecords(tableName, null, queryParameters);
	}

	public ResultSet findRecords(JoinParameter join,
			QueryParameter... queryParameters) {
		return findRecords(null, join, queryParameters);
	}

	public ResultSetMetaData describeTable(String tablename) {
		ResultSetMetaData vRetVal = null;
		String sql = "select top 1 * from " + tablename;
		DataSource dataSource = dataSourceProvider.get();
		if (dataSource != null) {
			try {
				Connection connection = dataSource.getConnection();
				try {
					Statement statement = connection.createStatement();
					try {
						CachedRowSet cache = new OracleCachedRowSet();
						cache.populate(statement.executeQuery(sql));
						vRetVal = cache.getMetaData();
					} catch (SQLException e) {
						LoggerFactory.getLogger(getClass()).error("Could not perform query as requested.", e);
					} finally {
						if (statement != null) {
							try {
								statement.close();
							} catch (SQLException e) {
								LoggerFactory.getLogger(getClass()).warn("The Statement could no be closed, it will be closed automatically.");
							}
						}
					}} catch (SQLException e) {
						LoggerFactory.getLogger(getClass()).error("Could not perform query as requested.", e);
					} finally {
						if (connection != null) {
							try {
								connection.close();
							} catch (SQLException e) {
								LoggerFactory.getLogger(getClass()).warn("The Connection could no be closed, it will be closed automatically.");
							}
						}
					}} catch (SQLException e) {
						LoggerFactory.getLogger(getClass()).error("Could not perform query as requested.", e);
					}
		}
		return vRetVal;
	}

}

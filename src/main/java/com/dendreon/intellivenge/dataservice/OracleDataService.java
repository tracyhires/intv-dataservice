package com.dendreon.intellivenge.dataservice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;

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

	private ResultSet findRecords(String aTableName, String[] aColumns, Collection<JoinParameter> aJoins, QueryParameter... queryParameters) {
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
					if (aJoins != null) {
						qb.addJoin(aJoins);
					}
					if (aColumns != null) {
						qb.addRequestedColumns(aColumns);
					}
					PreparedStatement pStatement = qb.buildSelect();
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

	public ResultSet findRecords(String tableName, String[] aColumns, QueryParameter... queryParameters) {
		return findRecords(tableName, aColumns, null, queryParameters);
	}

	public ResultSet findRecords(String[] aColumns, Collection<JoinParameter> joins,
			QueryParameter... queryParameters) {
		return findRecords(null, aColumns, joins, queryParameters);
	}


	public ResultSet findRecords(String tableName, QueryParameter... queryParameters) {
		return findRecords(tableName, null, null, queryParameters);
	}

	public ResultSet findRecords(Collection<JoinParameter> joins, QueryParameter... queryParameters) {
		return findRecords(null, null, joins, queryParameters);
	}

	public ResultSetMetaData describeTable(String tablename) {
		ResultSetMetaData vRetVal = null;
		String sql = "select * from " + tablename + " where rownum=1";
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

	private Integer[] updateRecords(UpdateParameter[] aUpdates, UpdateType aType) throws DataServiceException {
		Integer[] vRetVal = new Integer[aUpdates.length];
		DataSource dataSource = dataSourceProvider.get();
		if (dataSource != null) {
			try {
				Connection connection = dataSource.getConnection();
				connection.setAutoCommit(false);
				try {
					DataServiceException exception = null;
					int numProcessed = 0;
					for (UpdateParameter update : aUpdates) {
						OracleQuery.QueryBuilder qb = new OracleQuery.QueryBuilder(connection)
						.addColumnValuePairs(update.getColumnValuePairs())
						.addTableName(update.getTableName())
						.addParameters(update.getQueryParameters());
						PreparedStatement pStatement = null;
						switch (aType) {
						case INSERT:
							pStatement = qb.buildInsert();
							break;
						case UPDATE:
							pStatement = qb.buildUpdate();
							break;
						case DELETE:
							pStatement = qb.buildDelete();
							break;
						}
						try {
							pStatement.executeUpdate();
							ResultSet resultSet = pStatement.getGeneratedKeys();
							if (resultSet != null && resultSet.next()) { 
								vRetVal[numProcessed++] = resultSet.getInt(1); 
							}
						} catch (SQLException e) {
							LoggerFactory.getLogger(getClass()).error("Could not perform update as requested.", e);
							exception = new DataServiceException(e.getMessage());
							break;
						} finally {
							if (pStatement != null) {
								try {
									pStatement.close();
								} catch (SQLException e) {
									LoggerFactory.getLogger(getClass()).warn("The PreparedStatement could no be closed, it will be closed automatically.");
								}
							}
						}
					}
					if (exception == null) {
						connection.commit();
					}
					else {
						connection.rollback();
						throw exception;
					}
				} catch (SQLException e) {
					LoggerFactory.getLogger(getClass()).error("Could not perform update as requested.", e);
				} finally {
					if (connection != null) {
						try {
							connection.close();
						} catch (SQLException e) {
							LoggerFactory.getLogger(getClass()).warn("The Connection could no be closed, it will be closed automatically.");
						}
					}
				}} catch (SQLException e) {
					LoggerFactory.getLogger(getClass()).error("Could not perform update as requested.", e);
				}
		}
		return vRetVal;
	}

	public Integer[] insertRecords(UpdateParameter[] aInserts) throws DataServiceException {
		return updateRecords(aInserts, UpdateType.INSERT);
	}

	public void updateRecords(UpdateParameter[] aUpdates) throws DataServiceException {
		updateRecords(aUpdates, UpdateType.UPDATE);
	}

	public void deleteRecords(UpdateParameter[] aDeletes) throws DataServiceException {
		updateRecords(aDeletes, UpdateType.DELETE);
	}

	private enum UpdateType {
		INSERT, UPDATE, DELETE;
	}

}

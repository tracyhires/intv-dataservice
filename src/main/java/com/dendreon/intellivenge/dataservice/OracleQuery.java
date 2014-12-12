package com.dendreon.intellivenge.dataservice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class OracleQuery {

	public static class QueryBuilder {

		private final String SPACE = " ";
		private final String PH = "?";
		private final String SPHS = " ? ";
		private final Connection connection;
		private String requestedColumns;
		private String tableName;
		private Set<QueryParameter> parameters;
		private Collection<JoinParameter> joins;

		public  QueryBuilder(Connection aConnection) {
			connection = aConnection;
			parameters = new HashSet<QueryParameter>();
		}

		public QueryBuilder addRequestedColumns(String aColumnCSV) {
			requestedColumns = aColumnCSV;
			return this;
		}

		public QueryBuilder addTableName(String aTableName) {
			tableName = aTableName;
			return this;
		}

		public QueryBuilder addJoin(Collection<JoinParameter> aJoins) {
			joins = aJoins;
			return this;
		}

		public QueryBuilder addParameter(QueryParameter aParameter) {
			parameters.add(aParameter);
			return this;
		}

		public QueryBuilder addParameters(Collection<QueryParameter> aParameters) {
			parameters.addAll(aParameters);
			return this;
		}

		public PreparedStatement build() throws SQLException {
			PreparedStatement pStatement = null;
			StringBuffer queryString = new StringBuffer();
			Map<Integer,Object> preparedValues = new HashMap<Integer,Object>();

			queryString.append("select" + SPACE);
			if (requestedColumns != null) {
				queryString.append(requestedColumns + SPACE);
			} else {
				queryString.append("*" + SPACE);
			}
			queryString.append("from" + SPACE);
			if (joins != null) {
				addJoins(queryString);
			}
			else {
				queryString.append(tableName + SPACE);
			}
			if (parameters.size() > 0) {
				preparedValues.putAll(addParameters(queryString));
			}
			// create statement and add values
			pStatement = connection.prepareStatement(queryString.toString());
			for (Integer index : preparedValues.keySet()) {
				setValue(pStatement, index, preparedValues.get(index));
			}
			return pStatement;
		}
		
		private void addJoins(StringBuffer aQueryString) {
			Set<String> tableNames = new HashSet<String>();
			StringBuffer whereBuffer = new StringBuffer("where" + SPACE);
			Iterator<JoinParameter> joinIterator = joins.iterator();
			while (joinIterator.hasNext()) {
				JoinParameter join = joinIterator.next();
				tableNames.add(join.getLeftTableName());
				tableNames.add(join.getRightTableName());
				whereBuffer.append(join.getLeftTableName() + "." + join.getLeftColumnName());
				whereBuffer.append("=" + join.getRightTableName() + "." + join.getRightColumnName());
				switch(join.getJoinType()) {
				case INNER_JOIN:
					whereBuffer.append(SPACE);
					break;
				case OUTER_JOIN:
					whereBuffer.append("(+)" + SPACE);
					break;
				}
				if (joinIterator.hasNext()) {
					whereBuffer.append("and" + SPACE);
				}
				
			}
			Iterator<String> tableNamesIterator = tableNames.iterator();
			while (tableNamesIterator.hasNext()) {
				String tableName = tableNamesIterator.next();
				aQueryString.append(tableName);
				if (tableNamesIterator.hasNext()) {
					aQueryString.append(",");
				}
				else {
					aQueryString.append(SPACE);
				}
			}
			aQueryString.append(whereBuffer.toString());
		}
		
		private Map<Integer,Object> addParameters(StringBuffer aQueryString) {
			Map<Integer,Object> preparedValues = new HashMap<Integer,Object>();
			//in case of join
			if (aQueryString.indexOf("where") == -1) {
				aQueryString.append("where" + SPACE);
			}
			else {
				aQueryString.append("and" + SPACE);
			}
			Iterator<QueryParameter> paramIter = parameters.iterator();
			int counter = 1;
			while (paramIter.hasNext()) {
				QueryParameter q = paramIter.next();
				switch(q.getQueryType()) {
				case EQ:
					aQueryString.append(q.getColumnName() + SPACE);
					aQueryString.append("=" + SPHS);
					preparedValues.put(counter++, q.getValue());
					break;
				case GT:
					aQueryString.append(q.getColumnName() + SPACE);
					aQueryString.append(">" + SPHS);
					preparedValues.put(counter++, q.getValue());
					break;
				case GTE:
					aQueryString.append(q.getColumnName() + SPACE);
					aQueryString.append(">=" + SPHS);
					preparedValues.put(counter++, q.getValue());
					break;
				case LT:
					aQueryString.append(q.getColumnName() + SPACE);
					aQueryString.append("<" + SPHS);
					preparedValues.put(counter++, q.getValue());
					break;
				case LTE:
					aQueryString.append(q.getColumnName() + SPACE);
					aQueryString.append("<=" + SPHS);
					preparedValues.put(counter++, q.getValue());
					break;
				case NEQ:
					aQueryString.append(q.getColumnName() + SPACE);
					aQueryString.append("!=" + SPHS);
					preparedValues.put(counter++, q.getValue());
					break;
				case CONTAINS:
					Object stringValue = q.getValue();
					if (stringValue instanceof String) {
						aQueryString.append("lower(" + q.getColumnName() + ")" + SPACE);
						aQueryString.append("like" + SPHS);
						preparedValues.put(counter++, "%" + ((String)stringValue).toLowerCase() + "%");
					}
					break;
				case IN:
					Object collectionValue = q.getValue();
					Iterator<?> inIterator = null;
					if (collectionValue instanceof Collection) {
						inIterator = ((Collection<?>)collectionValue).iterator();
					}
					else if (collectionValue.getClass().isArray()) {
						inIterator = Arrays.asList(((Object[])collectionValue)).iterator();
					}
					if (inIterator != null) {
						aQueryString.append(q.getColumnName() + SPACE);
						aQueryString.append("in (");
						while (inIterator.hasNext()) {
							preparedValues.put(counter++, inIterator.next());
							aQueryString.append(PH);
							if (inIterator.hasNext()) {
								aQueryString.append(",");
							}
							else {
								aQueryString.append(")");
							}
						}
					}
					break;	
				}
				if (paramIter.hasNext()) {
					aQueryString.append("and" + SPACE);
				}
			}
			return preparedValues;
		}

		private void setValue(PreparedStatement aPreparedStatement, int index, Object aValue) throws SQLException {
			if (aValue instanceof Date) {
				aPreparedStatement.setDate(index, new java.sql.Date((((Date)aValue)).getTime()));
			}
			if (aValue instanceof String) {
				aPreparedStatement.setString(index, (String)aValue);
			}
			if (aValue instanceof Integer) {
				aPreparedStatement.setInt(index, ((Integer) aValue).intValue());
			}
			if (aValue instanceof Double) {
				aPreparedStatement.setDouble(index, ((Double) aValue).doubleValue());
			}
			if (aValue instanceof Long) {
				aPreparedStatement.setLong(index, ((Long) aValue).longValue());
			}
			if (aValue instanceof Boolean) {
				aPreparedStatement.setBoolean(index, (Boolean)aValue);
			}
			if (aValue instanceof java.sql.Date) {
				aPreparedStatement.setDate(index, (java.sql.Date)aValue);
			}
			if (aValue instanceof Time) {
				aPreparedStatement.setTime(index, (Time)aValue);
			}
			if (aValue instanceof Timestamp) {
				aPreparedStatement.setTimestamp(index, (Timestamp)aValue);
			}
			else {
				aPreparedStatement.setObject(index, aValue);
			}
		}
	}
}

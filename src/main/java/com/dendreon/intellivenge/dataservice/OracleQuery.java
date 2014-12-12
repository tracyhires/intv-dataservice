package com.dendreon.intellivenge.dataservice;

import java.lang.reflect.Array;
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
		private JoinParameter join;

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

		public QueryBuilder addJoin(JoinParameter aJoin) {
			join = aJoin;
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

			queryString.append("select" + SPACE);
			if (requestedColumns != null) {
				queryString.append(requestedColumns + SPACE);
			} else {
				queryString.append("*" + SPACE);
			}
			queryString.append("from" + SPACE);
			if (join != null) {
				switch(join.getJoinType()) {
				case INNER_JOIN:
					queryString.append(join.getLeftTableName() + " inner join " + join.getRightTableName() + SPACE);
					queryString.append("on " + join.getLeftTableName() + "." + join.getLeftColumnName());
					queryString.append("=" + join.getRightTableName() + "." + join.getRightColumnName());
					queryString.append(SPACE);
					break;
				case OUTER_JOIN:
					queryString.append(join.getLeftTableName() + "," + join.getRightTableName() + SPACE);
					queryString.append("where" + SPACE);
					queryString.append(join.getLeftTableName() + "." + join.getLeftColumnName());
					queryString.append("=" + join.getRightTableName() + "." + join.getRightColumnName());
					queryString.append("(+)" + SPACE);
					break;
				default:
					break;
				}
			}
			else {
				queryString.append(tableName + SPACE);
			}
			if (parameters.size() > 0) {
				Map<Integer,Object> preparedValues = new HashMap<Integer,Object>();
				//in case of outer join
				if (queryString.indexOf("where") == -1) {
					queryString.append("where" + SPACE);
				}
				else {
					queryString.append("and" + SPACE);
				}
				Iterator<QueryParameter> paramIter = parameters.iterator();
				int counter = 1;
				while (paramIter.hasNext()) {
					QueryParameter q = paramIter.next();
					switch(q.getQueryType()) {
					case EQ:
						queryString.append(q.getColumnName() + SPACE);
						queryString.append("=" + SPHS);
						preparedValues.put(counter++, q.getValue());
						break;
					case GT:
						queryString.append(q.getColumnName() + SPACE);
						queryString.append(">" + SPHS);
						preparedValues.put(counter++, q.getValue());
						break;
					case GTE:
						queryString.append(q.getColumnName() + SPACE);
						queryString.append(">=" + SPHS);
						preparedValues.put(counter++, q.getValue());
						break;
					case LT:
						queryString.append(q.getColumnName() + SPACE);
						queryString.append("<" + SPHS);
						preparedValues.put(counter++, q.getValue());
						break;
					case LTE:
						queryString.append(q.getColumnName() + SPACE);
						queryString.append("<=" + SPHS);
						preparedValues.put(counter++, q.getValue());
						break;
					case NEQ:
						queryString.append(q.getColumnName() + SPACE);
						queryString.append("!=" + SPHS);
						preparedValues.put(counter++, q.getValue());
						break;
					case CONTAINS:
						Object stringValue = q.getValue();
						if (stringValue instanceof String) {
							queryString.append("lower(" + q.getColumnName() + ")" + SPACE);
							queryString.append("like" + SPHS);
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
							queryString.append(q.getColumnName() + SPACE);
							queryString.append("in (");
							while (inIterator.hasNext()) {
								preparedValues.put(counter++, inIterator.next());
								queryString.append(PH);
								if (inIterator.hasNext()) {
									queryString.append(",");
								}
								else {
									queryString.append(")");
								}
							}
						}
						break;	
					}
					if (paramIter.hasNext()) {
						queryString.append("and" + SPACE);
					}
				}

				// create statement and add values
				pStatement = connection.prepareStatement(queryString.toString());
				for (Integer index : preparedValues.keySet()) {
					setValue(pStatement, index, preparedValues.get(index));
				}
			}
			return pStatement;
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

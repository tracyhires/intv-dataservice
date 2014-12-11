package com.dendreon.intellivenge.dataservice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.LoggerFactory;

public final class OracleQuery {

	public static class QueryBuilder {

		private final String SPACE = " ";
		private final String PH = "?";
		private final Connection connection;
		private String requestedColumns;
		private String tableName;
		private Set<QueryParameter> parameters;
		private QueryParameter join;

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
		
		public QueryBuilder addJoin(QueryParameter aJoin) {
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
			if (tableName != null) {
				queryString.append("select" + SPACE);
				if (requestedColumns != null) {
					queryString.append(requestedColumns + SPACE);
				} else {
					queryString.append("*" + SPACE);
				}
				queryString.append("from" + SPACE);
				queryString.append(tableName + SPACE);
/*				if (join != null) {
					switch(join.getQueryType()) {
					case INNER_JOIN:
						//"emp a, dept b WHERE a.deptno = b.deptno(+)"
						break;
					case OUTER_JOIN:
						queryString.append("t1," + SPACE + join.g)
						break;
					default:
						break;
					}
				}*/
				if (parameters.size() > 0) {
					Map<Integer,Object> preparedValues = new HashMap<Integer,Object>();
					queryString.append("where" + SPACE);
					Iterator<QueryParameter> paramIter = parameters.iterator();
					int counter = 1;
					while (paramIter.hasNext()) {
						QueryParameter q = paramIter.next();
						queryString.append(q.getColumnName() + SPACE);
						switch(q.getQueryType()) {
						case EQ:
							queryString.append("=" + SPACE + PH + SPACE);
							break;
						case GT:
							queryString.append(">" + SPACE + PH + SPACE);
							break;
						case GTE:
							queryString.append(">=" + SPACE + PH + SPACE);
							break;
						case LT:
							queryString.append("<" + SPACE + PH + SPACE);
							break;
						case LTE:
							queryString.append("<=" + SPACE + PH + SPACE);
							break;
						case NEQ:
							queryString.append("!=" + SPACE + PH + SPACE);
							break;
						case CONTAINS:
							queryString.append("contains(" + PH + ")" + SPACE);
							break;
						case IN:
							queryString.append("in(" + PH + ")" + SPACE);
							break;
						default:
							break;	
						}
						if (paramIter.hasNext()) {
							queryString.append("and" + SPACE);
						}
						preparedValues.put(counter++, q.getValue());
					}
					
					// create statement and add values
					pStatement = connection.prepareStatement(queryString.toString());
					for (Integer index : preparedValues.keySet()) {
						Object value = preparedValues.get(index);
						if (value instanceof Date) {
							LoggerFactory.getLogger(getClass()).info("Setting DATE");
							pStatement.setDate(index.intValue(), new java.sql.Date((((Date)value)).getTime()));
						}
						if (value instanceof String) {
							pStatement.setString(index.intValue(), (String)value);
						}
						if (value instanceof Integer) {
							pStatement.setInt(index.intValue(), ((Integer) value).intValue());
						}
						if (value instanceof Double) {
							pStatement.setDouble(index.intValue(), ((Double) value).doubleValue());
						}
						if (value instanceof Long) {
							pStatement.setLong(index.intValue(), ((Long) value).longValue());
						}
						if (value instanceof Boolean) {
							pStatement.setBoolean(index.intValue(), (Boolean)value);
						}
						if (value instanceof java.sql.Date) {
							pStatement.setDate(index.intValue(), (java.sql.Date)value);
						}
						if (value instanceof Time) {
							pStatement.setTime(index.intValue(), (Time)value);
						}
						if (value instanceof Timestamp) {
							pStatement.setTimestamp(index.intValue(), (Timestamp)value);
						}
						else {
							pStatement.setObject(index.intValue(), value);
						}
					}
				}
			}
			return pStatement;
		}
	}
}

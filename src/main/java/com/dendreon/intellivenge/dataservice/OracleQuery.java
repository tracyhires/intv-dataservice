package com.dendreon.intellivenge.dataservice;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class OracleQuery {

	public static class QueryBuilder {

		private final String SPACE = " ";
		private String requestedColumns;
		private String tableName;
		private Set<QueryParameter> parameters;
		private QueryParameter join;

		public  QueryBuilder() {
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

		public String build() {
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
					queryString.append("where" + SPACE);
					Iterator<QueryParameter> paramIter = parameters.iterator();
					while (paramIter.hasNext()) {
						QueryParameter q = paramIter.next();
						Object value = q.getValue();
						if (q.getValue() instanceof Date) {
							value = new java.sql.Date(((Date)value).getTime());
						}
						queryString.append(q.getColumnName() + SPACE);
						switch(q.getQueryType()) {
						case EQ:
							queryString.append("=" + SPACE + value + SPACE);
							break;
						case GT:
							queryString.append(">" + SPACE + value + SPACE);
							break;
						case GTE:
							queryString.append(">=" + SPACE + value + SPACE);
							break;
						case LT:
							queryString.append("<" + SPACE + value + SPACE);
							break;
						case LTE:
							queryString.append("<=" + SPACE + value + SPACE);
							break;
						case CONTAINS:
							queryString.append("contains(" + value + ")" + SPACE);
							break;
						case NEQ:
							queryString.append("!=" + SPACE + value + SPACE);
							break;
						case IN:
							queryString.append("in(" + value + ")" + SPACE);
							break;
						default:
							break;	
						}
						if (paramIter.hasNext()) {
							queryString.append("and" + SPACE);
						}
					}
				}
			}
			return queryString.toString();
		}
	}
}

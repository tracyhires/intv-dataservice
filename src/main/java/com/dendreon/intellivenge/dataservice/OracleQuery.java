package com.dendreon.intellivenge.dataservice;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class OracleQuery {

	public static class QueryBuilder {

		private final String SPACE = " ";
		private static String requestedColumns;
		private static String tableName;
		private static Set<QueryParameter> parameters;

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
				if (parameters.size() > 0) {
					queryString.append("where" + SPACE);
					Iterator<QueryParameter> paramIter = parameters.iterator();
					StringBuffer queryValues = new StringBuffer("values(");
					while (paramIter.hasNext()) {
						QueryParameter q = paramIter.next();
						queryString.append(q.getColumnName() + SPACE);
						switch(q.getQueryType()) {
						case EQ:
							queryString.append("=" + SPACE + q.getValue() + SPACE);
							break;
						case GT:
							queryString.append(">" + SPACE + q.getValue() + SPACE);
							break;
						case GTE:
							queryString.append(">=" + SPACE + q.getValue() + SPACE);
							break;
						case LT:
							queryString.append("<" + SPACE + q.getValue() + SPACE);
							break;
						case LTE:
							queryString.append("<=" + SPACE + q.getValue() + SPACE);
							break;
						case CONTAINS:
							queryString.append("contains(" + q.getValue() +")" + SPACE);
							break;
						case NEQ:
							queryString.append("!=" + SPACE + q.getValue() + SPACE);
							break;
						}
						queryValues.append(q.getValue());
						if (paramIter.hasNext()) {
							queryString.append("and" + SPACE);
							queryValues.append(",");
						}
						else {
							queryValues.append(")");
						}
					}
				}
			}
			return queryString.toString() ;
		}
	}
}

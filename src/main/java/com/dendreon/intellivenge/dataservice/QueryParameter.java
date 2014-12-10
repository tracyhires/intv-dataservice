package com.dendreon.intellivenge.dataservice;

public class QueryParameter {
	private String columnName;
	private QueryType queryType;
	private Object value;
	
	public QueryParameter(String aColumnName, QueryType aQueryType, Object aValue) {
		columnName = aColumnName;
		queryType = aQueryType;
		value = aValue;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public QueryType getQueryType() {
		return queryType;
	}

	public void setQueryType(QueryType queryType) {
		this.queryType = queryType;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}

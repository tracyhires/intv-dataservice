package com.dendreon.intellivenge.dataservice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class UpdateParameter {

	private String tableName;
	private Collection<QueryParameter> queryParameters;
	private HashMap<String, Object> columnValuePairs;

	public UpdateParameter(String aTableName) {
		tableName = aTableName;
	}
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String aTableName) {
		tableName = aTableName;
	}

	public HashMap<String, Object> getColumnValuePairs() {
		if (columnValuePairs == null) {
			columnValuePairs = new HashMap<String, Object>();
		}
		return columnValuePairs;
	}

	public Collection<QueryParameter> getQueryParameters() {
		if (queryParameters == null) {
			queryParameters = new ArrayList<QueryParameter>();
		}
		return queryParameters;
	}

}

package com.dendreon.intellivenge.dataservice;

import java.sql.ResultSet;

public interface DataService {
	ResultSet findRecords(String tableName, QueryParameter...queryParameters);

	ResultSet findRecords(String[] tableNames, QueryParameter join, QueryParameter[] queryParameters);
	
	
}

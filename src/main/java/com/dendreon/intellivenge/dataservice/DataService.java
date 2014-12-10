package com.dendreon.intellivenge.dataservice;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public interface DataService {
	
	ResultSet findRecords(String tableName, QueryParameter...queryParameters);

	ResultSet findRecords(String[] tableNames, QueryParameter join, QueryParameter[] queryParameters);
	
	ResultSetMetaData describeTable(String tabelname);
	
	
}

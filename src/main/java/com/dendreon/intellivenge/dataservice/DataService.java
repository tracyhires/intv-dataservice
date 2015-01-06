package com.dendreon.intellivenge.dataservice;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Collection;

public interface DataService {
	
	ResultSet findRecords(String tableName, QueryParameter...queryParameters);

	ResultSet findRecords(Collection<JoinParameter> joins, QueryParameter... queryParameters);
	
	ResultSet findRecords(String tableName, String[] aColumns, QueryParameter...queryParameters);

	ResultSet findRecords(String[] aColumns, Collection<JoinParameter> joins, QueryParameter... queryParameters);
	
	ResultSetMetaData describeTable(String tablename);
	
	Integer[] insertRecords(UpdateParameter[] aInserts) throws DataServiceException;
	
	void updateRecords(UpdateParameter[] aUpdates) throws DataServiceException;
	
	void deleteRecords(UpdateParameter[] aDeletes) throws DataServiceException;
}

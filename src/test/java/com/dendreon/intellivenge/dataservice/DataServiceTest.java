package com.dendreon.intellivenge.dataservice;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.slf4j.LoggerFactory;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import com.dendreon.intellivenge.dataservice.config.TestDataServiceModule;
import com.dendreon.test.db.config.JndiDbModule;

@Guice(modules = {TestDataServiceModule.class, JndiDbModule.class})
public class DataServiceTest {
	
	@Inject
	DataService service;
	
	@Test
	public void findRecordSimpleTest() throws SQLException {

		QueryParameter q = new QueryParameter("id", QueryType.EQ, new Long(101397));
		ResultSet resultSet = service.findRecords("doctor", q);
		resultSet.next();
		int id = resultSet.getInt("id");
		assertEquals(id, 101397);
	}
	
	@Test
	public void findRecordDateTest() throws SQLException, ParseException {
		DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
		Date date = format.parse("01/02/1950");
		QueryParameter q = new QueryParameter("date_of_birth", QueryType.LT, date);
		ResultSet resultSet = service.findRecords("patient", q);
		resultSet.next();
		int id = resultSet.getInt("id");
		assertEquals(id, 1234);
		q = new QueryParameter("date_of_birth", QueryType.LTE, date);
		resultSet = service.findRecords("patient", q);
		resultSet.next();
		id = resultSet.getInt("id");
		assertEquals(id, 1234);
		
		date = format.parse("01/01/1950");
		q = new QueryParameter("date_of_birth", QueryType.EQ, date);
		resultSet = service.findRecords("patient", q);
		resultSet.next();
		id = resultSet.getInt("id");
		assertEquals(id, 1234);
		q = new QueryParameter("date_of_birth", QueryType.LTE, date);
		resultSet = service.findRecords("patient", q);
		resultSet.next();
		id = resultSet.getInt("id");
		assertEquals(id, 1234);
		q = new QueryParameter("date_of_birth", QueryType.GTE, date);
		resultSet = service.findRecords("patient", q);
		resultSet.next();
		id = resultSet.getInt("id");
		assertEquals(id, 1234);
		
		date = format.parse("01/01/1949");
		q = new QueryParameter("date_of_birth", QueryType.GT, date);
		resultSet = service.findRecords("patient", q);
		resultSet.next();
		id = resultSet.getInt("id");
		assertEquals(id, 1234);
		q = new QueryParameter("date_of_birth", QueryType.GTE, date);
		resultSet = service.findRecords("patient", q);
		resultSet.next();
		id = resultSet.getInt("id");
		assertEquals(id, 1234);
	}
	
	@Test
	public void findRecordStringTest() throws SQLException {
		QueryParameter q = new QueryParameter("name", QueryType.EQ, "Doc Testerson");
		ResultSet resultSet = service.findRecords("doctor", q);
		resultSet.next();
		int id = resultSet.getInt("id");
		assertEquals(id, 101397);
		
		q = new QueryParameter("name", QueryType.CONTAINS, "Doc2");
		resultSet = service.findRecords("doctor", q);
		resultSet.next();
		id = resultSet.getInt("id");
		assertEquals(id, 101398);
		
		q = new QueryParameter("name", QueryType.CONTAINS, "DOC2");
		resultSet = service.findRecords("doctor", q);
		resultSet.next();
		id = resultSet.getInt("id");
		assertEquals(id, 101398);
	}
	
	@Test
	public void findRecordInTest() throws SQLException {
		List<Integer> values = new ArrayList<Integer>();
		values.add(new Integer(1234));
		values.add(new Integer(1235));
		QueryParameter q = new QueryParameter("id", QueryType.IN, values);
		ResultSet resultSet = service.findRecords("patient", q);
		resultSet.next();
		resultSet.last();
	    int size = resultSet.getRow();
	    assertEquals(size, 2);

	    q = new QueryParameter("id", QueryType.IN, new Integer[] {new Integer(1234),new Integer(1235)});
	    resultSet = service.findRecords("patient", q);
	    resultSet.next();
	    resultSet.last();
	    size = resultSet.getRow();
	    assertEquals(size, 2);
	}
	
	@Test
	public void findRecordNETest() throws SQLException {
		QueryParameter q = new QueryParameter("id", QueryType.NEQ, new Integer(1235));
		ResultSet resultSet = service.findRecords("patient", q);
		resultSet.next();
		resultSet.last();
	    int size = resultSet.getRow();
	    assertEquals(size, 4);
	}
	
	@Test
	public void findRecordNEColumnsTest() throws SQLException {
		QueryParameter q = new QueryParameter("id", QueryType.NEQ, new Integer(1235));
		String[] columns = {"id", "doctor_id", "gender", "ccid", "date_of_birth"};
		ResultSet resultSet = service.findRecords("patient", columns, q);
		resultSet.next();
		resultSet.last();
	    int size = resultSet.getRow();
	    assertEquals(size, 4);
	}
	
	@Test
	public void findRecordInnerJoinColumnsTest() throws SQLException {
		JoinParameter j = new JoinParameter("patient", "doctor", "doctor_id", "id", JoinType.INNER_JOIN);
		List<JoinParameter> joins = new ArrayList<JoinParameter>();
		joins.add(j);
		String[] columns = {"patient.id", "patient.gender", "patient.ccid", "patient.doctor_id", "doctor.id as doctor$id", "doctor.name", "doctor.is_clinical"};
		QueryParameter q = new QueryParameter("doctor.id", QueryType.EQ, new Integer(101397));
		ResultSet resultSet = service.findRecords(columns, joins, q);
		printResultSet(resultSet);
		resultSet.next();
		int doctorId = resultSet.getInt("doctor$id");
		String doctorName = resultSet.getString("name");
		boolean doctorIsClinical = resultSet.getBoolean("is_clinical");

		assertEquals(doctorId, 101397);
		assertEquals(doctorName, "Doc Testerson");
		assertEquals(doctorIsClinical, true);
		
		resultSet.last();
	    int size = resultSet.getRow();
	    assertEquals(size, 4);
	}
	
	@Test
	public void findRecordInnerJoinTest() throws SQLException {
		JoinParameter j = new JoinParameter("patient", "doctor", "doctor_id", "id", JoinType.INNER_JOIN);
		List<JoinParameter> joins = new ArrayList<JoinParameter>();
		joins.add(j);
		QueryParameter q = new QueryParameter("doctor.id", QueryType.EQ, new Integer(101397));
		ResultSet resultSet = service.findRecords(joins, q);
		resultSet.next();
		int doctorId = resultSet.getInt("doctor_id");
		String doctorName = resultSet.getString("name");
		boolean doctorIsClinical = resultSet.getBoolean("is_clinical");

		assertEquals(doctorId, 101397);
		assertEquals(doctorName, "Doc Testerson");
		assertEquals(doctorIsClinical, true);
		
		resultSet.last();
	    int size = resultSet.getRow();
	    assertEquals(size, 4);
	}
	
	@Test
	public void findRecordOuterJoinTest() throws SQLException {
		JoinParameter j = new JoinParameter("patient", "doctor", "doctor_id", "id", JoinType.OUTER_JOIN);
		List<JoinParameter> joins = new ArrayList<JoinParameter>();
		joins.add(j);
		QueryParameter q = new QueryParameter("doctor.is_clinical", QueryType.EQ, new Boolean(true));
		ResultSet resultSet = service.findRecords(joins, q);
		resultSet.next();
		int doctorId = resultSet.getInt("doctor_id");
		String doctorName = resultSet.getString("name");
		boolean doctorIsClinical = resultSet.getBoolean("is_clinical");

		assertTrue((doctorId == 101397) || (doctorId == 101398));
		assertTrue(doctorName.equalsIgnoreCase("Doc Testerson") || doctorName.equalsIgnoreCase("Doc2 Testerson2"));
		assertEquals(doctorIsClinical, true);
		
		resultSet.last();
	    int size = resultSet.getRow();
	    assertEquals(size, 5);
	}
	
	@Test
	public void findRecordMultpleJoinTest() throws SQLException {
		JoinParameter j1 = new JoinParameter("patient", "doctor", "doctor_id", "id", JoinType.OUTER_JOIN);
		JoinParameter j2 = new JoinParameter("regimen", "patient", "patient_id", "id", JoinType.INNER_JOIN);
		List<JoinParameter> joins = new ArrayList<JoinParameter>();
		joins.add(j1);
		joins.add(j2);
		QueryParameter q = new QueryParameter("doctor.is_clinical", QueryType.EQ, new Boolean(true));
		ResultSet resultSet = service.findRecords(joins, q);
		resultSet.next();
		int doctorId = resultSet.getInt("doctor_id");
		String doctorName = resultSet.getString("name");
		boolean doctorIsClinical = resultSet.getBoolean("is_clinical");
		int orderNumber = resultSet.getInt("order_number");

		assertTrue((doctorId == 101397) || (doctorId == 101398));
		assertTrue(doctorName.equalsIgnoreCase("Doc Testerson") || doctorName.equalsIgnoreCase("Doc2 Testerson2"));
		assertEquals(doctorIsClinical, true);
		assertTrue(orderNumber == 9999 || orderNumber == 9998 || orderNumber == 9997 || orderNumber == 9996);
		
		resultSet.last();
	    int size = resultSet.getRow();
	    assertEquals(size, 4);
	}
	
	@Test
	public void findRecordSqlDateTest() throws SQLException, ParseException {
		DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
		Date date = format.parse("01/02/1950");
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		QueryParameter q = new QueryParameter("date_of_birth", QueryType.LT, sqlDate);
		ResultSet resultSet = service.findRecords("patient", q);
		resultSet.next();
		int id = resultSet.getInt("id");
		assertEquals(id, 1234);
		
		Timestamp timeStamp = new Timestamp(date.getTime());
		q = new QueryParameter("date_of_birth", QueryType.LT, timeStamp);
		resultSet = service.findRecords("patient", q);
		resultSet.next();
		id = resultSet.getInt("id");
		assertEquals(id, 1234);
	}
	
	@Test
	public void findRecordDoubleTest() throws SQLException {
		QueryParameter q = new QueryParameter("latitude", QueryType.EQ, 10.22);
		ResultSet resultSet = service.findRecords("geolocation", q);
		resultSet.next();
		int id = resultSet.getInt("id");
		assertEquals(id, -10);
	}
	
	@Test
	public void findRecordAllTest() throws SQLException {
		ResultSet resultSet = service.findRecords("regimen", new QueryParameter[] {});

		resultSet.last();
	    int size = resultSet.getRow();
	    assertEquals(size, 4);
	}
	
	@Test
	public void describeTableTest() throws SQLException {
		
		ResultSetMetaData meta = service.describeTable("patient");
		assertEquals(meta.getTableName(1).toLowerCase(), "patient");
		assertEquals(meta.getColumnCount(), 23);
	}
	
	@Test
	public void insertTest() throws SQLException, DataServiceException {
		UpdateParameter insert = new UpdateParameter("doctor_apheresis_site");
		insert.getColumnValuePairs().put("id", "seq_doctor_resource_id.NEXTVAL");
		insert.getColumnValuePairs().put("doctor_id", 101399);
		insert.getColumnValuePairs().put("resource_id", -1000);
		insert.getColumnValuePairs().put("organization_rank", 4);
		
		UpdateParameter insert2 = new UpdateParameter("doctor_apheresis_site");
		insert2.getColumnValuePairs().put("id", "seq_doctor_resource_id.NEXTVAL");
		insert2.getColumnValuePairs().put("doctor_id", 101399);
		insert2.getColumnValuePairs().put("resource_id", -1010);
		insert2.getColumnValuePairs().put("organization_rank", 5);
		
		Integer[] newItems = service.insertRecords(new UpdateParameter[] {insert, insert2});
		
		QueryParameter q = new QueryParameter("id", QueryType.IN, newItems);
		ResultSet resultSet = service.findRecords("doctor_apheresis_site", q);
		resultSet.next();
		int did = resultSet.getInt("doctor_id");
		assertEquals(did, 101399);
		int rid = resultSet.getInt("resource_id");
		assertTrue(rid == -1000 || rid == -1010);
		int rank = resultSet.getInt("organization_rank");
		assertTrue(rank == 4 || rank == 5);
		
		resultSet.last();
	    int size = resultSet.getRow();
	    assertEquals(size, 2);	
	}
	
	@Test
	public void updateTest() throws SQLException, DataServiceException {
		
		UpdateParameter insert = new UpdateParameter("doctor_apheresis_site");
		insert.getColumnValuePairs().put("id", "seq_doctor_resource_id.NEXTVAL");
		insert.getColumnValuePairs().put("doctor_id", 101397);
		insert.getColumnValuePairs().put("resource_id", -1020);
		insert.getColumnValuePairs().put("organization_rank", 1);
		service.insertRecords(new UpdateParameter[] {insert});
		
		UpdateParameter update = new UpdateParameter("doctor_apheresis_site");
		update.getColumnValuePairs().put("doctor_id", 101397);
		update.getColumnValuePairs().put("resource_id", -1020);
		update.getColumnValuePairs().put("organization_rank", 9);
		update.getQueryParameters().add(new QueryParameter("doctor_id", QueryType.EQ, 101397));
		update.getQueryParameters().add(new QueryParameter("resource_id", QueryType.EQ, -1020));
		
		service.updateRecords(new UpdateParameter[] {update});
		
		QueryParameter q = new QueryParameter("doctor_id", QueryType.EQ, 101397);
		QueryParameter q2 = new QueryParameter("resource_id", QueryType.EQ, -1020);
		ResultSet resultSet = service.findRecords("doctor_apheresis_site", q, q2);
		resultSet.next();
		int did = resultSet.getInt("doctor_id");
		assertEquals(did, 101397);
		int rid = resultSet.getInt("resource_id");
		assertEquals(rid, -1020);
		int rank = resultSet.getInt("organization_rank");
		assertEquals(rank, 9);
	}
	
	@Test
	public void deleteTest() throws SQLException, DataServiceException {
		
		UpdateParameter insert = new UpdateParameter("doctor_apheresis_site");
		insert.getColumnValuePairs().put("id", "seq_doctor_resource_id.NEXTVAL");
		insert.getColumnValuePairs().put("doctor_id", 101397);
		insert.getColumnValuePairs().put("resource_id", -1030);
		insert.getColumnValuePairs().put("organization_rank", 5);
		service.insertRecords(new UpdateParameter[] {insert});
		
		UpdateParameter delete = new UpdateParameter("doctor_apheresis_site");
		delete.getQueryParameters().add(new QueryParameter("doctor_id", QueryType.EQ, 101397));
		delete.getQueryParameters().add(new QueryParameter("resource_id", QueryType.EQ, -1030));
		delete.getQueryParameters().add(new QueryParameter("organization_rank", QueryType.EQ, 5));
		
		service.deleteRecords(new UpdateParameter[] {delete});
		
		QueryParameter q = new QueryParameter("doctor_id", QueryType.EQ, 101397);
		QueryParameter q2 = new QueryParameter("resource_id", QueryType.EQ, -1030);
		QueryParameter q3 = new QueryParameter("organization_rank", QueryType.EQ, 5);
		ResultSet resultSet = service.findRecords("doctor_apheresis_site", q, q2, q3);
		
		assertFalse(resultSet.next());
	}
	
	private void printResultSet(ResultSet aResultSet) throws SQLException {
		ResultSetMetaData rsMetaData = aResultSet.getMetaData();
		int columnsNumber = rsMetaData.getColumnCount();
		while (aResultSet.next()) {
			for (int i = 1; i <= columnsNumber; i++) {
				if (i > 1) System.out.print(",  ");
				String columnValue = aResultSet.getString(i);
				System.out.print(columnValue + " " + rsMetaData.getColumnName(i));
			}
			System.out.println("");
		}

		aResultSet.first();
	}

}

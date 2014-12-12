package com.dendreon.intellivenge.dataservice;

import static org.testng.Assert.assertEquals;
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

}

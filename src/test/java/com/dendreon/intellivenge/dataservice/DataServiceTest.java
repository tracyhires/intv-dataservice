package com.dendreon.intellivenge.dataservice;

import static org.testng.Assert.assertEquals;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
	public void findRecordSimpleTest() throws SQLException, ParseException {

		QueryParameter q = new QueryParameter("id", QueryType.EQ, new Integer(101397));
		ResultSet resultSet = service.findRecords("doctor", q);
		resultSet.next();
		int id = resultSet.getInt("id");
		assertEquals(id, 101397);
	}
	
	@Test
	public void findRecordDateTest() throws ParseException, SQLException {
		DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
		Date date = format.parse("01/02/1950");
		QueryParameter q = new QueryParameter("date_of_birth", QueryType.LT, date);
		ResultSet resultSet = service.findRecords("patient", q);
		resultSet.next();
		int id = resultSet.getInt("id");
		assertEquals(id, 1234);
	}
	
	@Test
	public void findRecordInnerJoinTest() throws SQLException {
		JoinParameter j = new JoinParameter("patient", "doctor", "doctor_id", "id", JoinType.INNER_JOIN);
		QueryParameter q = new QueryParameter("doctor.id", QueryType.EQ, new Integer(101397));
		ResultSet resultSet = service.findRecords(j, q);
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
		QueryParameter q = new QueryParameter("doctor.is_clinical", QueryType.EQ, new Boolean(true));
		ResultSet resultSet = service.findRecords(j, q);
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
	public void describeTableTest() throws SQLException {
		
		ResultSetMetaData meta = service.describeTable("patient");
		assertEquals(meta.getTableName(1).toLowerCase(), "patient");
		assertEquals(meta.getColumnCount(), 23);
	}

}

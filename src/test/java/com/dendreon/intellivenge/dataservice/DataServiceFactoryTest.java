package com.dendreon.intellivenge.dataservice;

import static org.testng.Assert.assertNotNull;

import org.testng.annotations.Test;

public class DataServiceFactoryTest {
	
  @Test
  public void createDataServiceTest() {
	  
	  DataServiceFactory factory = new DataServiceFactory();
	  DataService service = factory.createDataService();
	  assertNotNull(service);
  }
}

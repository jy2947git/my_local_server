package com.focaplo.mylocal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.gson.Gson;



public class SaleServiceTest extends LocalDatastoreTest{
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private SaleItem createSaleItem() throws ParseException{
		SaleItem item = new SaleItem();
		item.setUserUniqueId("seller123");
		item.setAddress1("2200 Yale Cir");
		item.setDescription("this is a test1");
		
		item.setLatitude(Double.parseDouble("100.1234"));
		item.setLongitude(Double.parseDouble("-20.1234"));
		Calendar beginDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		endDate.add(Calendar.DAY_OF_YEAR, -13);
		item.setStartDateDate(beginDate.getTime());
		item.setEndDateDate(endDate.getTime());
		
		item.getImages().add("http://images.google.com/22");
		item.getImages().add("http://images.google.com/23");
		item.getImages().add("http://images.google.com/24");
		return item;
	}
	@Test
	public void testTranslateSaleItemToJsonString() throws ParseException{
		SaleItem si = this.createSaleItem();
		SaleService test = new SaleService();
		java.lang.System.out.println(si);
		java.lang.System.out.println(test.toJson(si));
	}
	@Test
	public void testTranslateJsonStringToSaleItem() throws ParseException{
		SaleItem si = this.createSaleItem();
		SaleService test = new SaleService();
		
		String jsonStr = test.toJson(si);
		
		SaleItem si2 = test.fromJson(jsonStr);
	}
	
	@Test
	public void testTranslateBrowseResultToJson() throws ParseException{
		List items = new ArrayList();
		items.add(this.createSaleItem());
		items.add(this.createSaleItem());
		items.add(this.createSaleItem());
		SaleService test = new SaleService();
		System.out.println(test.browseResultsToJson(items));
	}
	
	@Test
	public void testAdd() throws ParseException{
		SaleService test = new SaleService();
		{

			String id = test.saveItem(this.createSaleItem());
			java.lang.System.out.println("saved " + id);
		}
		
		

	}
	@Test
	public void testSearchDataStore(){
		//search
		SaleService test = new SaleService();
		java.lang.System.out.println(test.browseWithPage(0, 500));
		java.lang.System.out.println(test.searchItemByLocationAndDateWithPage(0, 100, 100.12345, -20.12345));
		java.lang.System.out.println();
	}
	@Test
	public void testHandlingSearchResult() throws ParseException{
		//search result
		List items = new ArrayList();
		items.add(this.createSaleItem());
		items.add(this.createSaleItem());
		items.add(this.createSaleItem());

		
		SaleService test = new SaleService();
		String jsonresult = test.browseResultsToJson(items);
		Gson gson = new Gson();
		RequestResult mapResult = gson.fromJson(jsonresult, RequestResult.class);
		System.out.println(mapResult);
		java.lang.System.out.println(mapResult.getStatus());
		List<SaleItem> items2 = (List<SaleItem>)mapResult.getData();
		for(SaleItem si : items2){
			System.out.println(si);
		}
	}
	@Test
	public void testBrowseAll(){
		SaleService test = new SaleService();
		java.lang.System.out.println(test.browseWithPage(0, 500));
		
	}
	@Test
	public void testDeleteOld(){
		SaleService test = new SaleService();
		Calendar date = Calendar.getInstance();
		date.add(Calendar.DAY_OF_YEAR, 100);
		test.deleteOldRecordsBeforeDate(date.getTime());
	
		java.lang.System.out.println(test.browseWithPage(0, 50));
		
	}
}

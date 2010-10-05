package com.focaplo.mylocal;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;



public class SaleServiceTest extends LocalDatastoreTest{
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private Sale createSaleItem() throws ParseException{
		Sale item = new Sale();
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
		
		
		return item;
	}
	@Test
	public void testTranslateSaleItemToJsonString() throws ParseException{
		Sale si = this.createSaleItem();
		SaleService test = new SaleService();
		java.lang.System.out.println(si);
		java.lang.System.out.println(test.toJson(si));
	}
	@Test
	public void testTranslateJsonStringToSaleItem() throws ParseException{
		Sale si = this.createSaleItem();
		SaleService test = new SaleService();
		
		String jsonStr = test.toJson(si);
		
		Sale si2 = test.fromJson(jsonStr);
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
			Sale item = this.createSaleItem();
			String res = test.saveSale(item);
			java.lang.System.out.println("saved item:" + res);
			//save image
			String jsonRes = test.saveSaleImage(item.getSaleId(), "aoaao");
			java.lang.System.out.println("Save Image:"+jsonRes);
			Gson gson = new Gson();
			Type parameterizedType = new TypeToken<RequestResult<ImageInfo>>() {}.getType();
			
			RequestResult<ImageInfo> rs = gson.fromJson(jsonRes, parameterizedType);
			ImageInfo imageInfo = (ImageInfo)rs.getData().get(0);
			//save icon image
			java.lang.System.out.println("Save Icon image:"+test.saveSaleIconImage(imageInfo.getImageId(), "aoaao"));
			//search
			System.out.println("Search Images:" + test.getSaleImages(item.getSaleId()));
		}
		
		

	}
	@Test
	public void testSearchDataStore(){
		//search
		SaleService test = new SaleService();
		java.lang.System.out.println(test.browseWithPage(0, 500));
		java.lang.System.out.println(test.searchSaleByLocationAndDateRange(0, 100, 100.12345, -20.12345));
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
		List<Serializable> items2 = (List<Serializable>)mapResult.getData();
		for(Serializable si : items2){
			
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

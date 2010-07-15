package com.focaplo.mylocal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.focaplo.common.LocalServiceTestCase;

public class SaleServiceTest extends LocalServiceTestCase{

	public void testAdd() throws ParseException{
		SaleService test = new SaleService();
		{

			Calendar beginDate = Calendar.getInstance();
			Calendar endDate = Calendar.getInstance();
			endDate.add(Calendar.DAY_OF_YEAR, -13);

			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String id = test.saveItem(null,"user123","address1","test4","100.1234","-20.1234",sdf.format(beginDate.getTime()), sdf.format(endDate.getTime()));
			System.out.println("saved " + id);
		}
		
		

	}
	
	public void testSearch(){
		//search
		SaleService test = new SaleService();
		List<SaleItem> resultes = test.browseWithPage(0, 500);
		System.out.println("browing all");
		for(int i=0; i<resultes.size();i++){
			System.out.println((SaleItem)resultes.get(i));
		}
		System.out.println("filtering");
		List<SaleItem> resultes2 = test.searchItemByLocationAndDateWithPage(0, 100, 100.12345, -20.12345);
		for(int i=0; i<resultes2.size();i++){
			System.out.println((SaleItem)resultes2.get(i));
		}
	}
	
	public void testBrowseAll(){
		SaleService test = new SaleService();
		List<SaleItem> resultes = test.browseWithPage(0, 500);
		for(int i=0; i<resultes.size();i++){
			System.out.println((SaleItem)resultes.get(i));
		}
	}
	
	public void testDeleteOld(){
		SaleService test = new SaleService();
		Calendar date = Calendar.getInstance();
		date.add(Calendar.DAY_OF_YEAR, 100);
		test.deleteOldRecordsBeforeDate(date.getTime());
	
		List<SaleItem> resultes = test.browseWithPage(0, 50);
		for(int i=0; i<resultes.size();i++){
			System.out.println((SaleItem)resultes.get(i));
		}
	}
}

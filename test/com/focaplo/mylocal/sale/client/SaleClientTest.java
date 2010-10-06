package com.focaplo.mylocal.sale.client;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import com.focaplo.mylocal.sale.client.SaleClient;
import com.focaplo.mylocal.sale.model.ImageInfo;
import com.focaplo.mylocal.sale.model.Sale;

public class SaleClientTest {

	SaleClient client = new SaleClient();
	@Test
	public void testGetImagesOfSale() throws Exception{
		List<ImageInfo> images = client.downloadImagesOfSale(new Long("46001"));
		for(ImageInfo image:images){
			System.out.println(image.getImageId() + " " + image.getSaleId() + " " + image.getImageBlobKey() + " " + image.getImageIconBlobKey());
		}
	}
	
	@Test
	public void testAddSaleAndImages() throws Exception{
		Sale item = new Sale();
		item.setUserUniqueId("seller111");
		item.setAddress1("2200 Yale Cir");
		item.setCity("Hoffman Estates");
		item.setState("IL");
		item.setZipcode("60192");
		item.setCountryCode("US");
		item.setDescription("this is a test");
		double mylati=41.881111;
		double mylagi=-87.632371;
		item.setLatitude(Double.valueOf(mylati));
		item.setLongitude(Double.valueOf(mylagi));
		Calendar beginDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		endDate.add(Calendar.DAY_OF_YEAR, 13);
		item.setStartDateDate(beginDate.getTime());
		item.setEndDateDate(endDate.getTime());
		//
		List<String> imagePaths = new ArrayList<String>();
		imagePaths.add("/Users/jyou00/Downloads/photo.JPG");
		imagePaths.add("/Users/jyou00/Downloads/photo.JPG");
		client.uploadNewSale(item, imagePaths);
	}
}

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
	public void testAddSale() throws Exception{
		Sale item = new Sale();
		item.setUserUniqueId("seller111");
		item.setAddress1("2200 Yale Cir");
		item.setCity("Hoffman Estates");
		item.setState("IL");
		item.setZipcode("60192");
		item.setCountryCode("US");
		item.setDetail("this is a test");
		//downtown
//		double mylati=41.881111;
//		double mylagi=-87.632371;
		//hoffman
		double mylati=42.064848;
		double mylagi=-88.219515;
		item.setLatitude(Double.valueOf(mylati));
		item.setLongitude(Double.valueOf(mylagi));
		Calendar beginDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		endDate.add(Calendar.DAY_OF_YEAR, 13);
		item.setStartDateDate(beginDate.getTime());
		item.setEndDateDate(endDate.getTime());
		client.uploadSaleOnly(item);
	}
	
	@Test
	public void testUploadImages() throws Exception{
		client.uploadSaleImage(new Long("39002"), "/Users/jyou00/Downloads/photo.JPG", Boolean.TRUE);
		client.uploadSaleImage(new Long("39002"), "/Users/jyou00/Downloads/photo.JPG", Boolean.FALSE);
		client.uploadSaleImage(new Long("39002"), "/Users/jyou00/Downloads/photo.JPG", Boolean.FALSE);
	}
	
	@Test
	public void testFinalizeSale() throws Exception{
		client.finalizeSale(new Long(""));
	}
	
	@Test
	public void testDeleteImage() throws Exception{
		client.deleteSaleImage(new Long(""), new Long(""));
	}
	
	@Test
	public void testDeleteSale() throws Exception{
		client.deleteSale(new Long(""));
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
		item.setDetail("this is a test");
		//downtown
//		double mylati=41.881111;
//		double mylagi=-87.632371;
		//hoffman
		double mylati=42.064848;
		double mylagi=-88.219515;
		item.setLatitude(Double.valueOf(mylati));
		item.setLongitude(Double.valueOf(mylagi));
		Calendar beginDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		endDate.add(Calendar.DAY_OF_YEAR, 13);
		item.setStartDateDate(beginDate.getTime());
		item.setEndDateDate(endDate.getTime());
		//
		List<String> imagePaths = new ArrayList<String>();
		imagePaths.add("/Users/jyou00/Downloads/photo.PNG");
		imagePaths.add("/Users/jyou00/Downloads/photo.PNG");
		client.uploadNewSale(item, imagePaths);
	}
	
	@Test
	public void testBrowse() throws Exception{
		double mylati=41.881111;
		double mylagi=-87.632371;
		List<Sale> sales = client.browse(new Double(mylati), new Double(mylagi));
		for(Sale s : sales){
			System.out.println(s);
		}
		//now retrieve images
		for(Sale s : sales){
			List<ImageInfo> images = client.downloadImagesOfSale(s.getSaleId());
			for(ImageInfo info : images){
				System.out.println(info.getImageId() + " " + info.getImageBlobKey() + " " + info.getImageIconBlobKey());
			}
		}
	}
}

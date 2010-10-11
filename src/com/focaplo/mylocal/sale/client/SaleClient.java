package com.focaplo.mylocal.sale.client;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.List;

import com.focaplo.mylocal.sale.model.ImageInfo;
import com.focaplo.mylocal.sale.model.Sale;
import com.focaplo.mylocal.sale.server.yardsaleServlet;
import com.focaplo.mylocal.sale.service.RequestResult;
import com.focaplo.mylocal.sale.service.SaleService;
import com.focaplo.mylocal.utils.NetUtility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * This is the Java client of the sale server. It sends the appropriate requests to the server to upload/delete/change/browse sale
 * and images, through the NetUtility.
 *
 */
public class SaleClient {
	SaleService service = new SaleService();
	String saleServer="http://senselocal.appspot.com/yardsale";
	String blobServer = "http://senselocal.appspot.com/BlobDataServlet";
	public void uploadNewSale(Sale sale, List<String> imageFilePaths) throws Exception{
		//upload sale
		
		String jsonAddItem = NetUtility.doPost(saleServer, new String[]{"token", "command","data"}, new String[]{yardsaleServlet.token,"upload", service.toJson(sale)});
		System.out.println(jsonAddItem);
		//find the sale id from the json result
		Gson gson = new Gson();
		Type parameterizedType = new TypeToken<RequestResult<Sale>>() {}.getType();
		RequestResult<Sale> rr = gson.fromJson(jsonAddItem, parameterizedType);
		System.out.println(rr.getStatus());
		if("good".equalsIgnoreCase(rr.getStatus())){
			Long saleId = rr.getData().get(0).getSaleId();
			System.out.println("sale:" + saleId);
			//send a GET to retrieve the upload url
			for(String filePath : imageFilePaths){
				String uploadURL = NetUtility.doGet(blobServer);
				System.out.println(uploadURL);
				
				//upload the image and trigger the resizing task
				NetUtility.uploadFile(uploadURL, new String[]{"saleId","isUsedAsSaleIcon"}, 
						new String[]{saleId.toString(),"1"}, 
						filePath.toLowerCase().endsWith("png")?"image/png":(filePath.toLowerCase().endsWith("jpeg")||filePath.toLowerCase().endsWith("jpg"))?"image/jpg":"application/octet-stream", 
						filePath);
			}
		}
	}
	
	public void uploadSaleOnly(Sale sale) throws Exception{
		//upload sale
		
		String jsonAddItem = NetUtility.doPost(saleServer, new String[]{"token", "command","data"}, new String[]{yardsaleServlet.token,"upload", service.toJson(sale)});
		System.out.println(jsonAddItem);

	}
	
	public void uploadSaleImage(Long saleId, String filePath, Boolean isUsedAsSaleIcon) throws Exception{
		String uploadURL = NetUtility.doGet(blobServer);
		System.out.println(uploadURL);
		
		//upload the image and trigger the resizing task
		NetUtility.uploadFile(uploadURL, new String[]{"saleId", "isUsedAsSaleIcon"}, 
				new String[]{saleId.toString(), isUsedAsSaleIcon?"1":"0"}, 
				filePath.toLowerCase().endsWith("png")?"image/png":(filePath.toLowerCase().endsWith("jpeg")||filePath.toLowerCase().endsWith("jpg"))?"image/jpg":"application/octet-stream", 
				filePath);
	}
	
	public void deleteSaleImage(Long saleId, Long imageInfoId) throws Exception{
		String jsonRemoveItem = NetUtility.doPost(saleServer, new String[]{"token", "command","imageId"}, new String[]{yardsaleServlet.token,"deleteImage", imageInfoId.toString()});
		System.out.println(jsonRemoveItem);
	}
	
	public void deleteSale(Long saleId) throws Exception{
		String jsonRemoveItem = NetUtility.doPost(saleServer, new String[]{"token", "command","id"}, new String[]{yardsaleServlet.token,"delete", saleId.toString()});
		System.out.println(jsonRemoveItem);
	}
	
	public void finalizeSale(Long saleId) throws Exception{
		String jsonRemoveItem = NetUtility.doPost(saleServer, new String[]{"token", "command","id"}, new String[]{yardsaleServlet.token,"delete", saleId.toString()});
		System.out.println(jsonRemoveItem);
	}
	public Sale downloadSale(Long saleId) throws Exception{
		String jsonGetItem = NetUtility.doGet(saleServer + "?token=" + yardsaleServlet.token + "&command=download&id="+saleId.toString());
		//decode the JSON response
		Gson gson = new Gson();
		Type parameterizedType = new TypeToken<RequestResult<Sale>>() {}.getType();
		RequestResult<Sale> rr = gson.fromJson(jsonGetItem, parameterizedType);
		System.out.println(rr.getStatus());
		if("good".equalsIgnoreCase(rr.getStatus())){
			return rr.getData().get(0);
		}else{
			return null;
		}

	}
	
	public List<ImageInfo> downloadImagesOfSale(Long saleId) throws Exception{
		String jsonGetItem = NetUtility.doGet(saleServer+"?token=" + yardsaleServlet.token + "&command=images&id="+saleId.toString());
		//decode the JSON response
		Gson gson = new Gson();
		Type parameterizedType = new TypeToken<RequestResult<ImageInfo>>() {}.getType();
		RequestResult<ImageInfo> rr = gson.fromJson(jsonGetItem, parameterizedType);
		System.out.println(rr.getStatus());
		if("good".equalsIgnoreCase(rr.getStatus())){
			return rr.getData();
		}else{
			return null;
		}

	}
	
	public List<Sale> browse(Double latitude, Double longitude) throws Exception{
		String jsonBrowse = NetUtility.doGet(saleServer+"?token=" + yardsaleServlet.token + "&command=browse&latitude="+latitude.toString()+"&longitude="+longitude.toString());
		//decode the JSON response
		Gson gson = new Gson();
		Type parameterizedType = new TypeToken<RequestResult<Sale>>() {}.getType();
		RequestResult<Sale> rr = gson.fromJson(jsonBrowse, parameterizedType);
		System.out.println(rr.getStatus());
		if("good".equalsIgnoreCase(rr.getStatus())){
			return rr.getData();
		}else{
			return null;
		}
	}
}

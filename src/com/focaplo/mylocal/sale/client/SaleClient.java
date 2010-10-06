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

public class SaleClient {
	SaleService service = new SaleService();
	public void uploadNewSale(Sale sale, List<String> imageFilePaths) throws Exception{
		String server="http://senselocal.appspot.com/yardsale";
		//upload sale
		
		String jsonAddItem = NetUtility.doPost(server, new String[]{"token", "command","data"}, new String[]{yardsaleServlet.token,"upload", service.toJson(sale)});
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
				String uploadURL = NetUtility.doGet("http://senselocal.appspot.com/BlobDataServlet");
				System.out.println(uploadURL);
				
				//upload the image and trigger the resizing task
				NetUtility.uploadFile(uploadURL, new String[]{"saleId"}, 
						new String[]{saleId.toString()}, 
						filePath.toLowerCase().endsWith("png")?"image/png":(filePath.toLowerCase().endsWith("jpeg")||filePath.toLowerCase().endsWith("jpg"))?"image/jpg":"application/octet-stream", 
						filePath);
			}
		}
	}
	
	public Sale downloadSale(Long saleId) throws Exception{
		String jsonGetItem = NetUtility.doGet("http://senselocal.appspot.com/yardsale?token=" + yardsaleServlet.token + "&command=download&id="+saleId.toString());
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
		String jsonGetItem = NetUtility.doGet("http://senselocal.appspot.com/yardsale?token=" + yardsaleServlet.token + "&command=images&id="+saleId.toString());
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
}

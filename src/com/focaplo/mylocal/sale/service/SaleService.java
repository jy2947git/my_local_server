package com.focaplo.mylocal.sale.service;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.Transactional;

import org.apache.log4j.Logger;

import com.focaplo.common.Geohash;
import com.focaplo.mylocal.sale.model.ImageInfo;
import com.focaplo.mylocal.sale.model.Sale;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class SaleService {
	double latitude_sope=0.3; // 30 KM square
	double longitude_scope=3;
//	int date_scope=7;
	protected final Logger log = Logger.getLogger(this.getClass());
	Geohash geohash = new Geohash();
	
	public Sale fromJson(String jsonString){
		Gson gson = new Gson();
		return gson.fromJson(jsonString, Sale.class);
	}
	
	public String toJson(Sale sale){
		Gson gson = new Gson();
		return gson.toJson(sale, Sale.class);
	}
	
	public String getResultToJson(Sale item){
		Gson gson = new Gson();
		RequestResult<Sale> rr = new RequestResult<Sale>();
		rr.setGood();
		rr.getData().add(item);
		Type parameterizedType = new TypeToken<RequestResult<Sale>>() {}.getType();
		return gson.toJson(rr, parameterizedType);
	}
	
	public String browseResultsToJson(List<Sale> items){
		Gson gson = new Gson();
		RequestResult<Sale> rr = new RequestResult<Sale>();
		rr.setGood();
		rr.getData().addAll(items);
		Type parameterizedType = new TypeToken<RequestResult<Sale>>() {}.getType();
		return gson.toJson(rr, parameterizedType);
	}
	
	public String saveResultToJson(Sale item){
		Gson gson = new Gson();
		RequestResult<Sale> rr = new RequestResult<Sale>();
		rr.setGood();
		rr.getData().add(item);
		Type parameterizedType = new TypeToken<RequestResult<Sale>>() {}.getType();
		return gson.toJson(rr, parameterizedType);
	}
	
	public String removeResultToJson(){
		Gson gson = new Gson();
		RequestResult<ImageInfo> rr = new RequestResult<ImageInfo>();
		rr.setGood();
		Type parameterizedType = new TypeToken<RequestResult<Sale>>() {}.getType();
		return gson.toJson(rr, parameterizedType);
	}
	
	public String errorResultToJson(Exception exception){
		Gson gson = new Gson();
		RequestResult<Sale> rr = new RequestResult<Sale>();
		rr.setError(exception);
		
		Type parameterizedType = new TypeToken<RequestResult<Sale>>() {}.getType();
		return gson.toJson(rr, parameterizedType);
	}
	
	@Transactional
	public String updateSaleStatus(Long saleId, String status){
		//set the status of the image-info to be invalid only
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			Sale s = pm.getObjectById(Sale.class, saleId);
			s.setStatus(status);
			pm.makePersistent(s);
			return this.saveResultToJson(s);
		}catch(Exception e){
			return errorResultToJson(e);
		}finally{
			pm.close();
		}
	}
	
	@Transactional
	public String deleteSaleSoft(Long saleId){
		//set the status of the image-info to be invalid only
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			//first delete all the images associated with this sale
			List<ImageInfo> images = this.getSaleImages(saleId);
			for(ImageInfo ii:images){
				//change the status of the ImageInfo to be "invalid"
				this.deleteImageSoft(ii.getImageId());
			}
			Sale s = pm.getObjectById(Sale.class, saleId);
			s.setStatus("invalid");
			pm.makePersistent(s);
			return this.removeResultToJson();
		}catch(Exception e){
			return errorResultToJson(e);
		}finally{
			pm.close();
		}
	}
	@Transactional
	public String deleteSale(Long itemId){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			//first delete all the images associated with this sale
			List<ImageInfo> images = this.getSaleImages(itemId);
			for(ImageInfo ii:images){
				this.deleteImage(ii.getImageId());
			}
			pm.deletePersistent(pm.getObjectById(Sale.class, itemId));
			return removeResultToJson();
		}catch(Exception e){
			return errorResultToJson(e);
		}finally{
			pm.close();
		}
	}
	@Transactional
	public String saveSale(Sale item){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		item.setGeohash(geohash.encode(item.getLongitude(), item.getLatitude()));
		try{
			pm.makePersistent(item);
			log.debug("saved " + item.getSaleId());
			return saveResultToJson(item);
		}catch(Exception e){
			log.error("Error", e);
			return errorResultToJson(e);
		}finally{
			pm.close();
		}
		
	}
	
	@Transactional
	public String saveNewSaleImage(Long itemId, String imageKey){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		ImageInfo ii = new ImageInfo();
		ii.setImageBlobKey(imageKey);
		ii.setSaleId(itemId);
		try{
			pm.makePersistent(ii);

			Gson gson = new Gson();
			RequestResult<ImageInfo> rr = new RequestResult<ImageInfo>();
			rr.setGood();
			rr.getData().add(ii);
			Type parameterizedType = new TypeToken<RequestResult<ImageInfo>>() {}.getType();
			return gson.toJson(rr, parameterizedType);
		}catch(Exception e){
			return errorResultToJson(e);
		}finally{
			pm.close();
		}
	}
	
	@Transactional
	public String deleteImageSoft(Long imageId){
		//set the status of the image-info to be invalid only
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			ImageInfo ii = pm.getObjectById(ImageInfo.class, imageId);
			ii.setStatus("invalid");
			pm.makePersistent(ii);
			return this.removeResultToJson();
		}catch(Exception e){
			return errorResultToJson(e);
		}finally{
			pm.close();
		}
	}
	
	@Transactional
	public String deleteImage(Long imageId){
		//delete image-info record, delete the image blob, delete the icon image blob
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			ImageInfo ii = pm.getObjectById(ImageInfo.class, imageId);
			String imageBlobKey = ii.getImageBlobKey();
			String iconImageBlobKey = ii.getImageIconBlobKey();
			BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
			if(imageBlobKey!=null){
				blobstoreService.delete(new BlobKey(imageBlobKey));
			}
			if(iconImageBlobKey!=null){
				blobstoreService.delete(new BlobKey(iconImageBlobKey));
			}
			pm.deletePersistent(ii);
			return removeResultToJson();
		}catch(Exception e){
			return errorResultToJson(e);
		}finally{
			pm.close();
		}
	}
	
	@Transactional
	public String saveSaleIconImage(Long imageId, String iconImageKey, Boolean isUsedAsSaleIcon){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			//get the image info, which is already in the data store.
			ImageInfo info = (ImageInfo) pm.getObjectById(ImageInfo.class, imageId);
			info.setImageIconBlobKey(iconImageKey);
			info.setIsUsedAsListIcon(isUsedAsSaleIcon);
			pm.makePersistent(info);
			//if this image is used as sale icon, need to update the Sale too
			if(isUsedAsSaleIcon){
				Sale s = pm.getObjectById(Sale.class, info.getSaleId());
				s.setIconImageBlobKey(iconImageKey);
				pm.makePersistent(s);
			}
			Gson gson = new Gson();
			RequestResult<ImageInfo> rr = new RequestResult<ImageInfo>();
			rr.setGood();
			rr.getData().add(info);
			Type parameterizedType = new TypeToken<RequestResult<ImageInfo>>() {}.getType();
			return gson.toJson(rr, parameterizedType);
		}catch(Exception e){
			return errorResultToJson(e);
		}finally{
			pm.close();
		}
	}
	/*
	public String saveItem(String itemId, String userId, String address, String description, String latitude, String longitude, String startDateString, String endDateString) throws ParseException{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		SaleItem item = null;
		if(itemId==null || itemId.equalsIgnoreCase("")){
			item = new SaleItem();
		}else{
			item = pm.getObjectById(SaleItem.class, new Long(itemId));
		}
		item.setUserUniqueId(userId);
		item.setAddress(address);
		item.setDescription(description);
		item.setStartDate(sdf.parse(startDateString));
		item.setEndDate(sdf.parse(endDateString));
		item.setLatitude(Double.parseDouble(latitude));
		item.setLongitude(Double.parseDouble(longitude));
		item.setGeohash(geohash.encode(item.getLongitude(), item.getLatitude()));
		
		try{
			pm.makePersistent(item);
		}finally{
			pm.close();
		}
		return item.getSaleId().toString();
	}
	*/
	public String getSale(Long itemId){
		Sale item = null;
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			item = pm.getObjectById(Sale.class, itemId);
			return getResultToJson(item);
		}catch(Exception e){
			return errorResultToJson(e);
		}finally{
			pm.close();
		}

	}
	
	/**
	 * return the Json request-result of ImageInfo associated with the given item id
	 * @param itemId
	 * @return
	 */
	public String getJsonOfSaleImages(Long itemId){
		try{
			Collection<ImageInfo> results = (Collection<ImageInfo>) this.getSaleImages(itemId);
			RequestResult<ImageInfo> rr = new RequestResult<ImageInfo>();
			rr.setGood();
			rr.getData().addAll(results);
			Type parameterizedType = new TypeToken<RequestResult<ImageInfo>>() {}.getType();
			Gson gson = new Gson();
			return gson.toJson(rr, parameterizedType);
		}catch(Exception e){
			return errorResultToJson(e);
		}finally{
		}
	}
	
	
	public List<ImageInfo> getSaleImages(Long saleId) throws Exception{
		List<ImageInfo> images = new ArrayList<ImageInfo>();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = null;
		try{
			Extent<ImageInfo> extent = pm.getExtent(ImageInfo.class, true);
			String filter = "saleId==inputSaleId";
			query = pm.newQuery(extent, filter);
			query.declareParameters("Long inputSaleId");
			log.debug(query.toString());
			@SuppressWarnings("unchecked")
			Collection<ImageInfo> results = (Collection<ImageInfo>) query.execute(saleId);
			log.debug("found " + results);
			images.addAll(results);
			return images;
		}catch(Exception e){
			log.error("error", e);
			throw e;
		}finally{
			query.closeAll();
			pm.close();
		}
		
	}
	@SuppressWarnings("unchecked")
	@Transactional
	public String deleteOldRecordsBeforeDate(Date date){
		//FIXME need to reconsider
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = null;
		try{
			Extent<Sale> extent = pm.getExtent(Sale.class, true);
			String filter = "endDate<inputEndDate";
			query = pm.newQuery(extent, filter);
			query.declareImports("import java.util.Date");
			query.declareParameters("Date inputEndDate");
			Collection<Sale> results = (Collection<Sale>) query.execute(date);
			pm.deletePersistentAll(results);
			return removeResultToJson();
		}catch(Exception e){
			return errorResultToJson(e);
		}finally{
			query.closeAll();
			pm.close();
		}
	}
	@SuppressWarnings("unchecked")
	public String browseWithPage(int start, int end){
		List<Sale> results;
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = null;
		Extent<Sale> extent = pm.getExtent(Sale.class, true);
		try{
			query = pm.newQuery(extent);
			query.setRange(start, end);
			
			results = (List<Sale>) query.execute();//
			//now find the all the image-infos of the sales
//			ListIterator<Sale> ite = results.listIterator();
//			while(ite.hasNext()){
//				Sale si = (Sale)ite.next();
//				List<ImageInfo> images = this.getSaleImages(si.getSaleId());
//				Gson gson = new Gson();
//				for(ImageInfo image:images){
//					si.getImageJsons().add(gson.toJson(image, ImageInfo.class));
//				}
//			}
			return browseResultsToJson(results);
		}catch(Exception e){
			return errorResultToJson(e);
		}finally{
			query.closeAll();
			pm.close();
		}
		
	}
	@SuppressWarnings("unchecked")
	@Transactional
	public String searchSaleByLocationAndDateRange(int start, int end, Double latitude, Double longitude){
		List<Sale> results;
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = null;
		//time begin with today and end by 7 days later
		//latitude is [latitude-10, latitude+10]
		//longitude is [longitude-10, longtitude+10];
		double minLatitude = latitude-this.latitude_sope/2;
		double maxlatitude = latitude+this.latitude_sope/2;
		double minlongitude = longitude-this.longitude_scope/2;
		double maxlongitude = longitude+this.longitude_scope/2;
		System.out.println(minlongitude + " " + minLatitude + " " + maxlongitude + " " + maxlatitude);
		String southwest = this.geohash.encode(minlongitude,minLatitude);
		String northeast = this.geohash.encode(maxlongitude, maxlatitude);
		System.out.println("southWest:" + southwest + " northEast:" + northeast);
		Calendar tomorrowMorning = Calendar.getInstance();
		tomorrowMorning.set(Calendar.HOUR_OF_DAY, 0);
		tomorrowMorning.add(Calendar.DAY_OF_YEAR,1);
//		Calendar sevenDaysLater = Calendar.getInstance();
//		sevenDaysLater.add(Calendar.DAY_OF_YEAR, this.date_scope);
		Extent extent = pm.getExtent(Sale.class, true);
		try{
			String filter = "geohash>=southWest && geohash<=northEast";
			query = pm.newQuery(extent, filter);
			query.setRange(start, end);

			query.declareImports("import java.lang.String");
			query.declareParameters("String southWest ,String northEast");
			results = (List<Sale>) query.execute(southwest, northeast);
			
		
			//manually filter by start date, end date, latitude and longtitude
			System.out.println("find " + results.size() + " results");
			ListIterator ite = results.listIterator();
			while(ite.hasNext()){
				Sale si = (Sale)ite.next();
				System.out.println(si.getSaleId() + " " + si.getStartDate() + " " + si.getEndDate());
				if(si.getEndDateDate().before(tomorrowMorning.getTime())){
					System.out.println("removing " + si.getSaleId());
					ite.remove();
				}else if(si.getLatitude()>maxlatitude || si.getLatitude()<minLatitude || si.getLongitude()>maxlongitude || si.getLongitude()<minlongitude){
					System.out.println("remove again " + si.getSaleId());
					ite.remove();
					
				}
			}
			//now find the all the image-infos of the sales
//			ite = results.listIterator();
//			while(ite.hasNext()){
//				Sale si = (Sale)ite.next();
//				List<ImageInfo> images = this.getSaleImages(si.getSaleId());
//				Gson gson = new Gson();
//				for(ImageInfo image:images){
//					si.getImageJsons().add(gson.toJson(image, ImageInfo.class));
//				}
//			}
			return browseResultsToJson(results);
		}catch(Exception e){
			return errorResultToJson(e);
		}finally{
			query.closeAll();
			pm.close();
		}
	}
	
	@Transactional
	public String cleanUpData(){
		//FIXME not implemented yet!
		//find all the invalid and expired sales, and hard-delete them
		//update all sale whose date fall out the next 2 weeks to be "expired"
		//can we issue "delete from sale where status being "invalid", "expired"?
		//
		return this.removeResultToJson();
	}
}

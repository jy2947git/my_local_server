package com.focaplo.mylocal;


import java.lang.reflect.Type;
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
	public String deleteItem(int itemId){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			pm.deletePersistent(pm.getObjectById(Sale.class, itemId));
			return removeResultToJson();
		}catch(Exception e){
			return errorResultToJson(e);
		}finally{
			pm.close();
		}
	}
	@Transactional
	public String saveItem(Sale item){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		item.setGeohash(geohash.encode(item.getLongitude(), item.getLatitude()));
		try{
			pm.makePersistent(item);
			log.debug("saved " + item.getItemId());
			return saveResultToJson(item);
		}catch(Exception e){
			log.error("Error", e);
			return errorResultToJson(e);
		}finally{
			pm.close();
		}
		
	}
	
	@Transactional
	public String saveItemImage(Long itemId, String imageKey){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		ImageInfo ii = new ImageInfo();
		ii.setImageBlobKey(imageKey);
		ii.setItemId(itemId);
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
	public String saveItemIconImage(Long imageId, String iconKey){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			ImageInfo info = (ImageInfo) pm.getObjectById(ImageInfo.class, imageId);
			info.setImageIconBlobKey(iconKey);
			pm.makePersistent(info);
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
		return item.getItemId().toString();
	}
	*/
	public String getItem(int itemId){
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
	public String getItemIconImages(Long itemId){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = null;
		try{
			Extent<ImageInfo> extent = pm.getExtent(ImageInfo.class, true);
			String filter = "itemId==inputItemId";
			query = pm.newQuery(extent, filter);
			query.declareParameters("Long inputItemId");
			Collection<ImageInfo> results = (Collection<ImageInfo>) query.execute(itemId);
			RequestResult<ImageInfo> rr = new RequestResult<ImageInfo>();
			rr.setGood();
			rr.getData().addAll(results);
			Type parameterizedType = new TypeToken<RequestResult<ImageInfo>>() {}.getType();
			Gson gson = new Gson();
			return gson.toJson(rr, parameterizedType);
		}catch(Exception e){
			return errorResultToJson(e);
		}finally{
			query.closeAll();
			pm.close();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@Transactional
	public String deleteOldRecordsBeforeDate(Date date){
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
	public String searchItemByLocationAndDateWithPage(int start, int end, Double latitude, Double longitude){
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
				System.out.println(si.getItemId() + " " + si.getStartDate() + " " + si.getEndDate());
				if(si.getEndDateDate().before(tomorrowMorning.getTime())){
					System.out.println("removing " + si.getItemId());
					ite.remove();
				}else if(si.getLatitude()>maxlatitude || si.getLatitude()<minLatitude || si.getLongitude()>maxlongitude || si.getLongitude()<minlongitude){
					System.out.println("remove again " + si.getItemId());
					ite.remove();
					
				}
			}
			return browseResultsToJson(results);
		}catch(Exception e){
			return errorResultToJson(e);
		}finally{
			query.closeAll();
			pm.close();
		}
	}
}

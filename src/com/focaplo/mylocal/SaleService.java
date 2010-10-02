package com.focaplo.mylocal;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.Transactional;

import com.focaplo.common.Geohash;
import com.google.gson.Gson;


public class SaleService {
	double latitude_sope=0.3; // 30 KM square
	double longitude_scope=3;
//	int date_scope=7;

	Geohash geohash = new Geohash();
	
	public SaleItem fromJson(String jsonString){
		Gson gson = new Gson();
		return gson.fromJson(jsonString, SaleItem.class);
	}
	
	public String toJson(SaleItem saleItem){
		Gson gson = new Gson();
		return gson.toJson(saleItem, SaleItem.class);
	}
	
	public String getResultToJson(SaleItem item){
		Gson gson = new Gson();
		RequestResult rr = new RequestResult();
		rr.setGood();
		rr.getData().add(item);
		return gson.toJson(rr, RequestResult.class);
	}
	
	public String browseResultsToJson(List<SaleItem> items){
		Gson gson = new Gson();
		RequestResult rr = new RequestResult();
		rr.setGood();
		rr.getData().addAll(items);
		return gson.toJson(rr, RequestResult.class);
	}
	
	public String saveResultToJson(SaleItem item){
		Gson gson = new Gson();
		RequestResult rr = new RequestResult();
		rr.setGood();
		rr.getData().add(item);
		return gson.toJson(rr, RequestResult.class);
	}
	
	public String removeResultToJson(){
		Gson gson = new Gson();
		RequestResult rr = new RequestResult();
		rr.setGood();
		return gson.toJson(rr, RequestResult.class);
	}
	
	public String errorResultToJson(Exception exception){
		Gson gson = new Gson();
		RequestResult rr = new RequestResult();
		rr.setError(exception);
		
		return gson.toJson(rr, RequestResult.class);
	}
	@Transactional
	public String deleteItem(int itemId){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			pm.deletePersistent(pm.getObjectById(SaleItem.class, itemId));
			return removeResultToJson();
		}catch(Exception e){
			return errorResultToJson(e);
		}finally{
			pm.close();
		}
	}
	@Transactional
	public String saveItem(SaleItem item){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		item.setGeohash(geohash.encode(item.getLongitude(), item.getLatitude()));
		try{
			pm.makePersistent(item);
			return saveResultToJson(item);
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
		SaleItem item = null;
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			item = pm.getObjectById(SaleItem.class, itemId);
			return getResultToJson(item);
		}catch(Exception e){
			return errorResultToJson(e);
		}finally{
			pm.close();
		}

	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public String deleteOldRecordsBeforeDate(Date date){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = null;
		try{
			Extent<SaleItem> extent = pm.getExtent(SaleItem.class, true);
			String filter = "endDate<inputEndDate";
			query = pm.newQuery(extent, filter);
			query.declareImports("import java.util.Date");
			query.declareParameters("Date inputEndDate");
			Collection<SaleItem> results = (Collection<SaleItem>) query.execute(date);
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
		List<SaleItem> results;
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = null;
		Extent<SaleItem> extent = pm.getExtent(SaleItem.class, true);
		try{
			query = pm.newQuery(extent);
			query.setRange(start, end);
			
			results = (List<SaleItem>) query.execute();//
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
		List<SaleItem> results;
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
		Extent extent = pm.getExtent(SaleItem.class, true);
		try{
			String filter = "geohash>=southWest && geohash<=northEast";
			query = pm.newQuery(extent, filter);
			query.setRange(start, end);

			query.declareImports("import java.lang.String");
			query.declareParameters("String southWest ,String northEast");
			results = (List<SaleItem>) query.execute(southwest, northeast);
			
		
			//manually filter by start date, end date, latitude and longtitude
			System.out.println("find " + results.size() + " results");
			ListIterator ite = results.listIterator();
			while(ite.hasNext()){
				SaleItem si = (SaleItem)ite.next();
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

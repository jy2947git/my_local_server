package com.focaplo.mylocal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.focaplo.common.Geohash;
import com.focaplo.common.PersistenceManagerSingeton;


public class SaleService {
	double latitude_sope=0.3; // 30 KM square
	double longitude_scope=3;
//	int date_scope=7;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	Geohash geohash = new Geohash();
	
	public void deleteItem(int itemId){
		PersistenceManager pm = PersistenceManagerSingeton.instance().getPersistenceManager();
		try{
			pm.deletePersistent(pm.getObjectById(SaleItem.class, itemId));
		}finally{
			pm.close();
		}
	}
	public String saveItem(String itemId, String userId, String address, String description, String latitude, String longitude, String startDateString, String endDateString) throws ParseException{
		PersistenceManager pm = PersistenceManagerSingeton.instance().getPersistenceManager();
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
	
	public SaleItem getItem(int itemId){
		SaleItem item = null;
		PersistenceManager pm = PersistenceManagerSingeton.instance().getPersistenceManager();
		try{
			item = pm.getObjectById(SaleItem.class, itemId);
		}finally{
			pm.close();
		}
		return item;
	}
	
	@SuppressWarnings("unchecked")
	public void deleteOldRecordsBeforeDate(Date date){
		PersistenceManager pm = PersistenceManagerSingeton.instance().getPersistenceManager();
		Query query = null;
		try{
			Extent<SaleItem> extent = pm.getExtent(SaleItem.class, true);
			String filter = "endDate<inputEndDate";
			query = pm.newQuery(extent, filter);
			query.declareImports("import java.util.Date");
			query.declareParameters("Date inputEndDate");
			Collection<SaleItem> results = (Collection<SaleItem>) query.execute(date);
			pm.deletePersistentAll(results);
		}finally{
			query.closeAll();
			pm.close();
		}
	}
	@SuppressWarnings("unchecked")
	public List<SaleItem> browseWithPage(int start, int end){
		List<SaleItem> results;
		PersistenceManager pm = PersistenceManagerSingeton.instance().getPersistenceManager();
		Query query = null;
		Extent<SaleItem> extent = pm.getExtent(SaleItem.class, true);
		try{
			query = pm.newQuery(extent);
			query.setRange(start, end);
			
			results = (List<SaleItem>) query.execute();//
		}finally{
			query.closeAll();
			pm.close();
		}
		return results;
	}
	@SuppressWarnings("unchecked")
	public List<SaleItem> searchItemByLocationAndDateWithPage(int start, int end, Double latitude, Double longitude){
		List<SaleItem> results;
		PersistenceManager pm = PersistenceManagerSingeton.instance().getPersistenceManager();
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

		}finally{
			query.closeAll();
			pm.close();
		}
		//manually filter by start date, end date, latitude and longtitude
		System.out.println("find " + results.size() + " results");
		ListIterator ite = results.listIterator();
		while(ite.hasNext()){
			SaleItem si = (SaleItem)ite.next();
			System.out.println(si.getItemId() + " " + si.getStartDate() + " " + si.getEndDate());
			if(si.getEndDate().before(tomorrowMorning.getTime())){
				System.out.println("removing " + si.getItemId());
				ite.remove();
			}else if(si.getLatitude()>maxlatitude || si.getLatitude()<minLatitude || si.getLongitude()>maxlongitude || si.getLongitude()<minlongitude){
				System.out.println("remove again " + si.getItemId());
				ite.remove();
				
			}
		}
		return results;
	}
}

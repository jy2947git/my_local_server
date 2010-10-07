package com.focaplo.mylocal.sale.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.Entity;
@PersistenceCapable(identityType = IdentityType.APPLICATION)

public class Sale{
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long saleId;
	@Persistent
	private Double latitude;
	@Persistent
	private Double longitude;
//	@Persistent
//	private String address;
	@Persistent
	private String description;
	@Persistent
	private String startDate;
	@Persistent
	private String endDate;
	@Persistent
	private String geohash;
	@Persistent
	private String userUniqueId;
	@Persistent
	private String address1;
	@Persistent
	private String address2;
	@Persistent
	private String city;
	@Persistent
	private String state;
	@Persistent
	private String countryCode;
	@Persistent
	private String zipcode;
	@Persistent
	private String phone;
	@Persistent
	private String email;
	//ids of ImageInfo, purposely keep them un-related to avoid problems of one-to-many implementations on GAE
	@Persistent
	private List<Long> images = new ArrayList<Long>();
	
	@Persistent
	private String status = "pending"; //pending, valid, expired, invalid
	
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUserUniqueId() {
		return userUniqueId;
	}
	public void setUserUniqueId(String userUniqueId) {
		this.userUniqueId = userUniqueId;
	}
	public String getGeohash() {
		return geohash;
	}
	public void setGeohash(String geohash) {
		this.geohash = geohash;
	}
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("id=" + this.saleId + " deviceId=" + this.userUniqueId + " address1=" + this.getAddress1() + " description=" + this.getDescription() + " startFrom=" + this.startDate + " end=" + this.endDate + " latitude=" + this.getLatitude() + " longitude=" + this.getLongitude() + " geohash=" + this.geohash);
		return buf.toString();
	}

	public Long getSaleId() {
		return saleId;
	}
	public void setSaleId(Long saleId) {
		this.saleId = saleId;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
//	public String getAddress() {
//		return address;
//	}
//	public void setAddress(String address) {
//		this.address = address;
//	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getStartDateDate() {
		try {
			return sdf.parse(startDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public void setStartDateDate(Date startDate) {
		this.startDate = sdf.format(startDate);
	}
	public Date getEndDateDate() {
		try {
			return sdf.parse(endDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public void setEndDateDate(Date endDate) {
		this.endDate = sdf.format(endDate);
		System.out.println(this.endDate);
	}
	
	
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public List<Long> getImages() {
		return images;
	}
	public void setImages(List<Long> images) {
		this.images = images;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}



	
}

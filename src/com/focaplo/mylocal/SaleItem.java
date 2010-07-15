package com.focaplo.mylocal;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class SaleItem {
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long itemId;
	@Persistent
	private Double latitude;
	@Persistent
	private Double longitude;
	@Persistent
	private String address;
	@Persistent
	private String description;
	@Persistent
	private Date startDate;
	@Persistent
	private Date endDate;
	@Persistent
	private String geohash;
	@Persistent
	private String userUniqueId;
	
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
		buf.append("id=" + this.itemId + " deviceId=" + this.userUniqueId + " address=" + this.getAddress() + " description=" + this.getDescription() + " startFrom=" + this.getStartDate() + " end=" + this.getEndDate() + " latitude=" + this.getLatitude() + " longitude=" + this.getLongitude() + " geohash=" + this.geohash);
		return buf.toString();
	}
	public Long getItemId() {
		return itemId;
	}
	public void setItemId(Long itemId) {
		this.itemId = itemId;
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}

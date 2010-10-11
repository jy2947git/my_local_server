package com.focaplo.mylocal.sale.model;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class ImageInfo {

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long imageId;
	
	@Persistent
	private Long saleId;
	
	@Persistent
	private String imageBlobKey; //big
	@Persistent
	private String imageIconBlobKey; //small
	
	@Persistent
	private String status = "valid" ; //valid, invalid
	
	@Persistent
	private Boolean isUsedAsListIcon;
	
	public ImageInfo() {
		super();
	}
	public ImageInfo(String imageBlobKey, String imageIconBlobKey) {
		super();
		this.imageBlobKey = imageBlobKey;
		this.imageIconBlobKey = imageIconBlobKey;
	}

	@Override
	public String toString() {
		return this.imageId + " " + this.getStatus() + " " + this.saleId + " " + this.imageBlobKey + " " + this.imageIconBlobKey;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getImageBlobKey() {
		return imageBlobKey;
	}
	public void setImageBlobKey(String imageBlobKey) {
		this.imageBlobKey = imageBlobKey;
	}
	public String getImageIconBlobKey() {
		return imageIconBlobKey;
	}
	public void setImageIconBlobKey(String imageIconBlobKey) {
		this.imageIconBlobKey = imageIconBlobKey;
	}

	public Long getImageId() {
		return imageId;
	}
	public void setImageId(Long imageId) {
		this.imageId = imageId;
	}
	public Long getSaleId() {
		return saleId;
	}
	public void setSaleId(Long saleId) {
		this.saleId = saleId;
	}
	public Boolean getIsUsedAsListIcon() {
		return isUsedAsListIcon;
	}
	public void setIsUsedAsListIcon(Boolean isUsedAsListIcon) {
		this.isUsedAsListIcon = isUsedAsListIcon;
	}


	
}

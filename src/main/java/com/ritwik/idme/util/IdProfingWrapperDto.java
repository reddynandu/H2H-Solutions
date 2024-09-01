package com.ritwik.idme.util;

import java.util.Date;


public class IdProfingWrapperDto {
	
	private String id; 

	private String fName;

	private String lName;

	private String personId;

	private String email;

	private boolean LOA3Verified;

	private String npi;

	private String uuid;

	private Date createdDate;

	private String deaNumber;

	private Date deaExpDate;

	private int deaLevel;

	public String getDeaNumber() {
		return deaNumber;
	}

	public void setDeaNumber(String deaNumber) {
		this.deaNumber = deaNumber;
	}

	public Date getDeaExpDate() {
		return deaExpDate;
	}

	public void setDeaExpDate(Date deaExpDate) {
		this.deaExpDate = deaExpDate;
	}

	public int getDeaLevel() {
		return deaLevel;
	}

	public void setDeaLevel(int deaLevel) {
		this.deaLevel = deaLevel;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public String getlName() {
		return lName;
	}

	public void setlName(String lName) {
		this.lName = lName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getpersonId() {
		return personId;
	}

	public void setpersonId(String personId) {
		this.personId = personId;
	}

	public boolean isLOA3Verified() {
		return LOA3Verified;
	}

	public void setLOA3Verified(boolean lOA3Verified) {
		LOA3Verified = lOA3Verified;
	}

	public String getNpi() {
		return npi;
	}

	public void setNpi(String npi) {
		this.npi = npi;
	}

}

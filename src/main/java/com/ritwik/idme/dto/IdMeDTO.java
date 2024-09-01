/*
 * DigitalRx : com.ritwik.idme.dto.IdMeDTO.java
 * Release : @SVN_RELEASE_NUMBER@
 * Copyright (c) 2022 H2HSolutions Inc, All Rights Reserved.
 * Redistribution and use in source and binary forms, with or without modification,
 * are not permitted without specific prior written permission.
 */
package com.ritwik.idme.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "idme_t")
public class IdMeDTO {

	@Id
	@Column(name = "idme_p")
	private String id;

	@Column(name = "fname")
	private String fName;

	@Column(name = "lname")
	private String lName;

	@Column(name = "email")
	private String email;

	@Column(name = "LOA3verified")
	private boolean LOA3Verified;

	@Column(name = "npi")
	private String npi;

	@Column(name = "uuid")
	private String uuid;

	@Column(name = "created_date")
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

	public static IdMeDTO getInstance() {
		return new IdMeDTO();
	}

}

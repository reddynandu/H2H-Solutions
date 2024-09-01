/*
 * DigitalRx : com.ritwik.idme.dto.IdentificationDTO.java
 * Release : @SVN_RELEASE_NUMBER@
 * Copyright (c) 2022 H2HSolutions Inc, All Rights Reserved.
 * Redistribution and use in source and binary forms, with or without modification,
 * are not permitted without specific prior written permission.
 */
package com.ritwik.idme.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "identification_t")
public class IdentificationDTO implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -3335684120436823050L;

	@Id
	@Column(name = "identification_p")
	private String id;

	@Column(name = "type_c")
	private String type;

	@Column(name = "identification_x")
	private String identification;

	@Column(name = "state_c")
	private String state;

	@Column(name = "description_x")
	private String description;

	@Column(name = "person_p")
	private String personId = null;

	@Column(name = "person_address_p")
	public String personAddressId = null;

	@Column(name = "delete_f")
	private boolean deleted;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	public boolean getDeleted() {
		return this.deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	/**
	 * Constructs a <code>IdentificationDTO</code>
	 */
	public IdentificationDTO() {
	}

	/**
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return
	 */
	public String getIdentification() {
		return identification;
	}

	/**
	 * @param identification
	 */
	public void setIdentification(String identification) {
		this.identification = identification;
	}

	/**
	 * @return
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return
	 */
	public String getPersonId() {
		return this.personId;
	}

	/**
	 * @param personId
	 */
	public void setPersonId(String personId) {
		this.personId = personId;
	}

	/**
	 * @return
	 */
	public String getPersonAddressId() {
		return personAddressId;
	}

	/**
	 * @param personAddressId
	 */
	public void setPersonAddressId(String personAddressId) {
		this.personAddressId = personAddressId;
	}

	public static IdentificationDTO getInstance() {
		return new IdentificationDTO();
	}
}
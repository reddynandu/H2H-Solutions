/*
 * DigitalRx : com.ritwik.idme.service.IdMeRepository.java
 * Release : @SVN_RELEASE_NUMBER@
 * Copyright (c) 2022 H2HSolutions Inc, All Rights Reserved.
 * Redistribution and use in source and binary forms, with or without modification,
 * are not permitted without specific prior written permission.
 */

package com.ritwik.idme.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ritwik.idme.dto.IdMeDTO;

public interface IdMeRepository extends JpaRepository<IdMeDTO, String> {
	
	public IdMeDTO findByEmail(String email);

}

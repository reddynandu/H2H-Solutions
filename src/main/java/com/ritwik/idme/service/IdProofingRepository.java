/*
 * DigitalRx : com.ritwik.idme.service.IdProofingRepository.java
 * Release : @SVN_RELEASE_NUMBER@
 * Copyright (c) 2022 H2HSolutions Inc, All Rights Reserved.
 * Redistribution and use in source and binary forms, with or without modification,
 * are not permitted without specific prior written permission.
 */

package com.ritwik.idme.service;

import org.springframework.data.jpa.repository.JpaRepository;


import com.ritwik.idme.dto.IdProofingDTO;

public interface IdProofingRepository extends JpaRepository<IdProofingDTO, String> {

	public IdProofingDTO findByEmail(String email);
}

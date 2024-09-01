/*
 * DigitalRx : com.ritwik.idme.service.IdentificationRepository.java
 * Release : @SVN_RELEASE_NUMBER@
 * Copyright (c) 2022 H2HSolutions Inc, All Rights Reserved.
 * Redistribution and use in source and binary forms, with or without modification,
 * are not permitted without specific prior written permission.
 */

package com.ritwik.idme.service;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ritwik.idme.dto.IdentificationDTO;

public interface IdentificationRepository extends JpaRepository<IdentificationDTO, String> {

	public List<IdentificationDTO> findByIdentificationAndType(String identification, String type);

	public List<IdentificationDTO> findByTypeAndPersonId(String type, String personId);

}

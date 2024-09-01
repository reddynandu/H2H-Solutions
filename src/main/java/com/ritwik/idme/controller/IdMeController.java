/*
 * DigitalRx : com.ritwik.idme.controller.IdMeController.java
 * Release : @SVN_RELEASE_NUMBER@
 * Copyright (c) 2022 H2HSolutions Inc, All Rights Reserved.
 * Redistribution and use in source and binary forms, with or without modification,
 * are not permitted without specific prior written permission.
 */

package com.ritwik.idme.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.ritwik.idme.service.IdMeService;
import com.ritwik.idme.util.IdProfingWrapperDto;
import com.ritwik.idme.util.Status;
import com.ritwik.idme.util.Tokens;
import com.ritwik.idme.util.User;
import com.ritwik.idme.util.UserDetails;


@RestController
public class IdMeController
{ 
	private static final Logger logger;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private IdMeService idMeService;
	@Value("${idme.client.id}")
	private String client_id;
	@Value("${idme.client.secret}")
	private String client_secret;
	@Value("${idme.cfg.domain}")
	private String domain;
	@Value("${idme.cfg.redirect-u-r-l.IdProofing}")
	private String redirectURLForIdProofing;
	@Value("${idme.cfg.redirect-u-r-l.IdProofing.WebService}")
	private String redirectURLForIdProofingWebService;
	@Value("${idme.cfg.redirect-u-r-l}")
	private String redirectURL;
	@Value("${idme.cfg.redirect-u-r-l-forSixDot}")
	private String redirectURLForSixDot;
	@Value("${drx.application.redirect.url}")
	private String drxRedirectionURL;
	@Value("${drx.application.redirect.url.for.IdProofing}")
	private String drxIdProofingRedirectionURL;
	@Value("${h2h.static.idproofing.success.url}")
	private String h2hIdProofingSuccessUrl;
	@Value("${h2h.static.idproofing.denied.url}")
	private String h2hIdProofingDeniedUrl;
	private String domainFrom;
	
	@Value("${wrapper.idprofing.redirect}")
	private String redirect;
	
	@Value("${idme.cfg.idme.wrapper.redirect-u-r-l.IdProofing.WebService}")
	private String wrapperRedirectURLForIdProofingWebService;
	

	@GetMapping({ "sendToIdMe", "redirectToIdMe", "registerAtIdMe", "idmeLogin" })
	public String redirectToIdMe(final HttpServletRequest request, final HttpServletResponse response, @RequestParam(value = "domain", required = false) final String domainFrom) throws Exception {
		try {
			this.domainFrom = domainFrom;
			final String url = "https://" + this.domain + "/oauth/authorize?client_id=" + this.client_id + "&redirect_uri=" + this.redirectURL + "&response_type=code&scope=http://idmanagement.gov/ns/assurance/ial/2/aal/2/epcs";
			response.sendRedirect(url);
		}
		catch (Exception e) {
			IdMeController.logger.error(e.getMessage(), (Throwable)e);
		}
		return null;
	}


	@GetMapping({ "sendToIdMeFromSixDot"})
	public String redirectToIdMeFromSixDot(final HttpServletRequest request, final HttpServletResponse response, @RequestParam(value = "domain", required = false) final String domainFrom) throws Exception {
		try {
			this.domainFrom = domainFrom;
			final String url = "https://" + this.domain + "/oauth/authorize?client_id=" + this.client_id + "&redirect_uri=" + this.redirectURLForSixDot + "&response_type=code&scope=medical";
			response.sendRedirect(url);
		}
		catch (Exception e) {
			IdMeController.logger.error(e.getMessage(), (Throwable)e);
		}
		return null;
	}


	@GetMapping({ "/receiveAuthCodeForSixDot" })
	public String receiveAuthCodeForSixDot(final HttpServletRequest request, final HttpServletResponse response, @RequestParam(value = "code", required = false) final String code, @RequestParam(value = "error", required = false) final String error) throws Exception {
		if (code == null && error != null && !error.equals("")) {
			response.sendRedirect("https://h2hdigitalrx.com/denied.html");
			return error + " ==> " + request.getParameter("error_description");
		}
		boolean verified = false;
		boolean idFlag = false;
		boolean medFlag = false;
		boolean storeDetails = false;
		try {
			final String url = "https://" + this.domain + "/oauth/token?code=" + code + "&client_id=" + this.client_id + "&client_secret=" + this.client_secret + "&grant_type=authorization_code&redirect_uri=" + this.redirectURLForSixDot;
			final ResponseEntity<Tokens> entity = (ResponseEntity<Tokens>)this.restTemplate.postForEntity(url, (Object)null, (Class)Tokens.class, (Map)new HashMap());
			final Tokens tokens = (Tokens)entity.getBody();
			final String url2 = "https://" + this.domain + "/api/public/v3/attributes.json?access_token=" + tokens.getAccess_token();
			IdMeController.logger.info("Token URL:" + url2);
			final ResponseEntity<User> responseEntity = (ResponseEntity<User>)this.restTemplate.getForEntity(url2, (Class)User.class, new Object[0]);
			final User user = (User)responseEntity.getBody();
			IdMeController.logger.info("userResponse:" + user.toString());
			storeDetails = this.idMeService.storeDetails(user);
			IdMeController.logger.info("storeDetails:" + storeDetails);
			for (final UserDetails str : user.getAttributes()) {
				IdMeController.logger.info("UserDetailsstr:" + str);
				System.out.println(str);
			}
			for (final Status status : user.getStatus()) {
				if ("identity".equals(status.getGroup())) {
					for (final String str2 : status.getSubgroups()) {
						IdMeController.logger.info("statusCode:" + status);
						if ("LOA3".equals(str2) && Boolean.parseBoolean(status.getVerified())) {
							idFlag = true;
						}
						else if ("IAL2".equals(str2) && Boolean.parseBoolean(status.getVerified())) {
							idFlag = true;
						}
					}
				}
				else if ("medical".equals(status.getGroup()) && Boolean.parseBoolean(status.getVerified())) {
					medFlag = true;
				}
				if (idFlag && medFlag) {
					verified = true;
					IdMeController.logger.info("verifiedCode:" + verified);
					break;
				}
			}
			if (verified) {
				// response.sendRedirect("https://www.h2hdigitalrx.com/drx/rxpad/ReceiveepcsAuth_IdMe.do?function=receivesAuthCodeFromIDme");
				response.sendRedirect(this.drxRedirectionURL);
				return null;
			}
			return "Please complete your ID proofing, By Sign in again to id.me";
		}
		catch (Exception e) {
			IdMeController.logger.warn(e.getMessage(), (Throwable)e);
		}
		finally {
			this.domainFrom = null;
		}
		if (storeDetails && verified) {
			response.sendRedirect("https://h2hdigitalrx.com/success.html");
			return "ID Proofing Completed Successfully";
		}
		// response.sendRedirect("https://www.h2hdigitalrx.com/drx/rxpad/ReceiveepcsAuth_IdMe.do?function=receivesAuthCodeFromIDme");
		response.sendRedirect(this.drxRedirectionURL);
		return null;
		//return "Already ID Proofing completed";
	}


	@GetMapping({ "/receiveAuthCode" })
	public String receiveAuthCode(final HttpServletRequest request, final HttpServletResponse response, @RequestParam(value = "code", required = false) final String code, @RequestParam(value = "error", required = false) final String error) throws Exception {
		if (code == null && error != null && !error.equals("")) {
			response.sendRedirect("https://h2hdigitalrx.com/denied.html");
			return error + " ==> " + request.getParameter("error_description");
		}
		boolean verified = false;
		boolean idFlag = false;
		boolean medFlag = false;
		boolean storeDetails = false;
		try {
			final String url = "https://" + this.domain + "/oauth/token?code=" + code + "&client_id=" + this.client_id + "&client_secret=" + this.client_secret + "&grant_type=authorization_code&redirect_uri=" + this.redirectURL;
			final ResponseEntity<Tokens> entity = (ResponseEntity<Tokens>)this.restTemplate.postForEntity(url, (Object)null, (Class)Tokens.class, (Map)new HashMap());
			final Tokens tokens = (Tokens)entity.getBody();
			final String url2 = "https://" + this.domain + "/api/public/v3/attributes.json?access_token=" + tokens.getAccess_token();
			IdMeController.logger.info("Token URL 5.2:" + url2);
			final ResponseEntity<User> responseEntity = (ResponseEntity<User>)this.restTemplate.getForEntity(url2, (Class)User.class, new Object[0]);
			final User user = (User)responseEntity.getBody();
			IdMeController.logger.info("userResponse:" + user.toString());
			storeDetails = this.idMeService.storeDetails(user);
			IdMeController.logger.info("storeDetails:" + storeDetails);
			for (final UserDetails str : user.getAttributes()) {
				IdMeController.logger.info("UserDetailsstr:" + str);
				System.out.println(str);
			}
			for (final Status status : user.getStatus()) {
				if ("identity".equals(status.getGroup())) {
					for (final String str2 : status.getSubgroups()) {
						IdMeController.logger.info("statusCode:" + status);
						if ("LOA3".equals(str2) && Boolean.parseBoolean(status.getVerified())) {
							idFlag = true;
						}
						else if ("IAL2".equals(str2) && Boolean.parseBoolean(status.getVerified())) {
							idFlag = true;
						}
					}
				}
				else if ("medical".equals(status.getGroup()) && Boolean.parseBoolean(status.getVerified())) {
					medFlag = true;
				}
				if (idFlag && medFlag) {
					verified = true;
					IdMeController.logger.info("verifiedCode:" + verified);
					break;
				}
			}
			if (verified) {
				if (storeDetails) {
					response.sendRedirect("https://h2hdigitalrx.com/success.html");
					return "ID Proofing Completed Successfully";
				} else {
					//   response.sendRedirect("https://www.h2hdigitalrx.com/drx/rxpad/ReceiveepcsAuth_IdMe.do?function=receivesAuthCodeFromIDme");
					response.sendRedirect(this.drxRedirectionURL);
					return null;
				}
			}
			return "Please complete your ID proofing, By Sign in again to id.me";
		}
		catch (Exception e) {
			IdMeController.logger.warn(e.getMessage(), (Throwable)e);
			response.sendRedirect("https://h2hdigitalrx.com/denied.html");
			return null;
		}
		finally {
			this.domainFrom = null;
		}
	}

	static {
		logger = LoggerFactory.getLogger((Class)IdMeController.class);
	}


	@GetMapping("sendToIdMeForIdProofing")
	public String redirectToIdProofing(final HttpServletRequest request, final HttpServletResponse response, @RequestParam(value = "domain", required = false) final String domainFrom) 
			throws Exception {
		try {
			this.domainFrom = domainFrom;
			final String url = "https://" + this.domain + "/oauth/authorize?client_id=" + this.client_id + "&redirect_uri=" + this.redirectURLForIdProofing + "&response_type=code&scope=http://idmanagement.gov/ns/assurance/ial/2/aal/2/erx";
			response.sendRedirect(url);
		}
		catch (Exception e) {
			IdMeController.logger.error(e.getMessage(), (Throwable)e);
		}
		return null;
	}


	@GetMapping({ "/receiveIdProofingAuthCode" })
	public String receiveIdProofingAuthCode(final HttpServletRequest request, final HttpServletResponse response, @RequestParam(value = "code", required = false) final String code, @RequestParam(value = "error", required = false) final String error) throws Exception {
		if (code == null && error != null && !error.equals("")) {
			response.sendRedirect("https://h2hdigitalrx.com/denied.html");
			return error + " ==> " + request.getParameter("error_description");
		}
		boolean verified = false;
		boolean idFlag = false;
		boolean medFlag = false;
		boolean storeDetails = false;
		//String newcode = code;

		try {
			Tokens token = new Tokens();
			token.setAccess_token(code);
			final String url = "https://" + this.domain + "/oauth/token?code=" + code + "&client_id=" + this.client_id + "&client_secret=" + this.client_secret + "&grant_type=authorization_code&redirect_uri=" + this.redirectURLForIdProofing;
			IdMeController.logger.info("url:" + url);
			final ResponseEntity<Tokens> entity = (ResponseEntity<Tokens>)this.restTemplate.postForEntity(url, (Object)null, (Class)Tokens.class, (Map)new HashMap());
			IdMeController.logger.info("entity:" + entity);
			final Tokens tokens = (Tokens)entity.getBody();
			IdMeController.logger.info("tokens:" + tokens);
			final String url2 = "https://" + this.domain + "/api/public/v3/attributes.json?access_token=" + tokens.getAccess_token();
			IdMeController.logger.info("Token URL 5.2:" + url2);
			final ResponseEntity<User> responseEntity = (ResponseEntity<User>)this.restTemplate.getForEntity(url2, (Class)User.class, new Object[0]);
			final User user = (User)responseEntity.getBody();
			IdMeController.logger.info("userResponse:" + user.toString());
			storeDetails = this.idMeService.storeDetailsForIdProofing(user);
			IdMeController.logger.info("storeDetailsForIdProofing:" + storeDetails);
			for (final UserDetails str : user.getAttributes()) {
				IdMeController.logger.info("UserDetailsstr:" + str);
				System.out.println(str);
			}
			for (final Status status : user.getStatus()) {
				if ("identity".equals(status.getGroup())) {
					for (final String str2 : status.getSubgroups()) {
						IdMeController.logger.info("statusCode:" + status);
						if ("LOA3".equals(str2) && Boolean.parseBoolean(status.getVerified())) {
							idFlag = true;
						}else if ("IAL2".equals(str2) && Boolean.parseBoolean(status.getVerified())) {
							idFlag = true;
						}

					}
				}
				else if ("medical".equals(status.getGroup()) && Boolean.parseBoolean(status.getVerified())) {
					medFlag = true;
				}
				if (idFlag && medFlag) {
					verified = true;
					IdMeController.logger.info("verifiedCode:" + verified);
					break;
				}
			}
			if (verified) {
				if (storeDetails) {
					response.sendRedirect(this.drxIdProofingRedirectionURL);
//					response.sendRedirect("https://h2hdigitalrx.com/success.html");
					return "eRx ID Proofing Completed Successfully";
				} else {
					response.sendRedirect(this.drxIdProofingRedirectionURL);
					return null;
				}
			}
			return "Please complete your ID proofing, By Sign in again to id.me";
		}
		catch (Exception e) {
			IdMeController.logger.warn(e.getMessage(), (Throwable)e);
			response.sendRedirect("https://h2hdigitalrx.com/denied.html");
			return null;
		}
		finally {
			this.domainFrom = null;
		}
		//response.sendRedirect("https://www.h2hdigitalrx.com/drx/rxpad/ReceiveUserIdProofingAuth_IdMe.do?function=receivesUserIdProofingAuthCodeFromIDme");
		//response.sendRedirect("https://drxstage.h2hdigitalrx.com/drx/drxLogin.do?function=login#");
		//return "Already ID Proofing completed";
	}
	
	
	@GetMapping("/idProofingLogin")
	public String redirectToIdProofingWebService(final HttpServletRequest request, final HttpServletResponse response, @RequestParam(value = "domain", required = false) final String domainFrom) 
			throws Exception {
		try {
			this.domainFrom = domainFrom;
			final String url = "https://" + this.domain + "/oauth/authorize?client_id=" + this.client_id + "&redirect_uri=" + this.redirectURLForIdProofingWebService + "&response_type=code&scope=http://idmanagement.gov/ns/assurance/ial/2/aal/2/erx";
			response.sendRedirect(url);
		}
		catch (Exception e) {
			IdMeController.logger.error(e.getMessage(), (Throwable)e);
		}
		return null;
	}
	
	
	@GetMapping({ "/receiveIdProofingAuthCodeWebService" })
	public String receiveIdProofingAuthCodeWebService(final HttpServletRequest request, final HttpServletResponse response, @RequestParam(value = "code", required = false) final String code, @RequestParam(value = "error", required = false) final String error) throws Exception {
		if (code == null && error != null && !error.equals("")) {
			response.sendRedirect(this.h2hIdProofingDeniedUrl);
			return error + " ==> " + request.getParameter("error_description");
		}
		boolean verified = false;
		boolean idFlag = false;
		boolean medFlag = false;
		boolean storeDetails = false;
		//String newcode = code;

		try {
			Tokens token = new Tokens();
			token.setAccess_token(code);
			final String url = "https://" + this.domain + "/oauth/token?code=" + code + "&client_id=" + this.client_id + "&client_secret=" + this.client_secret + "&grant_type=authorization_code&redirect_uri=" + this.redirectURLForIdProofingWebService;
			IdMeController.logger.info("url:" + url);
			final ResponseEntity<Tokens> entity = (ResponseEntity<Tokens>)this.restTemplate.postForEntity(url, (Object)null, (Class)Tokens.class, (Map)new HashMap());
			IdMeController.logger.info("entity:" + entity);
			final Tokens tokens = (Tokens)entity.getBody();
			IdMeController.logger.info("tokens:" + tokens);
			final String url2 = "https://" + this.domain + "/api/public/v3/attributes.json?access_token=" + tokens.getAccess_token();
			IdMeController.logger.info("Token URL 5.2:" + url2);
			final ResponseEntity<User> responseEntity = (ResponseEntity<User>)this.restTemplate.getForEntity(url2, (Class)User.class, new Object[0]);
			final User user = (User)responseEntity.getBody();
			IdMeController.logger.info("userResponse:" + user.toString());
			storeDetails = this.idMeService.storeDetailsForIdProofing(user);
			IdMeController.logger.info("storeDetailsForIdProofing:" + storeDetails);
			for (final UserDetails str : user.getAttributes()) {
				IdMeController.logger.info("UserDetailsstr:" + str);
				System.out.println(str);
			}
			for (final Status status : user.getStatus()) {
				if ("identity".equals(status.getGroup())) {
					for (final String str2 : status.getSubgroups()) {
						IdMeController.logger.info("statusCode:" + status);
						if ("LOA3".equals(str2) && Boolean.parseBoolean(status.getVerified())) {
							idFlag = true;
						}else if ("IAL2".equals(str2) && Boolean.parseBoolean(status.getVerified())) {
							idFlag = true;
						}

					}
				}
				else if ("medical".equals(status.getGroup()) && Boolean.parseBoolean(status.getVerified())) {
					medFlag = true;
				}
				if (idFlag && medFlag) {
					verified = true;
					IdMeController.logger.info("verifiedCode:" + verified);
					break;
				}
			}
			if (verified) {
				if (storeDetails) {
					response.sendRedirect(this.h2hIdProofingSuccessUrl);
					return "eRx ID Proofing Completed Successfully";
				} else {
					response.sendRedirect(this.h2hIdProofingSuccessUrl);
					return null;
				}
			}
			return "Please complete your ID proofing, By Sign in again to id.me";
		}
		catch (Exception e) {
			IdMeController.logger.warn(e.getMessage(), (Throwable)e);
			response.sendRedirect(this.h2hIdProofingDeniedUrl);
			return null;
		}
		finally {
			this.domainFrom = null;
		}
		
	}
	
	@GetMapping({"/sendToIdProfingForWrapper"})
	public String redirectToIdProofingForWrapper(final HttpServletRequest request, final HttpServletResponse response, @RequestParam(value = "vendorId", required = false) final String vendorId) 
			throws Exception {
		try {
			final String url = "https://" + this.domain + "/oauth/authorize?client_id=" + this.client_id + "&redirect_uri=" + this.wrapperRedirectURLForIdProofingWebService + "&response_type=code&scope=http://idmanagement.gov/ns/assurance/ial/2/aal/2/erx&state="+vendorId;
			response.sendRedirect(url);
		}
		catch (Exception e) {
			IdMeController.logger.error(e.getMessage(), (Throwable)e);
		}
		return null;
	}
	
	@GetMapping({ "/receiveIdProofingForWrapper" })
	public String receiveIdProofingForWrapper(final HttpServletRequest request, final HttpServletResponse response, @RequestParam(value = "code", required = false) final String code, @RequestParam(value = "state", required = false) final String state,@RequestParam(value = "error", required = false) final String error) throws Exception {
		if (code == null && error != null && !error.equals("")) {
			response.sendRedirect(this.h2hIdProofingDeniedUrl);
			return error + " ==> " + request.getParameter("error_description");
		}
		boolean verified = false;
		boolean idFlag = false;
		boolean medFlag = false;
		try {
			Tokens token = new Tokens();
			token.setAccess_token(code);
			final String url = "https://" + this.domain + "/oauth/token?code=" + code + "&client_id=" + this.client_id + "&client_secret=" + this.client_secret + "&grant_type=authorization_code&redirect_uri=" + this.wrapperRedirectURLForIdProofingWebService;
			IdMeController.logger.info("url:" + url);
			final ResponseEntity<Tokens> entity = (ResponseEntity<Tokens>)this.restTemplate.postForEntity(url, (Object)null, (Class)Tokens.class, (Map)new HashMap());
			IdMeController.logger.info("entity:" + entity);
			final Tokens tokens = (Tokens)entity.getBody();
			IdMeController.logger.info("tokens:" + tokens);
			final String url2 = "https://" + this.domain + "/api/public/v3/attributes.json?access_token=" + tokens.getAccess_token();
			IdMeController.logger.info("Token URL 5.2:" + url2);
			final ResponseEntity<User> responseEntity = (ResponseEntity<User>)this.restTemplate.getForEntity(url2, (Class)User.class, new Object[0]);
			final User user = (User)responseEntity.getBody();
			IdMeController.logger.info("userResponse:" + user.toString());
			
			for (final Status status : user.getStatus()) {
				if ("identity".equals(status.getGroup())) {
					for (final String str2 : status.getSubgroups()) {
						IdMeController.logger.info("statusCode:" + status);
						if ("LOA3".equals(str2) && Boolean.parseBoolean(status.getVerified())) {
							idFlag = true;
						}else if ("IAL2".equals(str2) && Boolean.parseBoolean(status.getVerified())) {
							idFlag = true;
						}

					}
				}
				else if ("medical".equals(status.getGroup()) && Boolean.parseBoolean(status.getVerified())) {
					medFlag = true;
				}
				if (idFlag && medFlag) {
					verified = true;
					IdMeController.logger.info("verifiedCode:" + verified);
					break;
				}
			}
			if (verified) {
				IdProfingWrapperDto wrapper = this.idMeService.idProfingWrapper(user);
				
				response.sendRedirect(this.redirect+"?email="+wrapper.getEmail()+"&fname="+wrapper.getfName()+"&lname="+wrapper.getlName()
                +"&npi="+wrapper.getNpi()+"&uuid="+wrapper.getUuid()+"&state="+state);
				return null;
			}
			return "Please complete your ID proofing, By Sign in again to id.me";
		}
		catch (Exception e) {
			IdMeController.logger.warn(e.getMessage(), (Throwable)e);
			response.sendRedirect(this.h2hIdProofingDeniedUrl);
			return null;
		}
		finally {
			this.domainFrom = null;
		}
		
	}

}

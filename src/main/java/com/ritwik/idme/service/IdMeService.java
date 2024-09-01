/*
 * DigitalRx : com.ritwik.idme.service.IdMeService.java
 * Release : @SVN_RELEASE_NUMBER@
 * Copyright (c) 2022 H2HSolutions Inc, All Rights Reserved.
 * Redistribution and use in source and binary forms, with or without modification,
 * are not permitted without specific prior written permission.
 */

package com.ritwik.idme.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ritwik.idme.dto.IdMeDTO;
import com.ritwik.idme.dto.IdProofingDTO;
import com.ritwik.idme.dto.IdentificationDTO;
import com.ritwik.idme.util.IdProfingWrapperDto;
import com.ritwik.idme.util.Status;
import com.ritwik.idme.util.User;
import com.ritwik.idme.util.UserDetails;



@Service
public class IdMeService {

	private static final Logger logger = LoggerFactory.getLogger(IdMeService.class);
	private static String SS_REGISTRATION_EMAIL_USERID = "alerts"; 
	private static String SS_REGISTRATION_EMAIL_PASSWORD = "Ma32qe"; 
	private static String SS_REGISTRATION_EMAIL_TO = "saidurga.v@youngsoft.in";
	private static String  SS_REGISTRATION_EMAIL_SMTPPORT  = "25";
	private static String SS_REGISTRATION_EMAIL_SMTPHOST = "aspmx.l.google.com"; 
	private static String SS_REGISTRATION_EMAIL_FROM = "alerts@h2hdigitalrx.com";

	@Autowired
	private IdMeRepository idMeRepository;

	@Autowired
	private IdProofingRepository idProofingRepository;

	@Autowired
	private IdentificationRepository identificationRepository;

	public boolean storeDetails(User user) {
		boolean flag = false;
		try {
			IdMeDTO idMeDTO = convertUserToIdMeDTO(user);
			if(idMeDTO != null) {				
				String firstname=idMeDTO.getfName();
				String lastname=idMeDTO.getlName();
				String npi = idMeDTO.getNpi();
				String dea = idMeDTO.getDeaNumber();
				String email = idMeDTO.getEmail();
				String Name=firstname.concat(lastname);
				String fromEmailAddress = SS_REGISTRATION_EMAIL_FROM;
				String pass = SS_REGISTRATION_EMAIL_PASSWORD;
				String[] toEmailAddress = { SS_REGISTRATION_EMAIL_TO }; // list of recipient email addresses
				String emailSubject="User Registration Notification for ID.ME(EPCS)";
				
				StringBuilder bodyBuilder = new StringBuilder("");
				bodyBuilder.append("<html xmlns='http://www.w3.org/1999/xhtml'> <head> <meta http-equiv='Content-Type' content='text/html; charset=utf-8' /> <title>H2H Support</title> <link href='https://fonts.googleapis.com/css?family=Oswald:400,300,700' rel='stylesheet' type='text/css' /> </head>");
				bodyBuilder.append("<body> <table width='90%' border='0' style='background: #f6f6f6; border: 1px solid #ccc; margin: 0 auto; font-family: Arial, Helvetica, sans-serif; font-size: 13px; padding: 0; color: #333;'> <tr> <th colspan='3' align='center' valign='middle' style='background-color:#fff;padding:20px;border-bottom:1px solid #ccc'> <img src='https://h2hdigitalrx.com/IDme-Logo.png' width='80' height='29'> <span style='font-size:35px;padding:0px 5px 0px 5px'>+</span> <img src='https://h2hdigitalrx.com/Drx-logo.png' width='122' height='36'> </th> </tr> <tr> <td width='7%'></td> <td width='23%'>&nbsp;</td> <td width='70%'>&nbsp;</td> </tr> <tr> <td width='7%'></td> <td colspan='2'>User successfully enrolled with ID.me and completed Id Proofing & Two factor authentication for H2H Digital Rx EPCS capability.</td> </tr> <tr height = '10px'></tr>");
				bodyBuilder.append("<tr> <td width='7%'></td> <td width='23%'><strong>Name</strong></td> <td width='70%'>: ").append(firstname + " " + lastname).append("</td> </tr>");
				bodyBuilder.append("<tr> <td width='7%'></td> <td width='23%'><strong>NPI</strong></td> <td width='70%'>: ").append(npi).append("</td> </tr>");
				bodyBuilder.append("<tr> <td width='7%'></td> <td width='23%'><strong>DEA</strong></td> <td width='70%'>: ").append(dea).append("</td> </tr>");
				bodyBuilder.append("<tr> <td width='7%'></td> <td width='23%'><strong>E-mail</strong></td> <td width='70%'>: ").append(email).append("</td> </tr>");
				bodyBuilder.append("<tr> <td width='7%'></td> <td width='23%'>&nbsp;</td> <td width='70%'>&nbsp;</td> </tr> <tr></tr> <tr style='background-color: #0a2b4e; text-align: center; color: #fff; margin-top: 10px;'> <td colspan='3' style='padding: 10px 15px;'>&copy; <span style='color: #7cb930;'>H2H Solutions</span>. All Rights Reserved</td> </tr>");
				bodyBuilder.append("</table> </body> </html>");
				String emailMsg = bodyBuilder.toString();
				
				sendFromGMail(fromEmailAddress, pass, toEmailAddress, emailMsg, emailSubject);
				
				if (idMeDTO != null) {
					flag = true;
					updateUserDEA(idMeDTO.getNpi(), idMeDTO.getDeaNumber());
				}
			}
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}

		return flag;
	}
	

	private IdMeDTO convertUserToIdMeDTO(User user) {

		IdMeDTO idMeDTO = IdMeDTO.getInstance();
		idMeDTO.setId(generateID());
		user.getAttributes().forEach(userDetails -> {
			storeAttributes(userDetails, idMeDTO);
		});
		if (idMeDTO.getEmail() != null) {
			if (idMeRepository.findByEmail(idMeDTO.getEmail()) != null)
				return null;
		}
		user.getStatus().forEach(status -> storeStatus(status, idMeDTO));
		idMeDTO.setCreatedDate(new Date(System.currentTimeMillis()));
		return idMeRepository.saveAndFlush(idMeDTO);

	}
	
	
	private void storeAttributes(UserDetails userDetails, IdMeDTO idMeDTO) {
		try {
			if ("email".equalsIgnoreCase(userDetails.getHandle())) {
				idMeDTO.setEmail(userDetails.getValue());
			} else if ("fname".equalsIgnoreCase(userDetails.getHandle())) {
				idMeDTO.setfName(userDetails.getValue());
			} else if ("lname".equalsIgnoreCase(userDetails.getHandle())) {
				idMeDTO.setlName(userDetails.getValue());
			} else if ("medical_npi_number".equalsIgnoreCase(userDetails.getHandle())) {
				idMeDTO.setNpi(userDetails.getValue());
			} else if ("medical_dea_number".equalsIgnoreCase(userDetails.getHandle())) {
				idMeDTO.setDeaNumber(userDetails.getValue());
			} else if ("medical_dea_expiration".equalsIgnoreCase(userDetails.getHandle())) {
				idMeDTO.setDeaExpDate(convertDate(userDetails.getValue()));
			} else if ("medical_dea_schedule".equalsIgnoreCase(userDetails.getHandle())) {
				idMeDTO.setDeaLevel(getDeaLevel(userDetails.getValue()));
			} else if ("uuid".equalsIgnoreCase(userDetails.getHandle())) {
				idMeDTO.setUuid(userDetails.getValue());
			}
			/*
			 * String fromEmailAddress = SS_REGISTRATION_EMAIL_FROM; String pass =
			 * SS_REGISTRATION_EMAIL_PASSWORD; String[] toEmailAddress = {
			 * SS_REGISTRATION_EMAIL_TO }; // list of recipient email addresses String
			 * emailMsg=""; String emailSubject=""; sendFromGMail(fromEmailAddress, pass,
			 * toEmailAddress, emailMsg, emailSubject);
			 */
		} 

		catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
	}
	

	private void storeStatus(Status status, IdMeDTO idMeDTO) {
		if ("identity".equals(status.getGroup())) {
			for (String str : status.getSubgroups()) {
				if ("LOA3".equals(str) && Boolean.parseBoolean(status.getVerified())) {
					idMeDTO.setLOA3Verified(true);
				}
				else if ("IAL2".equals(str) && Boolean.parseBoolean(status.getVerified())) {
					idMeDTO.setLOA3Verified(true);
				}
			}
		}
	}
	

	private String generateID() {
		char[] chars = (System.currentTimeMillis() + "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdeghijklmnopqrstuvwxyz")
				.toCharArray();
		Random rnd = new Random();
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < 20; i++)
			sb.append(chars[rnd.nextInt(chars.length)]);
		return sb.toString();
	}

	
	private static Date convertDate(String strDate) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.parse(strDate);
	}
	

	private void updateUserDEA(String npi, String dea) {
		IdentificationDTO identificationDTO = null;
		IdentificationDTO identDTO = null;
		IdentificationDTO idDTO = IdentificationDTO.getInstance();
		List<IdentificationDTO> list = identificationRepository.findByIdentificationAndType(npi, "HPI");
		if (list.size() > 0)
			identificationDTO = list.iterator().next();
		
		List<IdentificationDTO> identificationDTOs = identificationRepository.findByTypeAndPersonId("DH",
				identificationDTO.getPersonId());
		if (identificationDTOs.size() > 0) {
			identDTO = identificationDTOs.iterator().next();
			identDTO.setIdentification(dea);
			identificationRepository.saveAndFlush(identDTO);
		} else {
			BeanUtils.copyProperties(identificationDTO, idDTO);
			idDTO.setId(generateID());
			idDTO.setType("DH");
			idDTO.setIdentification(dea);
			identificationRepository.saveAndFlush(idDTO);
		}

	}

	
	private int getDeaLevel(String dea) {
		int deaNumber = 2;
		dea = dea.replaceAll("\\[", "").replaceAll("\\]", "");
		if (dea.contains(","))
			for (String s : dea.split(",")) {
				deaNumber = Integer.parseInt(s.trim());
			}
		return deaNumber;
	}


	private static void sendFromGMail(String fromEmailAddress, String pass, String[] toEmailAddress, String emailMsg, String emailSubject) {
		/*
		 * IdMeDTO idMeDTO = IdMeDTO.getInstance(); String firstname=idMeDTO.getfName();
		 * String lastname=idMeDTO.getlName(); String Name=firstname.concat(lastname);
		 */
		try
		{

			Properties emailProperties=	new Properties(); 

			emailProperties.put("mail.transport.protocol", "smtp");
			emailProperties.put("mail.smtp.host", SS_REGISTRATION_EMAIL_SMTPHOST);
			emailProperties.put("mail.smtp.auth", "true");
			emailProperties.put("mail.smtp.user", SS_REGISTRATION_EMAIL_USERID);
			emailProperties.put("mail.smtp.password", SS_REGISTRATION_EMAIL_PASSWORD);
			emailProperties.put("mail.smtp.starttls.enable", "true");
			emailProperties.put("mail.smtp.socketFactory.class",
					"javax.net.ssl.SSLSocketFactory");
			Session mailSession = Session.getDefaultInstance(emailProperties, null);
			mailSession.setDebug(true);
			Transport transport = mailSession.getTransport("smtp");

			MimeMessage message = new MimeMessage(mailSession);
			message.setSubject(emailSubject);
			message.setContent(emailMsg, "text/html");
			message.setFrom(new InternetAddress(fromEmailAddress));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(SS_REGISTRATION_EMAIL_TO));
			message.saveChanges();
			transport.connect(SS_REGISTRATION_EMAIL_SMTPHOST, Integer.parseInt(SS_REGISTRATION_EMAIL_SMTPPORT), SS_REGISTRATION_EMAIL_USERID, SS_REGISTRATION_EMAIL_PASSWORD);

			transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
			transport.close();
		}
		catch (MessagingException me) {
			me.printStackTrace();
			logger.error("ID.Me mail sending failed:",me);
		}

	}


	public boolean storeDetailsForIdProofing(User user) {
		boolean flag = false;
		try {
			IdProofingDTO idProofingDTO = convertUserToIdProofingDTO(user);
			if(idProofingDTO != null) {				
				String firstname=idProofingDTO.getfName();
				String lastname=idProofingDTO.getlName();
				String npi = idProofingDTO.getNpi();
				String dea = idProofingDTO.getDeaNumber();
				String email = idProofingDTO.getEmail();
				String Name=firstname.concat(lastname);
				String fromEmailAddress = SS_REGISTRATION_EMAIL_FROM;
				String pass = SS_REGISTRATION_EMAIL_PASSWORD;
				String[] toEmailAddress = { SS_REGISTRATION_EMAIL_TO }; // list of recipient email addresses
				String emailSubject="User Registration Notification for ID.ME(eRx)";
				
				StringBuilder bodyBuilder = new StringBuilder("");
				bodyBuilder.append("<html xmlns='http://www.w3.org/1999/xhtml'> <head> <meta http-equiv='Content-Type' content='text/html; charset=utf-8' /> <title>H2H Support</title> <link href='https://fonts.googleapis.com/css?family=Oswald:400,300,700' rel='stylesheet' type='text/css' /> </head>");
				bodyBuilder.append("<body> <table width='90%' border='0' style='background: #f6f6f6; border: 1px solid #ccc; margin: 0 auto; font-family: Arial, Helvetica, sans-serif; font-size: 13px; padding: 0; color: #333;'> <tr> <th colspan='3' align='center' valign='middle' style='background-color:#fff;padding:20px;border-bottom:1px solid #ccc'> <img src='https://h2hdigitalrx.com/IDme-Logo.png' width='80' height='29'> <span style='font-size:35px;padding:0px 5px 0px 5px'>+</span> <img src='https://h2hdigitalrx.com/Drx-logo.png' width='122' height='36'> </th> </tr> <tr> <td width='7%'></td> <td width='23%'>&nbsp;</td> <td width='70%'>&nbsp;</td> </tr> <tr> <td width='7%'></td> <td colspan='2'>User successfully enrolled with ID.me and completed Id Proofing for H2H Digital Rx eRx capability.</td> </tr> <tr height = '10px'></tr>");
				bodyBuilder.append("<tr> <td width='7%'></td> <td width='23%'><strong>Name</strong></td> <td width='70%'>: ").append(firstname + " " + lastname).append("</td> </tr>");
				bodyBuilder.append("<tr> <td width='7%'></td> <td width='23%'><strong>NPI</strong></td> <td width='70%'>: ").append(npi).append("</td> </tr>");
				bodyBuilder.append("<tr> <td width='7%'></td> <td width='23%'><strong>E-mail</strong></td> <td width='70%'>: ").append(email).append("</td> </tr>");
				bodyBuilder.append("<tr> <td width='7%'></td> <td width='23%'>&nbsp;</td> <td width='70%'>&nbsp;</td> </tr> <tr></tr> <tr style='background-color: #0a2b4e; text-align: center; color: #fff; margin-top: 10px;'> <td colspan='3' style='padding: 10px 15px;'>&copy; <span style='color: #7cb930;'>H2H Solutions</span>. All Rights Reserved</td> </tr>");
				bodyBuilder.append("</table> </body> </html>");
				String emailMsg = bodyBuilder.toString();
				
				sendFromGMail(fromEmailAddress, pass, toEmailAddress, emailMsg, emailSubject);
				flag = true;
			}
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}

		return flag;
	}


	private IdProofingDTO convertUserToIdProofingDTO(User user) {

		IdProofingDTO idProofingDTO = IdProofingDTO.getInstance();
		idProofingDTO.setId(generateID());
		user.getAttributes().forEach(userDetail -> {
			storeAttributesForIdProofing(userDetail, idProofingDTO);
		});
		if (idProofingDTO.getEmail() != null) {
			if (idProofingRepository.findByEmail(idProofingDTO.getEmail()) != null)
				return null;
		}
		IdentificationDTO identificationDTO = getpersonId(idProofingDTO.getNpi());
		user.getStatus().forEach(status -> storeStatusIdProofing(status, idProofingDTO));
		idProofingDTO.setCreatedDate(new Date(System.currentTimeMillis()));
		if(identificationDTO != null) {			
			idProofingDTO.setpersonId(identificationDTO.getPersonId());
			return idProofingRepository.saveAndFlush(idProofingDTO);
		} else {
			logger.error("IdentificationDTO IS NULL, NPI:" + idProofingDTO.getNpi());
			return null;
		}
		
	}


	


	private void storeAttributesForIdProofing(UserDetails userDetails, IdProofingDTO idProofingDTO) {
		
		try {
			
			if ("email".equalsIgnoreCase(userDetails.getHandle())) {
				idProofingDTO.setEmail(userDetails.getValue());
			} else if ("fname".equalsIgnoreCase(userDetails.getHandle())) {
				idProofingDTO.setfName(userDetails.getValue());
			} else if ("lname".equalsIgnoreCase(userDetails.getHandle())) {
				idProofingDTO.setlName(userDetails.getValue());
			} else if ("medical_npi_number".equalsIgnoreCase(userDetails.getHandle())) {
				idProofingDTO.setNpi(userDetails.getValue());
			} else if ("medical_dea_number".equalsIgnoreCase(userDetails.getHandle())) {
				idProofingDTO.setDeaNumber(userDetails.getValue());
			} else if ("medical_dea_expiration".equalsIgnoreCase(userDetails.getHandle())) {
				idProofingDTO.setDeaExpDate(convertDate(userDetails.getValue()));
			} else if ("medical_dea_schedule".equalsIgnoreCase(userDetails.getHandle())) {
				idProofingDTO.setDeaLevel(getDeaLevel(userDetails.getValue()));
			} else if ("uuid".equalsIgnoreCase(userDetails.getHandle())) {
				idProofingDTO.setUuid(userDetails.getValue());
			}
			
			
			/*
			 * String fromEmailAddress = SS_REGISTRATION_EMAIL_FROM; String pass =
			 * SS_REGISTRATION_EMAIL_PASSWORD; String[] toEmailAddress = {
			 * SS_REGISTRATION_EMAIL_TO }; // list of recipient email addresses String
			 * emailMsg=""; String emailSubject=""; sendFromGMail(fromEmailAddress, pass,
			 * toEmailAddress, emailMsg, emailSubject);
			 */
		} 

		catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
	}
	

	private void storeStatusIdProofing(Status status, IdProofingDTO idProofingDTO) {
		if ("identity".equals(status.getGroup())) {
			for (String str : status.getSubgroups()) {
				if ("LOA3".equals(str) && Boolean.parseBoolean(status.getVerified())) {
					idProofingDTO.setLOA3Verified(true);
				}
			}
		}
	}
	

	private IdentificationDTO getpersonId(String npi) {
		IdentificationDTO identificationDTO = null;
		
		try {
			List<IdentificationDTO> list =  identificationRepository.findByIdentificationAndType(npi, "HPI");			
			identificationDTO = list.get(0);
			return identificationDTO;
		}
		catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
		return identificationDTO;
	}

	public IdProfingWrapperDto idProfingWrapper(User user) {
		IdProfingWrapperDto idProfingWrapperDto = new IdProfingWrapperDto();
		user.getAttributes().forEach(
				userDetails ->{
					 try {
						 if
					 ("email".equalsIgnoreCase(userDetails.getHandle())) {
						 idProfingWrapperDto.setEmail(userDetails.getValue());
					 } else if("fname".equalsIgnoreCase(userDetails.getHandle())) {
						 idProfingWrapperDto.setfName(userDetails.getValue());
					 } else if("lname".equalsIgnoreCase(userDetails.getHandle())) {
						 idProfingWrapperDto.setlName(userDetails.getValue());
					 } else if("medical_npi_number".equalsIgnoreCase(userDetails.getHandle())) {
						 idProfingWrapperDto.setNpi(userDetails.getValue());
					 } else if("medical_dea_number".equalsIgnoreCase(userDetails.getHandle())) {
						 idProfingWrapperDto.setDeaNumber(userDetails.getValue());
					 } else if("medical_dea_expiration".equalsIgnoreCase(userDetails.getHandle())) {
						 idProfingWrapperDto.setDeaExpDate(convertDate(userDetails.getValue()));
					 } else if ("medical_dea_schedule".equalsIgnoreCase(userDetails.getHandle())) {
						 idProfingWrapperDto.setDeaLevel(getDeaLevel(userDetails.getValue()));
					 } else if("uuid".equalsIgnoreCase(userDetails.getHandle())) {
						 idProfingWrapperDto.setUuid(userDetails.getValue()); }

					} catch (Exception e) { logger.warn(e.getMessage(), e); }
					 
					 });
		return idProfingWrapperDto;
	}
}
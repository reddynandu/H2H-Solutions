package com.ritwik.idme.util;

import java.io.Serializable;

public class Tokens implements Serializable {

	private static final long serialVersionUID = 23452345234523L;

	private String access_token;
	private String token_type;
	private String expires_in;
	private String refresh_token;
	private String refresh_expires_in;

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getToken_type() {
		return token_type;
	}

	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}

	public String getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(String expires_in) {
		this.expires_in = expires_in;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}

	public String getRefresh_expires_in() {
		return refresh_expires_in;
	}

	public void setRefresh_expires_in(String refresh_expires_in) {
		this.refresh_expires_in = refresh_expires_in;
	}

	@Override
	public String toString() {
		return "Tokens [access_token=" + access_token + ", token_type=" + token_type + ", expires_in=" + expires_in
				+ ", refresh_token=" + refresh_token + ", refresh_expires_in=" + refresh_expires_in + "]";
	}

}

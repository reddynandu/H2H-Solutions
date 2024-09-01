package com.ritwik.idme.util;

import java.util.List;

public class User {

	private List<UserDetails> attributes;
	private List<Status> status;

	public List<UserDetails> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<UserDetails> attributes) {
		this.attributes = attributes;
	}

	public List<Status> getStatus() {
		return status;
	}

	public void setStatus(List<Status> status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "User [attributes=" + attributes + ", status=" + status + "]";
	}

}

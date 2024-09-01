package com.ritwik.idme.util;

public class UserDetails {

	private String handle;
	private String name;
	private String value;

	// Getter Methods

	public String getHandle() {
		return handle;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	// Setter Methods

	public void setHandle(String handle) {
		this.handle = handle;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "UserDetails [handle=" + handle + ", name=" + name + ", value=" + value + "]";
	}

}

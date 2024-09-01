package com.ritwik.idme.util;

import java.util.Arrays;

public class Status {

	private String group;
	private String[] subgroups;
	private String verified;

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String[] getSubgroups() {
		return subgroups;
	}

	public void setSubgroups(String[] subgroups) {
		this.subgroups = subgroups;
	}

	public String getVerified() {
		return verified;
	}

	public void setVerified(String verified) {
		this.verified = verified;
	}

	@Override
	public String toString() {
		return "Status [group=" + group + ", subgroups=" + Arrays.toString(subgroups) + ", verified=" + verified + "]";
	}

}

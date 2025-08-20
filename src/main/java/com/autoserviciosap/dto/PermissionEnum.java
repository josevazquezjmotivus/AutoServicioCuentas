package com.autoserviciosap.dto;

public enum PermissionEnum {

	USER_ADMINISTRATOR("USRADM"), ENTERPRISE_ADMINISTRATOR("ENTADM"), ENTERPRISE_USER("ENTUSR");

	private String id;

	private PermissionEnum(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}

package com.autoserviciosap.dto;

public class SelfServiceUserOperationRequestDto {

	private String username;
	private String email;
	private SelfServiceRequestTypeEnum type;
	private String password;
	private String systemId;
	private String recaptchaVerifyToken;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public SelfServiceRequestTypeEnum getType() {
		return type;
	}

	public void setType(SelfServiceRequestTypeEnum type) {
		this.type = type;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getRecaptchaVerifyToken() {
		return recaptchaVerifyToken;
	}

	public void setRecaptchaVerifyToken(String recaptchaVerifyToken) {
		this.recaptchaVerifyToken = recaptchaVerifyToken;
	}

}

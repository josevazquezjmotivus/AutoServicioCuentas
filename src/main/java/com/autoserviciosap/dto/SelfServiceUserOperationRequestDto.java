package com.autoserviciosap.dto;

public record SelfServiceUserOperationRequestDto(
	String username,
	String email,
	SelfServiceRequestTypeEnum type,
	String password,
	String systemId,
	String recaptchaVerifyToken
) {}

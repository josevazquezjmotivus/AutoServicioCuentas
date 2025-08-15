package com.autoserviciosap.dto;

public enum SelfServiceRequestTypeEnum {

	VALIDAR_EMAIL_Y_USUARIO("USREMLCHK", "0"), //
	DESBLOQUEO_DE_USUARIO("USRUNLCK", "1"), //
	REESTABLECER_CONTRASEÑA_TEMPORAL("PWDRESET", "2"), //
	REESTABLECER_CONTRASEÑA_DEFINITIVA("PWDRESET", "3"), //
	VALIDAR_RESETEO("RSTCHECK", "4"), //
	VALIDAR_DESBLOQUEO("LCKCHECK", "5");

	private String id;
	private String opcion;

	private SelfServiceRequestTypeEnum(String id, String opcion) {
		this.id = id;
		this.opcion = opcion;
	}

	public String getId() {
		return id;
	}

	public String getOpcion() {
		return opcion;
	}

}

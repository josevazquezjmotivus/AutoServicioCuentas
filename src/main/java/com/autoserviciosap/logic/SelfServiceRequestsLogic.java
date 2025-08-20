package com.autoserviciosap.logic;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import com.autoserviciosap.ApiException;
import com.autoserviciosap.AppProperties;
import com.autoserviciosap.model.SelfServiceRequest;
import com.autoserviciosap.resources.DesbloqueosSapResource;
import com.autoserviciosap.resources.InternationalizationStateless;
import com.autoserviciosap.resources.ZWS_DESBLOQUEO_USUARIOS.ZMFDESBLOUSUARIOResponse;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
// Eliminados imports legacy de fechas
// Eliminado import no usado

@Stateless
public class SelfServiceRequestsLogic {

	@PersistenceContext(unitName = "AutoServicioSAP")
	private EntityManager entityManager;

	@Inject
	private DesbloqueosSapResource desbloqueosSapResource;

	@Inject
	private AppProperties appProperties;
	
	@Inject 
	InternationalizationStateless i18n;

	@Inject
	private TemplatesLogic templatesLogic;

	private SelfServiceRequest obtenerSolicitud(String uuid, String confirmationCode) {

		SelfServiceRequest e = entityManager.find(SelfServiceRequest.class, uuid);

		// NO ENCONTRADA
		if (e == null) throw new ApiException(404, "La solicitud " + uuid + " no existe");

		// CODIGO INCORRECTO
		if (!e.getConfirmationCode().equalsIgnoreCase(confirmationCode)) throw new ApiException(404,
				"La solicitud " + uuid + " con código de confirmación " + confirmationCode + " no existe");

		// SOLICITUD EXPIRADA
		long seconds = LocalDateTime //
				.from(e.getConfirmationExpirationDate().toLocalDateTime()) //
				.until(LocalDateTime.now(), ChronoUnit.SECONDS);
		if (seconds > 0) throw new ApiException(409, "La solicitud ha expirado, realice una nueva solicitud");

		// SOLICITUD GASTADA
		if (e.getFulfillmentDate() != null) { 
			Timestamp t2 = Timestamp.from(LocalDateTime.now().toInstant(OffsetDateTime.now().getOffset()));
			if(pasoPorTiempo(e.getFulfillmentDate(), t2)) {
				System.out.println("Paso por que se envio varias veces");
			}else {
			throw new ApiException(409,
				"La solicitud ya ha sido realizada y no puede ser realizada nuevamente");
			}
		}

		// SOLICITUD INVALIDADA
		if (e.isInvalidated()) throw new ApiException(409,
				"La solicitud ya ha sido invalidada por una solicitud más reciente");

		return e;
	}

	public String confirmarSolicitud(String uuid, String confirmationCode,String idioma) {
		return confirmarSolicitud(uuid, confirmationCode, null,idioma);
	}

	public String confirmarSolicitud(String uuidSolicitud, String confirmationCode, String password,String idioma) {
		
		System.out.println("-------------------------------- Confirmando solicitud ");
		
		if (idioma == null)
			idioma = "es";
		if ("".equals(idioma))
			idioma = "es";

		SelfServiceRequest e = obtenerSolicitud(uuidSolicitud, confirmationCode);

		ZMFDESBLOUSUARIOResponse result = null;

		if ("USRUNLCK".equalsIgnoreCase(e.getSelfServiceRequestType().getId())) {
			result = desbloqueosSapResource.desbloquearUsuario( //
					e.getSystem(), //
					e.getEnvironment(), //
					e.getTargetUser());
		} else if ("PWDRESET".equalsIgnoreCase(e.getSelfServiceRequestType().getId())) {
			result = desbloqueosSapResource.establecerContraseñaTemporalParaUsuario( //
					e.getSystem(), //
					e.getEnvironment(), //
					e.getTargetUser(), //
					password);
		} else {
			throw new ApiException(501,
					"No se puede manejar el tipo " + e.getSelfServiceRequestType().getId() + " en esta sección");
		}
		if (!"S".equalsIgnoreCase(result.getTIPO())) throw new ApiException(500,
				"La solicitud no fué realizada por el siguiente motivo: " + result.getMESSAGE());

		e.setFulfillmentDate(Timestamp.from(LocalDateTime.now().toInstant(OffsetDateTime.now().getOffset())));

		String targetUser = e.getTargetUser();
		String requestType = e.getSelfServiceRequestType().getName();

		Map<String, Object> params = new HashMap<>();
		params.put("${request-type}", requestType);
		params.put("${sap-user}", targetUser);

		String html = "";
		if (idioma.equals("es")) {
			html = templatesLogic.solveTemplate("html-templates/sap-confirmation-message.html", params);
		}
		if (idioma.equals("en")) {
			 html = templatesLogic.solveTemplate("html-templates/sap-confirmation-message_en.html", params);
		}

		return html;
	}

	public String obtenerFormularioParaEstablecerContraseñaTemporal(String uuid, String confirmationCode,
			String errorMessage,String idioma) {
		
		if(idioma == null) {
			idioma = "es";
		}
		if("".equals(idioma)) {
			idioma="es";
		}

		if (errorMessage == null) errorMessage = "";

		String action = appProperties.getApiUrl() + "/self-service-requests/" + uuid
				+ "/reset-password-form?confirmation-code=" + confirmationCode + "&idioma="+idioma;

		Map<String, Object> params = new HashMap<>();
		params.put("${action}", action);
		params.put("${idioma}", idioma);
		params.put("${error}", errorMessage);

		String html = "";
		if (idioma.equals("es")) {
		html = templatesLogic.solveTemplate("html-templates/sap-password-reset-form.html", params);
		}
		if (idioma.equals("en")) {
			html = templatesLogic.solveTemplate("html-templates/sap-password-reset-form_en.html", params);
		}
		
		return html;

	}

	public String reestablecerPassword(String uuid, String confirmationCode, String password, String passwordRepeat,String idioma) {
		if(idioma == null)
			idioma = "es";
		if("".equals(idioma))
			idioma = "es";
		if(idioma.equals("es")) {
			if (password == null) return obtenerFormularioParaEstablecerContraseñaTemporal(uuid, confirmationCode,
					"Falta la contraseña",idioma);
			if (!password.contentEquals(passwordRepeat)) return obtenerFormularioParaEstablecerContraseñaTemporal(uuid,
					confirmationCode, "Ambas contraseñas deben coincidir",idioma);
		}
		if(idioma.equals("en")) {
			if (password == null) return obtenerFormularioParaEstablecerContraseñaTemporal(uuid, confirmationCode,
					"Password is missing",idioma);
			if (!password.contentEquals(passwordRepeat)) return obtenerFormularioParaEstablecerContraseñaTemporal(uuid,
					confirmationCode, "Both passwords must match",idioma);
		}

		try {
			return confirmarSolicitud(uuid, confirmationCode, password,idioma);
		} catch (ApiException ex) {
			return obtenerFormularioParaEstablecerContraseñaTemporal(uuid, confirmationCode, "" + ex.getEntity(),idioma);
		}
	}
	
	private boolean pasoPorTiempo(Timestamp t,Timestamp t2) {
		System.out.println(t);
		var time1 = t.toLocalDateTime().toLocalTime();
		var time2 = t2.toLocalDateTime().toLocalTime();
		System.out.println("f:" + time1);
		int hour = time1.getHour();
		System.out.println(hour);
		int minutes = time1.getMinute();
		System.out.println(minutes);
		System.out.println("f:" + time2);
		int hour2 = time2.getHour();
		System.out.println(hour2);
		int minutes2 = time2.getMinute();
		System.out.println(minutes2);
		boolean pasaPorTiempo = (hour2 == hour) && (minutes == minutes2);
		System.out.println("paso por tiempo:" + pasaPorTiempo);
		return pasaPorTiempo;
	}
	
	public static void main(String args[]) {
		Timestamp t = Timestamp.from(LocalDateTime.now().toInstant(OffsetDateTime.now().getOffset()));
		Timestamp t2 = Timestamp.from(LocalDateTime.now().toInstant(OffsetDateTime.now().getOffset()));
		SelfServiceRequestsLogic s = new SelfServiceRequestsLogic();
		s.pasoPorTiempo(t, t2);
	
	}

}
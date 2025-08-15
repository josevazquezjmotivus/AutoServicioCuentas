package com.autoserviciosap.logic;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.xml.ws.Holder;

import com.autoserviciosap.ApiException;
import com.autoserviciosap.AppProperties;
import com.autoserviciosap.dto.PermissionEnum;
import com.autoserviciosap.dto.SelfServiceRequestTypeEnum;
import com.autoserviciosap.model.Enterprise;
import com.autoserviciosap.model.Environment;
import com.autoserviciosap.model.SelfServiceRequest;
import com.autoserviciosap.model.SelfServiceRequestType;
import com.autoserviciosap.model.User;
import com.autoserviciosap.resources.CaptchaValidatorResource;
import com.autoserviciosap.resources.DesbloqueosSapResource;
import com.autoserviciosap.resources.EmailResource;
import com.autoserviciosap.resources.InternationalizationStateless;
import com.autoserviciosap.resources.ZWS_DESBLOQUEO_USUARIOS.ZMFDESBLOUSUARIOResponse;
import com.autoserviciosap.resources.ZWS_DESBLOQUEO_USUARIOS.ZWSBloqueoDesbloqueoUsuarios;

@Stateless
public class EnvironmentsLogic {

	@PersistenceContext(unitName = "AutoServicioSAP")
	private EntityManager entityManager;

	@Inject
	private EnterprisesLogic enterprisesLogic;
	
	@Inject
	private EventoLogic eventoLogica;

	@Inject
	private SessionLogic sessionLogic;

	@Inject
	private EmailResource emailResource;

	@Inject
	private CaptchaValidatorResource captchaResource;

	@Inject
	private AppProperties appProperties;

	@Inject 
	InternationalizationStateless i18n;

	public List<Environment> obtenerAmbientes() {
		return entityManager.createQuery("" //
				+ "SELECT e " //
				+ "FROM Environment AS e " //
				+ "LEFT JOIN FETCH e.systems "
				+ "WHERE e.systemVisible = true order by e.alias" //
				+ "", Environment.class).getResultList();
	}

	public Environment obtenerAmbiente(String uuid) throws ApiException {
		sessionLogic.obtenerUsuarioAutorizado(PermissionEnum.ENTERPRISE_ADMINISTRATOR);
		List<Environment> results = entityManager.createQuery("" //
				+ "SELECT e " //
				+ "FROM Environment AS e " //
				+ "LEFT JOIN FETCH e.systems " //
				+ "WHERE e.uuid = :uuid" //
				+ "", Environment.class) //
				.setParameter("uuid", uuid) //
				.getResultList();
		Environment e = results.isEmpty() ? null : results.get(0);
		if (e == null) throw new ApiException(404, i18n.get("error-ambiente-uuid") + " \"" + uuid + "\"");
		return e;
	}

	public Environment crearEntornoDeEmpresa(String enterpriseUuid, Environment e) {
		sessionLogic.obtenerUsuarioAutorizado(PermissionEnum.ENTERPRISE_ADMINISTRATOR);
		Enterprise f = enterprisesLogic.obtenerEmpresa(enterpriseUuid);
		e.setEnterprise(f);
		e.setUuid(UUID.randomUUID().toString());
		ApiException.validateBean(e);
		entityManager.persist(e);
		return e;
	}

	public Environment actualizarAmbiente(String uuid, Environment e) throws ApiException {
		sessionLogic.obtenerUsuarioAutorizado(PermissionEnum.ENTERPRISE_ADMINISTRATOR);
		Environment ambiente = obtenerAmbiente(uuid);
		ApiException.validateBean(e);
		ambiente.setAlias(e.getAlias());
		ambiente.setUsername(e.getUsername());
		ambiente.setPassword(e.getPassword());
		ambiente.setWsdlLocation(e.getWsdlLocation());
		ambiente.setSystemsEnabled(e.isSystemsEnabled());
		ambiente.setSystems(e.getSystems());
		ambiente.setSystemVisible(e.isSystemVisible());
		return ambiente;
	}

	public void eliminarAmbiente(String uuid) throws ApiException {
		Environment e = obtenerAmbiente(uuid);
		entityManager.remove(e);
	}

	public SelfServiceRequest procesarPeticion(String systemId, String environmentUuid, String usuario,
			SelfServiceRequestTypeEnum typeEnum) throws ApiException {

		User user = sessionLogic.obtenerUsuarioAutorizado(PermissionEnum.ENTERPRISE_USER);

		Environment environment = entityManager.find(Environment.class, environmentUuid);

		if (usuario == null || usuario.isEmpty()) throw new ApiException(400, i18n.get("error-falta-nom-usuario"));

		if (environment == null) throw new ApiException(404, i18n.get("error-ambiente"));

		if (!environment.getEnterprise().getUsers().contains(user)) throw new ApiException(403,
				i18n.get("error-us-sin-permiso"));

		String notes = null;

		switch (typeEnum) {
		case DESBLOQUEO_DE_USUARIO:
			notes = desbloquearUsuario(environment, usuario, systemId);
			break;
		case REESTABLECER_CONTRASEÑA_TEMPORAL:
			notes = reestablecerContraseña(environment, usuario);
			break;
		default:
			throw new ApiException(501, i18n.get("error-no-implementado"));
		}

		SelfServiceRequestType type = entityManager.find(SelfServiceRequestType.class, typeEnum.getId());

		SelfServiceRequest request = new SelfServiceRequest();
		request.setUuid(UUID.randomUUID().toString());
		request.setRequestTimestamp(Timestamp.valueOf(LocalDateTime.now()));
		request.setUser(user);
		request.setEnvironment(environment);
		request.setSelfServiceRequestType(type);
		request.setNotes(notes);
		request.setTargetUser(usuario);

		ApiException.validateBean(request);

		entityManager.persist(request);

		return request;
	}

	private String desbloquearUsuario(Environment environment, String usuario, String destino) throws ApiException {

		try {

			URL wsdlLocation = new URL(environment.getWsdlLocation());

			Holder<String> tipo = new Holder<String>();
			Holder<String> message = new Holder<String>();
			Holder<String> password2 = new Holder<String>();
			Holder<String> titulo = new Holder<String>();
			new ZWSBloqueoDesbloqueoUsuarios(wsdlLocation).getZBDBloqueoDesbloqueoUsuarios() //
					.zmfDESBLOUSUARIO(destino, null, null, "1", null, usuario, message, password2, tipo, titulo);

			if ("S".equalsIgnoreCase(tipo.value)) {

				Map<String, Object> params = new HashMap<>();
				params.put("${sap-user}", "ANGLOBAL_EXPRESS");

				emailResource.enviarTemplateParalelo( //
						sessionLogic.obtenerUsuarioAutenticado().getEmail(), //
						"Recuperación de contraseña de portal de auto servicio SAP", //
						"email-templates/email-successful-unlock-confirmation.html", params);
			}

			return message.value;

		} catch (Exception e) {
			e.printStackTrace();
			throw new ApiException(500, e.getMessage());
		}
	}

	private String reestablecerContraseña(Environment environment, String usuario) throws ApiException {
		throw new ApiException(501, i18n.get("error-restcont-no-imp"));
	}

	public List<SelfServiceRequest> obtenerPeticionesDeAutoServicioDeEntorno(String uuid) throws ApiException {
		User user = sessionLogic.obtenerUsuarioAutorizado(PermissionEnum.ENTERPRISE_ADMINISTRATOR);
		Environment environment = obtenerAmbiente(uuid);
		if (!environment.getEnterprise().getUsers().contains(user)) throw new ApiException(403,
				i18n.get("error-us-sin-permiso"));
		return environment.getRequests();
	}

	public List<Environment> obtenerAmbientesParaUsuarioAutenticado() {
		User user = sessionLogic.obtenerUsuarioAutorizado(PermissionEnum.ENTERPRISE_USER);
		List<Environment> environments = new ArrayList<>();
		for (Enterprise enterprise : user.getEnterprises()) {
			for (Environment environment : enterprise.getEnvironments()) {
				environments.add(environment);
			}
		}
		return environments;
	}

	@Context
	private HttpServletRequest httpServletRequest;

	@Inject
	private DesbloqueosSapResource desbloqueosSapResource;

	private static final Logger LOGGER = Logger.getLogger(EnvironmentsLogic.class.getName());

	public SelfServiceRequest procesarPeticionAutoServicio( //
			String systemId, //
			String uuid, //
			String username, //
			String email, //
			String captchaResponse, //
			SelfServiceRequestTypeEnum operacion) {
		
		String idioma = i18n.getSessionLanguage();
		if(idioma == null)
			idioma = "es";
		if("".equals(idioma))
			idioma = "es";

		captchaResource.validateCaptchaIfEnabled(captchaResponse);

		JsonObject operacionJson = operacion == null ? null
				: Json.createObjectBuilder() //
						.add("id", operacion.getId()) //
						.add("opcion", operacion.getOpcion()) //
						.build();
		JsonObjectBuilder logJsonBuilder = Json.createObjectBuilder() //
				.add("uuid", uuid) //
				.add("username", username) //
				.add("username", email) //
				.add("operacion", operacionJson);

		if (systemId != null) {
			logJsonBuilder.add("system", systemId);
		}

		JsonObject logJson = logJsonBuilder.build();
		LOGGER.info(logJson.toString());

		switch (operacion) {
		case REESTABLECER_CONTRASEÑA_DEFINITIVA:
			throw new ApiException(501, i18n.get("error-opcion-no-imp-aun"));
		case REESTABLECER_CONTRASEÑA_TEMPORAL:
		case DESBLOQUEO_DE_USUARIO:
			break;
		default:
			throw new ApiException(400, i18n.get("error-falta-tipo-opcion"));
		}

		Environment environment = entityManager.find(Environment.class, uuid);
		if (environment == null) throw new ApiException(404, i18n.get("error-ambiente-uuid") + "'"+ uuid + "'");
		String alias = "";
		if(environment!= null) {
			alias = environment.getAlias();
		}
		

		ZMFDESBLOUSUARIOResponse validarUsuarioYCorreoResponse = desbloqueosSapResource.validarUsuarioYCorreo(systemId,
				environment, username, email);

		if (!"S".contentEquals(validarUsuarioYCorreoResponse.getTIPO())) {
			String message = validarUsuarioYCorreoResponse.getMESSAGE();
			if (validarUsuarioYCorreoResponse.getMESSAGE().isEmpty()) {
				message = "Ocurrió un error en SAP (tipo: <" + validarUsuarioYCorreoResponse.getTIPO()
						+ ">) y no acompañó con mensaje, consulte con el administrador del sistema";
			} else {
				JsonObjectBuilder errorJson = Json.createObjectBuilder() //
						.add("error",
								validarUsuarioYCorreoResponse.getTITULO() != null
										? validarUsuarioYCorreoResponse.getTITULO()
										: "Error") //
						.add("descripcion", message);
				message = errorJson.build().toString();
			}
			throw new ApiException(400, message);
		}

		ZMFDESBLOUSUARIOResponse verificarBajaOBloqueoResponse = null;

		if (operacion == SelfServiceRequestTypeEnum.DESBLOQUEO_DE_USUARIO) {
			// DESBLOQUEAR USUARIOS
			verificarBajaOBloqueoResponse = desbloqueosSapResource
					.verificarBajaOBloqueoPorAdministracionEventoDesbloqueo(systemId, environment, username, email);
			eventoLogica.crearEvento(GeneralConstants.DES_US_SAP + " "+environment.getAlias(), "Se desbloqueo el usuario "+username.toUpperCase(), username.toUpperCase());
		} else {
			// REESTABLECER CONTRASEÑAS
			verificarBajaOBloqueoResponse = desbloqueosSapResource
					.verificarBajaOBloqueoPorAdministracionEventoReseteo(systemId, environment, username, email);
			eventoLogica.crearEvento(GeneralConstants.RES_CONT_SAP + " "+environment.getAlias(), "Se reseteo cont. del usuario "+username.toUpperCase(), username.toUpperCase());
			
		}if (!"S".contentEquals(verificarBajaOBloqueoResponse.getTIPO())) {
			String message = verificarBajaOBloqueoResponse.getMESSAGE();
			if (verificarBajaOBloqueoResponse.getMESSAGE().isEmpty()) {
				message = "Ocurrió un error en SAP (tipo: <" + verificarBajaOBloqueoResponse.getTIPO()
						+ ">) y no acompañó con mensaje, consulte con el administrador del sistema";
			} else {
				JsonObjectBuilder errorJson = Json.createObjectBuilder() //
						.add("error",
								verificarBajaOBloqueoResponse.getTITULO() != null
										? verificarBajaOBloqueoResponse.getTITULO()
										: "Error") //
						.add("descripcion", message);
				message = errorJson.build().toString();
			}
			throw new ApiException(400, message);
		}

		SelfServiceRequestType type = entityManager.find(SelfServiceRequestType.class, operacion.getId());

		SelfServiceRequest r = new SelfServiceRequest();
		r.setUuid(UUID.randomUUID().toString());
		r.setRequestTimestamp(Timestamp.valueOf(LocalDateTime.now()));
		r.setUser(null);
		r.setEnvironment(environment);
		r.setSelfServiceRequestType(type);
		r.setNotes(validarUsuarioYCorreoResponse.getMESSAGE());
		r.setTargetUser(username);
		r.setSystem(systemId);

		r.setClientIp(httpServletRequest.getRemoteAddr());
		r.setConfirmationCode(UUID.randomUUID().toString());
		r.setConfirmationExpirationDate(
				Timestamp.from(LocalDateTime.now().plusMinutes(15).toInstant(OffsetDateTime.now().getOffset())));

		ApiException.validateBean(r);

		// INVALIDAR TODAS LAS PETICIONES ...
		entityManager.createQuery("" //
				+ "UPDATE SelfServiceRequest AS e " //
				+ "SET e.invalidated = TRUE " //
				+ "WHERE e.targetUser = :targetUser " // del usuario objetivo
				+ "AND e.user IS null " // que sean peticiones anonimas
				+ "AND e.fulfillmentDate IS null " // sin completar
				+ "AND e.invalidated = FALSE " // o invalidadas
				+ "", SelfServiceRequest.class) //
				.setParameter("targetUser", username) //
				.executeUpdate();

		entityManager.persist(r);

		System.out.println("Tipo operación: " + operacion);
		System.out.println("Idioma: " + idioma);
		
		if (operacion == SelfServiceRequestTypeEnum.DESBLOQUEO_DE_USUARIO) {
			String linkCta = appProperties.getApiUrl() + "/self-service-requests/" + r.getUuid()
					+ "/confirmation-status?confirmation-code=" + r.getConfirmationCode()+"&idioma="+idioma;

			System.out.println("Enviando correo de desbloqueo");
			Map<String, Object> params = new HashMap<>();
			params.put("${sap-user}", username);
			params.put("${cta-url}", linkCta);
			
			if(idioma.equals("es")) {
			emailResource.enviarTemplate( //
					email, //
					"Desbloqueo de usuario SAP " + alias, //
					"email-templates/email-sap-user-unlock.html", params);
			}
			if(idioma.equals("en")) {
				emailResource.enviarTemplate( //
						email, //
						"SAP user unlock " + alias, //
						"email-templates/email-sap-user-unlock_en.html", params);
			}
		}

		if (operacion == SelfServiceRequestTypeEnum.REESTABLECER_CONTRASEÑA_TEMPORAL) {
			String linkCta = appProperties.getApiUrl() + "/self-service-requests/" + r.getUuid()
					+ "/reset-password-form?confirmation-code=" + r.getConfirmationCode()+"&idioma="+idioma;

			System.out.println("Enviando correo de restablecer contraseña");
			Map<String, Object> params = new HashMap<>();
			params.put("${sap-user}", username);
			params.put("${cta-url}", linkCta);
			if(idioma.equals("es")) {
			emailResource.enviarTemplate( //
					email, //
					"Establecer contraseña temporal de usuario SAP " + alias, //
					"email-templates/email-sap-user-password-reset.html", params);
			}
			if(idioma.equals("en")) {
				emailResource.enviarTemplate( //
						email, //
						"Set temporary password for SAP user " + alias, //
						"email-templates/email-sap-user-password-reset_en.html", params);
			}
		}
		return r;

	}

	public void probarConexionConAmbiente(Environment environment) {
		sessionLogic.obtenerUsuarioAutorizado(PermissionEnum.ENTERPRISE_ADMINISTRATOR);
		desbloqueosSapResource.obtenerClienteParaAmbiente(environment);
	}

}

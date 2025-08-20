package com.autoserviciosap.resources;

import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Base64;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.ejb.Stateless;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Holder;
import jakarta.xml.ws.WebServiceException;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.CXFBusFactory;
import org.apache.cxf.ws.policy.PolicyEngine;
import org.apache.cxf.ws.policy.PolicyEngineImpl;

import com.autoserviciosap.ApiException;
import com.autoserviciosap.dto.SelfServiceRequestTypeEnum;
import com.autoserviciosap.model.Environment;
import com.autoserviciosap.resources.ZWS_DESBLOQUEO_USUARIOS.ZMFDESBLOUSUARIOResponse;
import com.autoserviciosap.resources.ZWS_DESBLOQUEO_USUARIOS.ZWSBloqueoDesbloqueoUsrs;
import com.autoserviciosap.resources.ZWS_DESBLOQUEO_USUARIOS.ZWSBloqueoDesbloqueoUsuarios;

@Stateless
public class DesbloqueosSapResource {

	public ZWSBloqueoDesbloqueoUsuarios obtenerClienteParaAmbiente(Environment environment) {
		try {

			Bus bus = CXFBusFactory.getThreadDefaultBus();
			PolicyEngineImpl engine = (PolicyEngineImpl) bus.getExtension(PolicyEngine.class);
			engine.setEnabled(false);
			engine.setIgnoreUnknownAssertions(true);

			Client client = ClientBuilder.newClient();
			client.property("http.connection.timeout", 10_000L);
			client.property("http.receive.timeout", 10_000L);

			String wsdlLocation = environment.getWsdlLocation();
			wsdlLocation = agregarQueryParamsToString(wsdlLocation, environment);

			WebTarget target = client.target(wsdlLocation);

			Builder request = target.request();
			request.header("Authorization", "Basic " + Base64.getEncoder()
					.encodeToString((environment.getUsername() + ":" + environment.getPassword()).getBytes()));

			Response response = request.get();

			System.out.println(response.getStatus());

			if (response.getStatus() == 401) throw new ApiException(400,
					"La consulta del WSDL arrojó un error de autenticación, verifique las credenciales ingresadas");

			if (response.getStatus() != 200) {
				response.bufferEntity();
				Logger.getAnonymousLogger().log(Level.WARNING, response.readEntity(String.class));

				String message;
				String tail = "no es posible generar el cliente para comunicarse con el ambiente seleccionado.";

				if (response.getStatus() >= 500) {
					message = "Ocurrió un error interno del servidor en SAP al consultar la descripción del servicio (WSDL), "
							+ tail;
				} else {
					message = "Ocurrió un error en SAP (" + response.getStatus()
							+ ") al consultar la descripción del servicio (WSDL), " + tail;
				}

				throw new ApiException(400, message);
			}

			return new ZWSBloqueoDesbloqueoUsuarios(new URL(wsdlLocation));

		} catch (ApiException ex) {
			throw ex;
		} catch (Exception ex) {

			String message = ex.getMessage();

			if (ex instanceof ProcessingException) {
				Throwable cause = ex;
				while ((cause = cause.getCause()) != null) {
					if (cause instanceof SocketTimeoutException) {
						message = "Superó el tiempo de espera al consultar el WSDL";
						break;
					}
				}
			}

			throw new ApiException(500, message);
		}
	}

	private static final Logger LOGGER = Logger.getLogger(DesbloqueosSapResource.class.getName());

	public ZMFDESBLOUSUARIOResponse validarUsuarioYCorreo(String system, Environment environment, String username,
			String email) {
		return dummy(SelfServiceRequestTypeEnum.VALIDAR_EMAIL_Y_USUARIO, system, environment, username, email);
	}

	public ZMFDESBLOUSUARIOResponse verificarBajaOBloqueoPorAdministracionEventoReseteo(String system,
			Environment environment, String username, String email) {
		return dummy(SelfServiceRequestTypeEnum.VALIDAR_RESETEO, system, environment, username, email);
	}
	
	public ZMFDESBLOUSUARIOResponse verificarBajaOBloqueoPorAdministracionEventoDesbloqueo(String system,
			Environment environment, String username, String email) {
		return dummy(SelfServiceRequestTypeEnum.VALIDAR_DESBLOQUEO, system, environment, username, email);
	}
	
	public ZMFDESBLOUSUARIOResponse dummy(SelfServiceRequestTypeEnum enum1, String system,
			Environment environment, String username, String email) {
		
		if (username == null) throw new ApiException(400, "Falta username");
		if (email == null) throw new ApiException(400, "Falta email");
		if (username.length() > 12) throw new ApiException(400,
				"La longitud del username debe ser menor o igual a 12 caracteres");
		if (email.length() > 241) throw new ApiException(400,
				"La longitud del email debe ser menor o igual a 241 caracteres");

		ZWSBloqueoDesbloqueoUsrs ws = obtenerClienteParaAmbiente(environment).getZBDBloqueoDesbloqueoUsuarios();
		agregarQueryParams(ws, environment);

		Holder<String> tipo = new Holder<String>();
		Holder<String> message = new Holder<>();
		Holder<String> password2 = new Holder<>();
		Holder<String> titulo = new Holder<>();

		String opcion = enum1.getOpcion();

		try {
		ws.zmfDESBLOUSUARIO( //
				system, //
				email, //
				null, //
				opcion, //
				null, //
				username, //
				message, password2, tipo, titulo);
		}catch(WebServiceException e) {
			System.out.println("error en endpoint sap");
			e.getStackTrace();
		}

		ZMFDESBLOUSUARIOResponse response = new ZMFDESBLOUSUARIOResponse();
		response.setTIPO(tipo.value);
		response.setMESSAGE(message.value);
		response.setTITULO(titulo.value);

		StringBuilder json = new StringBuilder();
		json.append("{");

		json.append('"').append("email").append("\": ");
		json.append(email != null ? "\"" + email + "\"" : "null").append(", ");

		json.append('"').append("opcion").append("\": ");
		json.append(opcion != null ? "\"" + opcion + "\"" : "null").append(", ");

		json.append('"').append("username").append("\": ");
		json.append(username != null ? "\"" + username + "\"" : "null").append(", ");

		json.append('"').append("message").append("\": ");
		json.append(message.value != null ? "\"" + message.value + "\"" : "null").append(", ");

		json.append('"').append("tipo").append("\": ");
		json.append(tipo.value != null ? "\"" + tipo.value + "\"" : "null");

		json.append("}");

		LOGGER.info(json.toString());

		return response;
	}

	public ZMFDESBLOUSUARIOResponse desbloquearUsuario(String system, Environment environment, String username) {

		if (username == null) throw new ApiException(400, "Falta username");
		if (username.length() > 12) throw new ApiException(400,
				"La longitud del username debe ser menor o igual a 12 caracteres");

		ZWSBloqueoDesbloqueoUsrs service = obtenerClienteParaAmbiente(environment).getZBDBloqueoDesbloqueoUsuarios();
		agregarQueryParams(service, environment);

		Holder<String> tipo = new Holder<String>();
		Holder<String> message = new Holder<>();
		Holder<String> password2 = new Holder<>();
		Holder<String> titulo = new Holder<>();
		System.out.println("Usuario ambiente:"+username);
		service.zmfDESBLOUSUARIO( //
				system, //
				null, //
				null, //
				SelfServiceRequestTypeEnum.DESBLOQUEO_DE_USUARIO.getOpcion(), //
				null, //
				username, //
				message, password2, tipo, titulo);

		ZMFDESBLOUSUARIOResponse response = new ZMFDESBLOUSUARIOResponse();
		response.setTIPO(tipo.value);
		response.setMESSAGE(message.value);
		response.setTITULO(titulo.value);

		return response;
	}

	@Context
	private HttpServletRequest request;

	public ZMFDESBLOUSUARIOResponse establecerContraseñaTemporalParaUsuario(String system, Environment environment,
			String username, String newPassword) {

		if (username == null) throw new ApiException(400, "Falta username");
		if (newPassword == null) throw new ApiException(400, "Falta password");
		if (username.length() > 12) throw new ApiException(400,
				"La longitud del username debe ser menor o igual a 12 caracteres");
		if (newPassword.length() > 40) throw new ApiException(400,
				"La longitud del password debe ser menor o igual a 40 caracteres");

		ZWSBloqueoDesbloqueoUsrs service = obtenerClienteParaAmbiente(environment).getZBDBloqueoDesbloqueoUsuarios();
		agregarQueryParams(service, environment);

		Holder<String> tipo = new Holder<String>();
		Holder<String> message = new Holder<>();
		Holder<String> password2 = new Holder<>();
		Holder<String> titulo = new Holder<>();
		service.zmfDESBLOUSUARIO( //
				system, //
				null, //
				newPassword, //
				SelfServiceRequestTypeEnum.REESTABLECER_CONTRASEÑA_TEMPORAL.getOpcion(), //
				null, //
				username, //
				message, password2, tipo, titulo);

		ZMFDESBLOUSUARIOResponse response = new ZMFDESBLOUSUARIOResponse();
		response.setTIPO(tipo.value);
		response.setMESSAGE(message.value);
		response.setTITULO(titulo.value);

		return response;
	}

	private void agregarQueryParams(Object service, Environment environment) {

		if (service instanceof BindingProvider) {
			BindingProvider b = (BindingProvider) service;
			Map<String, Object> requestContext = b.getRequestContext();
			String endpointUrl = "" + requestContext.get(org.apache.cxf.message.Message.ENDPOINT_ADDRESS);
			endpointUrl = agregarQueryParamsToString(endpointUrl, environment);
			requestContext.put(org.apache.cxf.message.Message.ENDPOINT_ADDRESS, endpointUrl);
		}
	}

	private String agregarQueryParamsToString(String url, Environment environment) {

		String lang = request.getLocale().getLanguage();
		if (lang == null || lang.trim().isEmpty()) lang = "ES";

		if (!url.contains("?")) url += "?";
		else url += "&";
		url += "sap-language=" + lang;

		url += "&sap-user=" + environment.getUsername();
		url += "&sap-password=" + environment.getPassword();

		// PARCHAZO!
		url = url.replace("nsxwspd01.nasoft.com", "172.35.1.7");

		return url;
	}
	
	

}

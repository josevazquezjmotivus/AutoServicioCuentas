package com.autoserviciosap.endpoints;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import com.autoserviciosap.ApiException;
import com.autoserviciosap.AppProperties;
import com.autoserviciosap.logic.EnvironmentsLogic;
import com.autoserviciosap.logic.SessionLogic;
import com.autoserviciosap.logic.TemplatesLogic;
import com.autoserviciosap.logic.UsersLogic;
import com.autoserviciosap.model.Environment;
import com.autoserviciosap.model.User;
import com.autoserviciosap.resources.InternationalizationStateless;

@Path("session")
public class SessionEndpoints {
	
	@Context
	HttpServletRequest request;

	@Inject
	private SessionLogic sessionLogic;

	@Inject
	private UsersLogic usersLogic;

	@Inject
	private EnvironmentsLogic environmentsLogic;

	@Inject
	private AppProperties appProperties;

	@Inject
	private TemplatesLogic templatesLogic;
	
	@Inject 
	InternationalizationStateless i18n;
	
	@SuppressWarnings("null")
	@GET
	@Path("npe")
	public void mo() {
		Integer e = null;
		System.out.print(e.toString());
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response iniciarSesion(JsonObject json) {
		String username = json.getString("username");
		String password = json.getString("password");
		sessionLogic.iniciarSesion(username, password);
		return null;
	}

	@GET
	@Path("user/uuid")
	@Produces(MediaType.TEXT_PLAIN)
	public Response obtenerUuidDeUsuarioAutenticado() {
		sessionLogic.obtenerUuidDeUsuarioAutenticado();
		return Response.ok().build();
	}

	@POST
	@Path("user/new-password")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response reestablecerContraseña(@SuppressWarnings("rawtypes") Map json) {
		String currentPassword = (String) json.get("currentPassword");
		String nextPassword = (String) json.get("nextPassword");
		String nextPasswordCopy = (String) json.get("nextPasswordCopy");
		sessionLogic.reestablecerContraseña(currentPassword, nextPassword, nextPasswordCopy);
		return Response.ok().build();
	}

	@POST
	@Path("user/new-email")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response reestablecerEmail(@SuppressWarnings("rawtypes") Map json) {
		String currentPassword = (String) json.get("currentPassword");
		String nextEmail = (String) json.get("nextEmail");
		String nextEmailCopy = (String) json.get("nextEmailCopy");
		sessionLogic.reestablecerEmail(currentPassword, nextEmail, nextEmailCopy);
		return Response.ok().build();
	}

	@GET
	@Path("user")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerUsuarioAutenticado() {
		User user = sessionLogic.obtenerUsuarioAutenticado(true);
		user.setPassword(null);
		user.setPasswordRecoveryExpirationDate(null);
		if (user.getPermissions() != null) user.getPermissions().forEach(e -> e.setUsers(null));
		if (user.getEnterprises() != null) user.getEnterprises().forEach(e -> e.setUsers(null));
		return Response.ok(user).build();
	}

	@GET
	@Path("user/environments")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerAmbientesParaUsuarioAutenticado() {
		List<Environment> environments = environmentsLogic.obtenerAmbientesParaUsuarioAutenticado();
		environments = environments.stream().map(environment -> {
			Environment fakeEnvironment = new Environment();
			fakeEnvironment.setAlias(environment.getAlias());
			fakeEnvironment.setUuid(environment.getUuid());
			return fakeEnvironment;
		}).collect(Collectors.toList());
		return Response.ok(environments).build();
	}

    // @DELETE
    @POST
    @Path("delete-method")
	public Response cerrarSesion() {
		sessionLogic.cerrarSesion();
		return null;
	}

	@POST
	@Path("password-recovery-requests")
	@Consumes(MediaType.TEXT_PLAIN)
	public Response recuperarContraseña(String email) {
		sessionLogic.enviarCorreoDeRecuperacionDeContraseña(email);
		return Response.ok().build();
	}

	@GET
	@Path("password-recovery/{key}")
	@Produces("text/html;charset=utf-8")
	public Response passwordRecovery(@PathParam("key") String key) {
		return callable(key, null);
	}

	@POST
	@Path("password-recovery/{key}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces("text/html;charset=utf-8")
	public Response passwordRecovery2( //
			@Multipart(value = "password", required = true) //
			String password, //
			@Multipart(value = "password-repeat", required = true) //
			String passwordRepeat, //
			@PathParam("key") String key //
	) {
		try {
			usersLogic.actualizarContraseñaPorRecuperacion(key, password, passwordRepeat);
			String url = appProperties.getPortalUrl();
			String html = "<!DOCTYPE html><html><head><title>Redirect</title></head><body onload='window.location = \""
					+ url + "\"'></body></html>";
			return Response.ok(html).build();
		} catch (ApiException ex) {
			return callable("", "" + ex.getEntity());
		}
	}

	private Response callable(String key, String notes) {
		
		String idioma = i18n.getSessionLanguage();
		if (idioma == null)
			idioma = "es";
		if ("".equals(idioma))
			idioma = "es";

		Map<String, Object> params = new HashMap<>();
		params.put("${display}", notes != null ? "visible" : "none");
		params.put("${notes}", notes);

		String html = "";
		if (idioma.equals("es")) {
			html = templatesLogic.solveTemplate("html-templates/app-password-reset-form.html", params);
		}
		if (idioma.equals("es")) {
			html = templatesLogic.solveTemplate("html-templates/app-password-reset-form_en.html", params);
		}

		return Response.ok(html).build();
	}

}

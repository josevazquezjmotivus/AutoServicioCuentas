package com.autoserviciosap.endpoints;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

import com.autoserviciosap.dto.PermissionEnum;
import com.autoserviciosap.logic.ConfigurationsLogic;
import com.autoserviciosap.logic.SessionLogic;
import com.autoserviciosap.model.Configuration;

@Path("configuration")
public class ConfigurationEndpoints {
	
	@Context
	HttpServletRequest request;
	
	public static final String CODIGO_DE_IDIOMA = "CODIGO_DE_IDIOMA";
	public static final String IDIOMA_INGLES = "en";
	public static final String IDIOMA_ESPAÑOL = "es";

	@Inject
	private ConfigurationsLogic logic;

	@Inject
	private SessionLogic sessionLogic;
	
	@PUT
	@Path("idioma")
	@Produces(MediaType.TEXT_PLAIN)
	public String cambiarIdioma(String rawText) {
		System.out.println("codigo idioma entrada:"+rawText);
		rawText = ("" + rawText).toLowerCase();
		if (IDIOMA_INGLES.equals(rawText) || IDIOMA_ESPAÑOL.equals(rawText)) {
			
			System.out.println("CODIGO DE IDIOMA : " + rawText);
			HttpSession session = request.getSession();
			session.setAttribute(CODIGO_DE_IDIOMA, rawText);
			System.out.println("EL IDIOMA PARA " + session.getId() + " ES " + rawText);
			return "OK";
		} else return "Código de idioma '" + rawText + "' no reconocido";
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Configuration> getConfiguration() {
		sessionLogic.obtenerUsuarioAutorizado(PermissionEnum.USER_ADMINISTRATOR);
		return logic.obtenerConfiguraciones();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void saveConfiguration(List<Configuration> configuration) {
		sessionLogic.obtenerUsuarioAutorizado(PermissionEnum.USER_ADMINISTRATOR);
		logic.guardarConfiguracion(configuration);
	}

	@GET
	@Path("recaptchaEnabled")
	@Produces(MediaType.TEXT_PLAIN)
	public String getRecaptchaEnabledConfiguration() {
		Configuration e = logic.obtenerConfiguracion("recaptchaEnabled");
		if (e == null) return null;
		return e.getValue();
	}

	@GET
	@Path("recaptchaSiteKey")
	@Produces(MediaType.TEXT_PLAIN)
	public String getRecaptchaSiteKeyConfiguration() {
		Configuration e = logic.obtenerConfiguracion("recaptchaSiteKey");
		if (e == null) return null;
		return e.getValue();
	}
	
	@GET
	@Path("activeDirectoryEnabled")
	@Produces(MediaType.TEXT_PLAIN)
	public String getActiveDirectoryEnabledConfiguration() {
		Configuration e = logic.obtenerConfiguracion("activeDirectoryEnabled");
		if (e == null) return null;
		return e.getValue();
	}
}
package com.autoserviciosap.endpoints;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.autoserviciosap.resources.EmailResource;
import com.autoserviciosap.resources.InternationalizationStateless;

@Path("util")
public class UtilEndpoints {
	
	@Context
	HttpServletRequest request;

	@Inject
	private EmailResource emailResource;
	
	@Inject 
	InternationalizationStateless i18n;


	@GET
	@Path("probar-email")
	@Produces(MediaType.TEXT_PLAIN)
	public Response probarEnvioMail() {

		Map<String, Object> params = new HashMap<>();
		params.put("${sap-user}", "ANGLOBAL_EXPRESS");
		params.put("${portal-url}", "url");
		params.put("${api-url}", "api url");
		params.put("${soporte-operacion}", "soporte");
		
		emailResource.enviarTemplate("jose.vazquezj@agilethought.com", "prueba",
				"email-templates/email-successful-unlock-confirmation.html", params);
		
		return Response.ok().build();
	}


}

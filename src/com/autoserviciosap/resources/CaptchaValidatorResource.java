package com.autoserviciosap.resources;

import java.io.StringReader;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.autoserviciosap.ApiException;
import com.autoserviciosap.logic.ConfigurationsLogic;
import com.autoserviciosap.model.Configuration;

@Stateless
public class CaptchaValidatorResource {

	private static final String URL = "https://www.google.com/recaptcha/api/siteverify";
	
	@Inject
	private ConfigurationsLogic configurationsLogic;

	public void validateCaptchaIfEnabled(String captchaResponse) {
		if (configurationsLogic.seRequiereRecaptcha()) {
			Configuration secretConfiguration = configurationsLogic.obtenerConfiguracion("recaptchaSecretKey");
			if (secretConfiguration == null) throw new ApiException(500,
					"Falta configurar 'recaptchaSecretKey', contacte al administrador");
			String secret = secretConfiguration.getValue();
			boolean validate = validate(secret, captchaResponse);
			if (validate == false) {
				throw new ApiException(403, "No se pudo validar su token de reCAPTCHA");
			}
		}
	}
	
	private boolean validate(String secret, String response) {

		String webResourceUri = URL + "?secret=" + secret + "&response=" + response;

		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(webResourceUri);
		Response r = webTarget.request().get();

		String responseText = r.readEntity(String.class);

		// PARSEAR RESPUESTA
		try (StringReader sr = new StringReader(responseText)) {
			JsonReader reader = Json.createReader(sr);
			JsonObject personObject = reader.readObject();
			boolean s = personObject.getBoolean("success");
			return s;
		}

	}

}

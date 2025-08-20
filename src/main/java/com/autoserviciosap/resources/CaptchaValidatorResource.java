package com.autoserviciosap.resources;

import java.io.StringReader;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

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

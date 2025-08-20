package com.autoserviciosap.resources;

import java.util.Locale;
import java.util.ResourceBundle;

import jakarta.ejb.Stateless;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.Context;

import com.autoserviciosap.endpoints.ConfigurationEndpoints;

@Stateless
public class InternationalizationStateless {

	@Context
	HttpServletRequest request;

	public String get(String id) {
		System.out.println("GET : " + id);
		return get(getSessionLanguage(), id);
	}

	public String getSessionLanguage() {

		if (request != null) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				Object idioma = session.getAttribute(ConfigurationEndpoints.CODIGO_DE_IDIOMA);
				System.out.println("Idioma:"+idioma);
				if (idioma != null) return "" + idioma;
			}
		}

		System.out.println("No tiene idioma en sesion");
		return ConfigurationEndpoints.IDIOMA_ESPAÑOL;
		
	}
	
	public String get(String lang, String id) {
		Locale locale = null;
		
		if (lang == null)
			lang = "es";

		System.out.println("GET " + id + " IN " + lang);

		if(lang.equals("es")) {
			locale = new Locale("es","MX");
		}
		if(lang.equals("en")) {
			locale = new Locale("en","US");
		}

		ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
		String message = bundle.getString(id);

		return message;
	}
	
}

package com.autoserviciosap.logic;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import com.autoserviciosap.ApiException;
import com.autoserviciosap.AppProperties;

@Stateless
public class TemplatesLogic {

	@Inject
	private AppProperties properties;

	public static File ASSETS_FOLDER = new File("assets");

	private String getStyles() {

		File templateFile = new File(ASSETS_FOLDER, "template-styles.css");

		if (!templateFile.exists()) throw new ApiException(500,
				"No se encontró el archivo de estilos de plantilla en la dirección <" + templateFile.getAbsolutePath()
						+ ">");

		try (InputStream stream = new FileInputStream(templateFile)) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int b;
			while ((b = stream.read()) >= 0)
				baos.write(b);
			return new String(baos.toByteArray(), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
			throw new ApiException(500, "Ocurrió un error al leer la plantilla; " + e.getMessage());
		}
	}

	private String getTemplate(String templateName) {

		String style = "<style>" + getStyles() + "</style>\r\n";

		File templateFile = new File(ASSETS_FOLDER, templateName);

		if (!templateFile.exists()) throw new ApiException(500,
				"No se encontró el archivo plantilla en la dirección <" + templateFile.getAbsolutePath() + ">");

		try (InputStream stream = new FileInputStream(templateFile)) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int b;
			while ((b = stream.read()) >= 0)
				baos.write(b);
			return style + new String(baos.toByteArray(), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
			throw new ApiException(500, "Ocurrió un error al leer la plantilla; " + e.getMessage());
		}
	}

	public String solveTemplate(String templateName, Map<String, Object> customParams) {

		String template = getTemplate(templateName);

		Map<String, Object> params = new HashMap<>();
		params.put("${portal-url}", properties.getPortalUrl());
		params.put("${api-url}", properties.getApiUrl());
		params.put("${soporte-operacion}", properties.getSoporteOperacion());

		for (Entry<String, Object> entry : customParams.entrySet())
			params.put(entry.getKey(), entry.getValue());

		for (Entry<String, Object> entry : params.entrySet()) {
			String entryKey = entry.getKey();
			String value = "" + entry.getValue();
			template = template.replace(entryKey, value);
		}

		return template;
	}
}

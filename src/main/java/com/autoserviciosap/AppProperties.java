package com.autoserviciosap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Startup
@Singleton
public class AppProperties {

	private String apiUrl;
	private String portalUrl;

	private Properties properties;

	@PostConstruct
	public void setup() {

		File propertiesFile = new File("autoserviciosap.properties");

		System.out.println("buscando archivo de propiedades en " + propertiesFile.getAbsolutePath());

		try (FileInputStream fis = new FileInputStream(propertiesFile)) {
			properties = new Properties();
			properties.load(fis);
			apiUrl = properties.getProperty("apiUrl");
			portalUrl = properties.getProperty("portalUrl");
		} catch (IOException e) {
			e.printStackTrace();
			throw new ApiException(500, "No se encontró el archivo de propiedades " + propertiesFile.getName() + " en <"
					+ propertiesFile.getAbsolutePath() + ">");
		}

		if (apiUrl == null || apiUrl.isEmpty()) throw new ApiException(500, "Falta propiedad apiUrl en archivo "
				+ propertiesFile.getName()
				+ ", asegurese que el apiUrl contenga el protocolo (http:// o https://) y que no termine en una diagonal");

		if (portalUrl == null || portalUrl.isEmpty()) throw new ApiException(500,
				"Falta propiedad portalUrl en archivo " + propertiesFile.getName());
	}

	public String getApiUrl() {
		return apiUrl;
	}

	public String getPortalUrl() {
		return portalUrl;
	}

	public String getSoporteOperacion() {
		return "" + properties.get("soporteoperacion");
	}
}

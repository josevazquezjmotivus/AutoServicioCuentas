package com.autoserviciosap.logic;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.autoserviciosap.ApiException;
import com.autoserviciosap.model.Configuration;

@Stateless
public class ConfigurationsLogic {

	@PersistenceContext(unitName = "AutoServicioSAP")
	private EntityManager entityManager;

	public List<Configuration> obtenerConfiguraciones() throws ApiException {
		return entityManager.createQuery("SELECT e FROM Configuration AS e", Configuration.class).getResultList();
	}

	public void guardarConfiguracion(List<Configuration> configuration) {
		for (Configuration e : configuration) {
			entityManager.merge(e);
		}
	}
	
	public Configuration obtenerConfiguracion(String id) {
		return entityManager.find(Configuration.class, id);
	}

	public boolean seRequiereRecaptcha() {
		Configuration e = obtenerConfiguracion("recaptchaEnabled");
		if (e == null) return false;
		return "true".equalsIgnoreCase(e.getValue());
	}

}

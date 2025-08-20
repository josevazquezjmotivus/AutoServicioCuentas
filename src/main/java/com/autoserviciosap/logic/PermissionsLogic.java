package com.autoserviciosap.logic;

import java.util.List;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.autoserviciosap.ApiException;
import com.autoserviciosap.model.Permission;

@Stateless
public class PermissionsLogic {

	@PersistenceContext(unitName = "AutoServicioSAP")
	private EntityManager entityManager;

	public List<Permission> obtenerPemisos() throws ApiException {
		return entityManager.createQuery("SELECT p FROM Permission AS p", Permission.class).getResultList();
	}

}

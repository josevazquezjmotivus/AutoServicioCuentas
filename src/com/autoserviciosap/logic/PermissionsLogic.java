package com.autoserviciosap.logic;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

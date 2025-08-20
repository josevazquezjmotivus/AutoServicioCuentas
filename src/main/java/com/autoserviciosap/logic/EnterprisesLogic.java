package com.autoserviciosap.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.autoserviciosap.ApiException;
import com.autoserviciosap.dto.PermissionEnum;
import com.autoserviciosap.model.Enterprise;
import com.autoserviciosap.model.Environment;
import com.autoserviciosap.model.SelfServiceRequest;
import com.autoserviciosap.model.User;

@Stateless
public class EnterprisesLogic {

	@PersistenceContext(unitName = "AutoServicioSAP")
	private EntityManager entityManager;

	@Inject
	private SessionLogic sessionLogic;

	private void fixParaEvitarUsuariosDuplicados(Enterprise xxe) {
		List<User> users = xxe.getUsers();
		if (users == null) return;
		if (users.size() == 1) return;
		List<User> permissionsUnique = users //
				.stream() //
				.collect(Collectors.groupingBy(u -> u.getUuid())) //
				.values().stream() //
				.map(l -> l.get(0)) //
				.collect(Collectors.toList());
		xxe.setUsers(permissionsUnique);
	}

	public List<Enterprise> obtenerEmpresas() throws ApiException {
		sessionLogic.obtenerUsuarioAutorizado(PermissionEnum.ENTERPRISE_ADMINISTRATOR);
		return entityManager.createQuery("SELECT e FROM Enterprise AS e", Enterprise.class).getResultList();
	}

	public Enterprise obtenerEmpresa(String uuid) throws ApiException {
		sessionLogic.obtenerUsuarioAutorizado(PermissionEnum.ENTERPRISE_ADMINISTRATOR);
		List<Enterprise> e = entityManager.createQuery("" //
				+ "SELECT e " //
				+ "FROM Enterprise AS e " //
				+ "LEFT JOIN FETCH e.environments " //
				+ "LEFT JOIN FETCH e.activeDirectoryEnvironments " //
				+ "LEFT JOIN FETCH e.users " //
				+ "WHERE e.uuid = :uuid", //
				Enterprise.class).setParameter("uuid", uuid).getResultList();
		if (e.isEmpty()) throw new ApiException(404, "No se encontró empresa con uuid \"" + uuid + "\"");
		return e.get(0);
	}

	public Enterprise crearEmpresa(Enterprise e) throws ApiException {
		sessionLogic.obtenerUsuarioAutorizado(PermissionEnum.ENTERPRISE_ADMINISTRATOR);
		e.setUuid(UUID.randomUUID().toString());
		ApiException.validateBean(e);
		fixParaEvitarUsuariosDuplicados(e);
		entityManager.merge(e);
		return e;
	}

	public Enterprise actualizarEmpresa(String uuid, Enterprise e) throws ApiException {

		sessionLogic.obtenerUsuarioAutorizado(PermissionEnum.ENTERPRISE_ADMINISTRATOR);
		e.setUuid(uuid);
		if (e.getUsers() != null) for (User u : e.getUsers()) {
			if (u.getEnterprises() == null) u.setEnterprises(new ArrayList<>());
			if (u.getEnterprises().stream().filter(x -> x.getUuid().equals(uuid)).count() == 0) u.getEnterprises()
					.add(e);
		}

		ApiException.validateBean(e);
		obtenerEmpresa(uuid);
		fixParaEvitarUsuariosDuplicados(e);
		return entityManager.merge(e);
	}

	public void eliminarEmpresa(String uuid) throws ApiException {
		sessionLogic.obtenerUsuarioAutorizado(PermissionEnum.ENTERPRISE_ADMINISTRATOR);

		Enterprise e = obtenerEmpresa(uuid);

		for (Environment environment : e.getEnvironments())
			for (SelfServiceRequest selfServiceRequest : environment.getRequests())
				entityManager.remove(selfServiceRequest);
		entityManager.flush();

		for (Environment environment : e.getEnvironments())
			entityManager.remove(environment);
		entityManager.flush();

		entityManager.remove(e);
	}

	public void eliminarEmpresas(List<String> uuids) {
		for (String uuid : uuids) {
			eliminarEmpresa(uuid);
		}
	}

}

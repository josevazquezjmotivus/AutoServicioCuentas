package com.autoserviciosap.endpoints;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.autoserviciosap.logic.PermissionsLogic;
import com.autoserviciosap.model.Permission;

@Path("permissions")
public class PermissionsEndpoints {

	@Inject
	private PermissionsLogic permissionsLogic;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerPermisos() {
		List<Permission> permisos = permissionsLogic.obtenerPemisos();
		permisos.forEach(e -> e.setUsers(null));
		return Response.ok(permisos).build();
	}

}

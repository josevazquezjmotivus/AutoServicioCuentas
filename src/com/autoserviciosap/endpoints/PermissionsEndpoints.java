package com.autoserviciosap.endpoints;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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

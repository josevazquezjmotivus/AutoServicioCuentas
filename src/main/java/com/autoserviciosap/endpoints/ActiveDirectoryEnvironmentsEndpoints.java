package com.autoserviciosap.endpoints;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.autoserviciosap.logic.ActiveDirectoryEnvironmentLogic;
import com.autoserviciosap.model.ActiveDirectoryEnvironment;

@Path("active-directory-environments")
public class ActiveDirectoryEnvironmentsEndpoints {

	@Inject
	private ActiveDirectoryEnvironmentLogic logic;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ActiveDirectoryEnvironment> getActiveDirectoryDomains() {
		List<ActiveDirectoryEnvironment> list = logic.obtenerAmbientesDeActiveDirectoryDeTodasLasEmpresas();
		for (ActiveDirectoryEnvironment e : list)
			e.setDomains(null);
		return list;
	}

	@GET
	@Path("{uuid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getActiveDirectoryEnvironment(@PathParam("uuid") String uuid) {
		ActiveDirectoryEnvironment e = logic.obtenerAmbienteDeActiveDirectory(uuid);
		clean(e);
		return Response.ok(e).build();
	}

	// @PUT
	// @Path("{uuid}")
	@POST
	@Path("{uuid}/put-method")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response actualizarAmbiente(@PathParam("uuid") String uuid, ActiveDirectoryEnvironment e) {
		ActiveDirectoryEnvironment f = logic.actualizarAmbienteDeActiveDirectory(uuid, e);
		clean(f);
		return Response.ok(f).build();
	}

	// @DELETE
	// @Path("{uuid}")
	@POST
	@Path("{uuid}/delete-method")
	public Response eliminarDominio(@PathParam("uuid") String uuid) {
		logic.eliminarDominio(uuid);
		return null;
	}

	@POST
	@Path("{uuid}/self-service-requests")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public void procesarPeticionDeAutoservicio(@PathParam("uuid") String uuid, @QueryParam("captcha") String captcha,
			String email) {
		logic.desbloquearUsuarioPorEmail(uuid, email, captcha);
	}

	private ActiveDirectoryEnvironment clean(ActiveDirectoryEnvironment e) {
		e.setEnterprise(null);
		if (e.getDomains() != null) e.getDomains().forEach(f -> f.setEnvironment(null));
		return e;
	}
}

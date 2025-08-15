package com.autoserviciosap.endpoints;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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

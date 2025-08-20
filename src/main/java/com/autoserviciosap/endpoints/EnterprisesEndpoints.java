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
import com.autoserviciosap.logic.EnterprisesLogic;
import com.autoserviciosap.logic.EnvironmentsLogic;
import com.autoserviciosap.model.ActiveDirectoryDomain;
import com.autoserviciosap.model.ActiveDirectoryEnvironment;
import com.autoserviciosap.model.Enterprise;
import com.autoserviciosap.model.Environment;

@Path("enterprises")
public class EnterprisesEndpoints {

	@Inject
	private EnterprisesLogic enterprisesLogic;

	@Inject
	private EnvironmentsLogic environmentsLogic;
	
	@Inject
	private ActiveDirectoryEnvironmentLogic activeDirectoryEnvironmentLogic;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerEmpresas() {
		List<Enterprise> list = enterprisesLogic.obtenerEmpresas();
		list.forEach(this::prepareForSerialization);
		return Response.ok(list).build();
	}

    // @DELETE
    @POST
    @Path("delete-method")
	@Produces(MediaType.APPLICATION_JSON)
	public Response eliminarEmpresas(@QueryParam("uuid") List<String> uuids) {
		enterprisesLogic.eliminarEmpresas(uuids);
		return Response.ok().build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response crearEmpresa(Enterprise e) {
		Enterprise f = enterprisesLogic.crearEmpresa(e);
		prepareForSerialization(f);
		return Response.ok(f).build();
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerEmpresa(@PathParam("id") String uuid) {
		Enterprise e = enterprisesLogic.obtenerEmpresa(uuid);
		prepareForSerialization(e);
		return Response.ok(e).build();
	}

	//@PUT
    //@Path("{id}")
    @POST
	@Path("{id}/put-method")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response actualizarEmpresa(@PathParam("id") String id, Enterprise e) {
		Enterprise f = enterprisesLogic.actualizarEmpresa(id, e);
		prepareForSerialization(f);
		return Response.ok(f).build();
	}

	// @DELETE
    // @Path("{id}")
    @POST
    @Path("{id}/delete-method")
	public Response eliminarEmpresa(@PathParam("id") String id) {
		enterprisesLogic.eliminarEmpresa(id);
		return null;
	}

	@POST
	@Path("{id}/environments")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response crearEntornoDeEmpresa(@PathParam("id") String id, Environment e) {
		Environment f = environmentsLogic.crearEntornoDeEmpresa(id, e);
		f.setEnterprise(null);
		return Response.ok(f).build();
	}
	
	@POST
	@Path("{id}/active-directory-environments")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ActiveDirectoryEnvironment createActiveDirectoryDomain(@PathParam("id") String id, ActiveDirectoryEnvironment e) {
		ActiveDirectoryEnvironment f = activeDirectoryEnvironmentLogic.createActiveDirectoryEnvironment(id, e);
		prepareForSerialization(f);
		return f;
	}

	private void prepareForSerialization(ActiveDirectoryEnvironment f) {
		if (f.getDomains() != null) {
			for (ActiveDirectoryDomain domains : f.getDomains()) {
				prepareForSerialization(domains);
			}
		}
		if (f.getEnterprise() != null) {
			prepareForSerialization(f.getEnterprise());
		}
	}

	private void prepareForSerialization(ActiveDirectoryDomain e) {
		e.setEnvironment(null);
	}

	private void prepareForSerialization(Enterprise e) {
		if (e.getEnvironments() != null) e.getEnvironments().forEach(f -> f.setEnterprise(null));
		if (e.getActiveDirectoryDomains() != null) e.getActiveDirectoryDomains().forEach(f -> f.setEnterprise(null));
		if (e.getUsers() != null) e.getUsers().forEach(u -> {
			u.setEnterprises(null);
			u.setEmail(null);
			u.setPassword(null);
			u.setPermissions(null);
		});
	}
}

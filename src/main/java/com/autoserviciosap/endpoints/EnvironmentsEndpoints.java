package com.autoserviciosap.endpoints;

import java.util.List;
import java.util.stream.Collectors;

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

import com.autoserviciosap.dto.SelfServiceRequestTypeEnum;
import com.autoserviciosap.dto.SelfServiceUserOperationRequestDto;
import com.autoserviciosap.logic.EnvironmentsLogic;
import com.autoserviciosap.model.Environment;
import com.autoserviciosap.model.SelfServiceRequest;
import com.autoserviciosap.model.SelfServiceRequestType;
import com.autoserviciosap.model.User;

@Path("environments")
public class EnvironmentsEndpoints {

	@Inject
	private EnvironmentsLogic enviornmentLogic;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerAmbientes() {

		List<Environment> environments = enviornmentLogic.obtenerAmbientes();

		environments = environments.stream().map(e -> {
			Environment dto = new Environment();
			dto.setAlias(e.getAlias());
			// dto.setEnterprise(e.get);
			dto.setPassword(e.getPassword());
			dto.setUsername(e.getUsername());
			dto.setUuid(e.getUuid());
			dto.setWsdlLocation(e.getWsdlLocation());
			dto.setSystemsEnabled(e.isSystemsEnabled());
			dto.setSystems(e.getSystems());
			return dto;
		}).collect(Collectors.toList());

		return Response.ok(environments).build();
	}

	@GET
	@Path("{uuid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerAmbiente(@PathParam("uuid") String uuid) {
		Environment e = enviornmentLogic.obtenerAmbiente(uuid);
		clean(e);
		return Response.ok(e).build();
	}

	private void clean(Environment env) {
		if (env.getEnterprise() != null) env.getEnterprise().setEnvironments(null);
		if (env.getRequests() != null) env.getRequests().forEach(e -> e.setEnvironment(null));
	}

	//@PUT
    //@Path("{uuid}")
    @POST
    @Path("{uuid}/put-method")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response actualizarAmbiente(@PathParam("uuid") String uuid, Environment e) {
		Environment f = enviornmentLogic.actualizarAmbiente(uuid, e);
		return Response.ok(f).build();
	}

	// @DELETE
    // @Path("{uuid}")
    @POST
	@Path("{uuid}/delete-method")
	public Response eliminarAmbiente(@PathParam("uuid") String uuid) {
		enviornmentLogic.eliminarAmbiente(uuid);
		return null;
	}

	@GET
	@Path("{uuid}/self-service-requests")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerPeticionesDeAutoServicioDeEntorno(@PathParam("uuid") String uuid) {
		List<SelfServiceRequest> list = enviornmentLogic.obtenerPeticionesDeAutoServicioDeEntorno(uuid);
		list.forEach(e -> {
			e.setEnvironment(null);
			SelfServiceRequestType type = e.getSelfServiceRequestType();
			if (type != null) {
				type.setDescription(null);
			}
			User user = e.getUser();
			if (user != null) {
				user.setEmail(null);
				user.setPassword(null);
				user.setUsername(null);
				user.setPermissions(null);
			}
		});
		return Response.ok(list).build();
	}

	@POST
	@Path("{uuid}/unlock-user-requests")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Response debloquearUsuario( //
			@PathParam("uuid") String uuid, //
			@QueryParam("system") String systemId, //
			String usuario) {
		SelfServiceRequest result = enviornmentLogic.procesarPeticion(systemId, uuid, usuario,
				SelfServiceRequestTypeEnum.DESBLOQUEO_DE_USUARIO);
		prepareForReturn(result);
		return Response.ok(result).build();
	}

	private void prepareForReturn(SelfServiceRequest result) {

		Environment environment = result.getEnvironment();
		Environment fakeEnvironment = new Environment();
		fakeEnvironment.setAlias(environment.getAlias());
		fakeEnvironment.setUuid(environment.getUuid());
		result.setEnvironment(fakeEnvironment);

		User user = result.getUser();
		User fakeUser = new User();
		fakeUser.setUsername(user.getUsername());
		fakeUser.setUuid(user.getUuid());
		result.setUser(fakeUser);

	}

	@POST
	@Path("{uuid}/reset-password-requests")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Response reestablecerContraseña( //
			@PathParam("uuid") String uuid, //
			@QueryParam("system") String systemId, //
			String usuario) {
		SelfServiceRequest result = enviornmentLogic.procesarPeticion(systemId, uuid, usuario,
				SelfServiceRequestTypeEnum.REESTABLECER_CONTRASEÑA_TEMPORAL);
		prepareForReturn(result);
		return Response.ok(result).build();
	}

	@POST
	@Path("jco-destination-requests")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void probarConexionConAmbiente(Environment environment) {
		enviornmentLogic.probarConexionConAmbiente(environment);
	}

	@POST
	@Path("{uuid}/self-service-user-operation-requests")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public SelfServiceRequest procesarPeticionAutoServicio( //
			@PathParam("uuid") String uuid, //
			SelfServiceUserOperationRequestDto dto //
	) {
		SelfServiceRequest ssr = enviornmentLogic.procesarPeticionAutoServicio( //
				dto.getSystemId(), //
				uuid, //
				dto.getUsername(), //
				dto.getEmail(), //
				dto.getRecaptchaVerifyToken(), //
				dto.getType());
		ssr.setEnvironment(null);
		ssr.setSelfServiceRequestType(null);
		return ssr;
	}

}

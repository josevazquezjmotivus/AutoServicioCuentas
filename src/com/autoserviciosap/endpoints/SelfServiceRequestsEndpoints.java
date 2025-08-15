package com.autoserviciosap.endpoints;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import com.autoserviciosap.logic.SelfServiceRequestsLogic;

@Path("self-service-requests")
public class SelfServiceRequestsEndpoints {

	@Inject
	private SelfServiceRequestsLogic logic;

	@GET
	@Path("{uuid}/confirmation-status")
	@Produces(MediaType.TEXT_HTML + "; charset=utf-8")
	public String confirmarSolicitud( //
			@PathParam("uuid") String uuid, //
			@QueryParam("confirmation-code") String confirmationCode,
			@QueryParam("idioma") String idioma//
	) {
		System.out.println("Confirmacion status");
		return logic.confirmarSolicitud(uuid, confirmationCode,idioma);
	}

	@GET
	@Path("{uuid}/reset-password-form")
	@Produces(MediaType.TEXT_HTML + "; charset=utf-8")
	public String obtenerFormularioParaEstablecerContraseñaTemporal( //
			@PathParam("uuid") String uuid, //
			@QueryParam("confirmation-code") String confirmationCode,
			@QueryParam("idioma") String idioma) {
		return logic.obtenerFormularioParaEstablecerContraseñaTemporal(uuid, confirmationCode, null,idioma);
	}

	@POST
	@Path("{uuid}/reset-password-form")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_HTML + "; charset=utf-8")
	public String procesarFormularioReestablecerPassword( //
			@PathParam("uuid") String uuid, //
			@QueryParam("confirmation-code") String confirmationCode, //
			@Multipart("password") String password, //
			@Multipart("password-repeat") String passwordRepeat,
			@QueryParam("idioma") String idioma) {
		return logic.reestablecerPassword(uuid, confirmationCode, password, passwordRepeat,idioma);
	}
}

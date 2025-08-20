package com.autoserviciosap.endpoints;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

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

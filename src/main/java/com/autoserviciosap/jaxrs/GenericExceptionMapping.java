package com.autoserviciosap.jaxrs;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapping implements ExceptionMapper<Exception> {

	@Override
	public Response toResponse(Exception e) {
		e.printStackTrace();
		return Response.status(500).entity("Error interno del servidor").type("text/plain").build();
	}

}

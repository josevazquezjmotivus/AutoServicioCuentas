package com.autoserviciosap.jaxrs;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.autoserviciosap.ApiException;

@Provider
public class RestApiExceptionMapping implements ExceptionMapper<ApiException> {

	@Override
	public Response toResponse(ApiException e) {
		System.out.println(e.getEntity());
		return Response.status(e.getStatus()).entity(e.getEntity()).type(e.getMediaType()+"; charset=utf8").build();
	}

}

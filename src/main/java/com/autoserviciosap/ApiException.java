package com.autoserviciosap;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jakarta.ejb.ApplicationException;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonWriter;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.ws.rs.core.MediaType;

@ApplicationException(rollback = true)
public class ApiException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private Map<String, String> validationErrors;

	private int status;
	private Object entity;
	private String mediaType;

	public ApiException(int status, Object entity) {
		super("(" + status + ") : " + entity);
		this.status = status;
		this.entity = entity;
		this.mediaType = MediaType.TEXT_PLAIN;
	}

	public ApiException(Map<String, String> validationErrors) {
		this(409, getEntity(validationErrors));
		this.mediaType = MediaType.APPLICATION_JSON;
		this.validationErrors = validationErrors;
	}

	public ApiException(Set<ConstraintViolation<Object>> validationErrors) {
		this(toMap(validationErrors));
	}

	public static Map<String, String> toMap(Set<ConstraintViolation<Object>> validationErrors) {
		Map<String, String> validationErrorsMap = new HashMap<>();
		validationErrors.forEach(e -> validationErrorsMap.put(e.getPropertyPath().toString(), e.getMessage()));
		return validationErrorsMap;
	}

	private static Object getEntity(Map<String, String> validationErrors) {
		JsonObjectBuilder errors = Json.createObjectBuilder();
		validationErrors.forEach((key, val) -> errors.add(key, val));
		JsonObjectBuilder root = Json.createObjectBuilder();
		root.add("error", "Error de validación");
		root.add("descripcion", "El recurso no puede ser procesado porqué tiene errores de validación");
		root.add("errores", errors);
		JsonObject json = root.build();
		StringWriter writer = new StringWriter();
		JsonWriter jsonWriter = Json.createWriter(writer);
		jsonWriter.writeObject(json);
		return writer.toString();
	}

	public Map<String, String> getValidationErrors() {
		return validationErrors;
	}

	public static void validateBean(Object bean) throws ApiException {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<Object>> validationErrors = validator.validate(bean);
		if (!validationErrors.isEmpty()) throw new ApiException(validationErrors);
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Object getEntity() {
		return entity;
	}

	public void setEntity(Object entity) {
		this.entity = entity;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public void setValidationErrors(Map<String, String> validationErrors) {
		this.validationErrors = validationErrors;
	}

}

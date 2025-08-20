package com.autoserviciosap.model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.List;


/**
 * The persistent class for the self_service_request_type database table.
 * 
 */
@Entity
@Table(name="self_service_request_type")
@NamedQuery(name="SelfServiceRequestType.findAll", query="SELECT s FROM SelfServiceRequestType s")
public class SelfServiceRequestType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private String description;

	private String name;

	//bi-directional many-to-one association to SelfServiceRequest
	@OneToMany(mappedBy="selfServiceRequestType")
	private List<SelfServiceRequest> requests;

	public SelfServiceRequestType() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<SelfServiceRequest> getRequests() {
		return this.requests;
	}

	public void setRequests(List<SelfServiceRequest> requests) {
		this.requests = requests;
	}

	public SelfServiceRequest addRequest(SelfServiceRequest request) {
		getRequests().add(request);
		request.setSelfServiceRequestType(this);

		return request;
	}

	public SelfServiceRequest removeRequest(SelfServiceRequest request) {
		getRequests().remove(request);
		request.setSelfServiceRequestType(null);

		return request;
	}

}
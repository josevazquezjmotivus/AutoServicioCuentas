package com.autoserviciosap.model;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * The persistent class for the self_service_request database table.
 * 
 */
@Entity
@Table(name = "self_service_request")
@NamedQuery(name = "SelfServiceRequest.findAll", query = "SELECT s FROM SelfServiceRequest s")
public class SelfServiceRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@NotNull
	@Size(max = 40)
	private String uuid;

	@Column(name = "request_timestamp")
	private Timestamp requestTimestamp;

	@Column(name = "target_user")
	@NotNull
	@Size(max = 255)
	private String targetUser;

	@NotNull
	@Size(max = 255)
	private String notes;

	@Column(name = "client_ip")
	@NotNull
	@Size(max = 45)
	private String clientIp;

	@Column(name = "confirmation_code")
	@Size(max = 40)
	private String confirmationCode;

	@Column(name = "confirmation_expiration_date")
	private Timestamp confirmationExpirationDate;

	@Column(name = "fulfillment_date")
	private Timestamp fulfillmentDate;

	private boolean invalidated;

	// bi-directional many-to-one association to SelfServiceRequestType
	@ManyToOne
	@JoinColumn(name = "self_service_request_type_id")
	private SelfServiceRequestType selfServiceRequestType;

	// bi-directional many-to-one association to User
	@ManyToOne
	private User user;

	// bi-directional many-to-one association to User
	@ManyToOne
	private Environment environment;

	private String system;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Timestamp getRequestTimestamp() {
		return requestTimestamp;
	}

	public void setRequestTimestamp(Timestamp timestamp) {
		this.requestTimestamp = timestamp;
	}

	public String getTargetUser() {
		return targetUser;
	}

	public void setTargetUser(String targetUser) {
		this.targetUser = targetUser;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getConfirmationCode() {
		return confirmationCode;
	}

	public void setConfirmationCode(String confirmationCode) {
		this.confirmationCode = confirmationCode;
	}

	public Timestamp getConfirmationExpirationDate() {
		return confirmationExpirationDate;
	}

	public void setConfirmationExpirationDate(Timestamp confirmationExpirationDate) {
		this.confirmationExpirationDate = confirmationExpirationDate;
	}

	public Timestamp getFulfillmentDate() {
		return fulfillmentDate;
	}

	public void setFulfillmentDate(Timestamp fulfillmentDate) {
		this.fulfillmentDate = fulfillmentDate;
	}

	public SelfServiceRequestType getSelfServiceRequestType() {
		return selfServiceRequestType;
	}

	public void setSelfServiceRequestType(SelfServiceRequestType selfServiceRequestType) {
		this.selfServiceRequestType = selfServiceRequestType;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public boolean isInvalidated() {
		return invalidated;
	}

	public void setInvalidated(boolean invalidated) {
		this.invalidated = invalidated;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

}
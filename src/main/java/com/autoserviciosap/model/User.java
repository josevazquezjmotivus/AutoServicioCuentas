package com.autoserviciosap.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * The persistent class for the user database table.
 * 
 */
@Entity
@NamedQuery(name = "User.findAll", query = "SELECT u FROM User u")
@Access(value = AccessType.FIELD)
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@NotNull
	@Size(max = 255)
	@Column(name = "uuid")
	private String uuid;

	@NotNull
	@Size(max = 255, min = 1)
	private String email;

	@NotNull
	@Size(max = 255, min = 1)
	private String name;

	@NotNull
	@Size(max = 64)
	private String password;

	@NotNull
	@Size(max = 255, min = 1)
	private String username;

	// bi-directional many-to-many association to Enterprise
	@ManyToMany(mappedBy = "users")
	private List<Enterprise> enterprises;

	// bi-directional many-to-one association to SelfServiceRequest
	@OneToMany(mappedBy = "user")
	private List<SelfServiceRequest> requests;

	// bi-directional many-to-many association to Permission
	@ManyToMany
	@JoinTable( //
			name = "user_has_permission", //
			joinColumns = { @JoinColumn(name = "user_uuid") }, //
			inverseJoinColumns = { @JoinColumn(name = "permission_id") }) //
	private List<Permission> permissions;

	@Size(max = 255)
	@Column(name = "password_recovery_key")
	private String passwordRecoveryKey;

	@Column(name = "password_recovery_expiration_date")
	private Timestamp passwordRecoveryExpirationDate;

	public User() {
	}

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<Enterprise> getEnterprises() {
		return this.enterprises;
	}

	public void setEnterprises(List<Enterprise> enterprises) {
		this.enterprises = enterprises;
	}

	public List<SelfServiceRequest> getRequests() {
		return this.requests;
	}

	public void setRequests(List<SelfServiceRequest> requests) {
		this.requests = requests;
	}

	public SelfServiceRequest addRequest(SelfServiceRequest request) {
		getRequests().add(request);
		request.setUser(this);

		return request;
	}

	public SelfServiceRequest removeRequest(SelfServiceRequest request) {
		getRequests().remove(request);
		request.setUser(null);

		return request;
	}

	public List<Permission> getPermissions() {
		return this.permissions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

	public String getPasswordRecoveryKey() {
		return passwordRecoveryKey;
	}

	public void setPasswordRecoveryKey(String passwordRecoveryKey) {
		this.passwordRecoveryKey = passwordRecoveryKey;
	}

	public Timestamp getPasswordRecoveryExpirationDate() {
		return passwordRecoveryExpirationDate;
	}

	public void setPasswordRecoveryExpirationDate(Timestamp passwordRecoveryExpirationDate) {
		this.passwordRecoveryExpirationDate = passwordRecoveryExpirationDate;
	}

}
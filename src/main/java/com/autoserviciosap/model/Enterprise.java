package com.autoserviciosap.model;

import java.io.Serializable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * The persistent class for the enterprise database table.
 * 
 */
@Entity
@NamedQuery(name = "Enterprise.findAll", query = "SELECT e FROM Enterprise e")
public class Enterprise implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String uuid;

	@NotNull
	@Size(max = 255)
	private String name;

	// bi-directional many-to-many association to User
	@ManyToMany
	@JoinTable(//
			name = "enterprise_has_user", //
			joinColumns = { @JoinColumn(name = "enterprise_uuid") }, //
			inverseJoinColumns = { @JoinColumn(name = "user_uuid") })
	private List<User> users;

	// bi-directional many-to-one association to Environment
	@OneToMany(mappedBy = "enterprise", orphanRemoval = true)
	private List<Environment> environments;
	
	@OneToMany(mappedBy = "enterprise", orphanRemoval = true)
	private List<ActiveDirectoryEnvironment> activeDirectoryEnvironments;

	public Enterprise() {
	}

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<User> getUsers() {
		return this.users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<Environment> getEnvironments() {
		return this.environments;
	}

	public void setEnvironments(List<Environment> environments) {
		this.environments = environments;
	}

	public Environment addEnvironment(Environment environment) {
		getEnvironments().add(environment);
		environment.setEnterprise(this);

		return environment;
	}

	public Environment removeEnvironment(Environment environment) {
		getEnvironments().remove(environment);
		environment.setEnterprise(null);

		return environment;
	}

	public List<ActiveDirectoryEnvironment> getActiveDirectoryDomains() {
		return activeDirectoryEnvironments;
	}

	public void setActiveDirectoryDomains(List<ActiveDirectoryEnvironment> activeDirectoryDomains) {
		this.activeDirectoryEnvironments = activeDirectoryDomains;
	}
}
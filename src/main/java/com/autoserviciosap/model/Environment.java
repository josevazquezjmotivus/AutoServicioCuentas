package com.autoserviciosap.model;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * The persistent class for the environment database table.
 * 
 */
@Entity
@NamedQuery(name = "Environment.findAll", query = "SELECT e FROM Environment e")
public class Environment implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String uuid;

	@NotNull
	@Size(max = 255)
	private String alias;

	@Column(name = "wsdl_location")
	@NotNull
	@Size(max = 1024)
	private String wsdlLocation;

	@Column(name = "password")
	@NotNull
	@Size(max = 255)
	private String password;

	@Column(name = "username")
	@NotNull
	@Size(max = 255)
	private String username;

	@Column(name = "systems_enabled")
	private boolean systemsEnabled;
	
	@Column(name = "system_visible")
	private boolean systemVisible;

	// bi-directional many-to-one association to Enterprise
	@ManyToOne
	private Enterprise enterprise;

	// bi-directional many-to-one association to SelfServiceRequest
	@OneToMany(mappedBy = "environment", orphanRemoval = true)
	private List<SelfServiceRequest> requests;

	@ElementCollection // 1
	@CollectionTable(name = "system", joinColumns = @JoinColumn(name = "environment")) // 2
	@Column(name = "id") // 3
	private List<String> systems;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getWsdlLocation() {
		return wsdlLocation;
	}

	public void setWsdlLocation(String wsdlLocation) {
		this.wsdlLocation = wsdlLocation;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String clientPassword) {
		this.password = clientPassword;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String clientUsername) {
		this.username = clientUsername;
	}

	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}

	public List<SelfServiceRequest> getRequests() {
		return requests;
	}

	public void setRequests(List<SelfServiceRequest> requests) {
		this.requests = requests;
	}

	public boolean isSystemVisible() {
		return systemVisible;
	}

	public void setSystemVisible(boolean systemVisible) {
		this.systemVisible = systemVisible;
	}
	
	public boolean isSystemsEnabled() {
		return systemsEnabled;
	}

	public void setSystemsEnabled(boolean systemsEnabled) {
		this.systemsEnabled = systemsEnabled;
	}

	public List<String> getSystems() {
		return systems;
	}

	public void setSystems(List<String> systems) {
		this.systems = systems;
	}

}
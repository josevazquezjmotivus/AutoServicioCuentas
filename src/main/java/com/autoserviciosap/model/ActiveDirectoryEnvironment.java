package com.autoserviciosap.model;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Table(name = "active_directory_environment")
@Entity
public class ActiveDirectoryEnvironment implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String uuid;

	@NotNull
	@Size(max = 255, min = 1)
	private String alias;

	// bi-directional many-to-one association to Enterprise
	@NotNull
	@ManyToOne
	@JoinColumn(name = "enterprise")
	private Enterprise enterprise;

	@Valid
	@OneToMany(mappedBy = "environment", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<ActiveDirectoryDomain> domains;

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

	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}

	public List<ActiveDirectoryDomain> getDomains() {
		return domains;
	}

	public void setDomains(List<ActiveDirectoryDomain> domains) {
		this.domains = domains;
	}

}

package com.autoserviciosap.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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

package com.autoserviciosap.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Table(name = "active_directory_domain")
@Entity
public class ActiveDirectoryDomain implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String uuid;

	@NotNull
	@Size(max = 1024)
	private String url;

	@Column(name = "bind_pw")
	@NotNull
	@Size(max = 255)
	private String bindPw;

	@Column(name = "bind_dn")
	@NotNull
	@Size(max = 255)
	private String bindDn;

	@Column(name = "search_base")
	@NotNull
	@Size(max = 255)
	private String searchBase;
	
	@Column(name = "search_filter")
	@NotNull
	@Size(max = 255)
	private String searchFilter;

	// bi-directional many-to-one association to Enterprise
	@NotNull
	@ManyToOne
	@JoinColumn(name = "environment")
	private ActiveDirectoryEnvironment environment;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBindPw() {
		return bindPw;
	}

	public void setBindPw(String bindPw) {
		this.bindPw = bindPw;
	}

	public String getBindDn() {
		return bindDn;
	}

	public void setBindDn(String bindDn) {
		this.bindDn = bindDn;
	}

	public String getSearchBase() {
		return searchBase;
	}

	public void setSearchBase(String searchBase) {
		this.searchBase = searchBase;
	}

	public ActiveDirectoryEnvironment getEnvironment() {
		return environment;
	}

	public void setEnvironment(ActiveDirectoryEnvironment environment) {
		this.environment = environment;
	}

	public String getSearchFilter() {
		return searchFilter;
	}

	public void setSearchFilter(String searchFilter) {
		this.searchFilter = searchFilter;
	}

}

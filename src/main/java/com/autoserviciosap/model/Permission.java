package com.autoserviciosap.model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.List;

/**
 * The persistent class for the permission database table.
 * 
 */
@Entity
@NamedQuery(name="Permission.findAll", query="SELECT p FROM Permission p")
public class Permission implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private String description;

	private String name;

	//bi-directional many-to-many association to User
	@ManyToMany(mappedBy="permissions")
	private List<User> users;

	public Permission() {
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

	public List<User> getUsers() {
		return this.users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

}
package com.autoserviciosap.model;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Configuration implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    private String id;

    private String value;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
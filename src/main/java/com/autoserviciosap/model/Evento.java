package com.autoserviciosap.model;

import java.io.Serializable;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.validation.constraints.NotNull;

/**
 * The persistent class for the user database table.
 * 
 */
@Entity
@NamedQuery(name = "Evento.findAll", query = "SELECT e FROM Evento e")
@Access(value = AccessType.FIELD)
public class Evento implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@NotNull
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@NotNull
	private String fecha;

	@NotNull
	private String evento;

	@NotNull
	private String texto;

	@NotNull
	private String usuario;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getEvento() {
		return evento;
	}

	public void setEvento(String evento) {
		this.evento = evento;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	

}
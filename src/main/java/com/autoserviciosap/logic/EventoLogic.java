package com.autoserviciosap.logic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.autoserviciosap.ApiException;
import com.autoserviciosap.model.Evento;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class EventoLogic {
	
	@PersistenceContext(unitName = "AutoServicioSAP")
	private EntityManager entityManager;
	
	public Evento crearEvento(String evento, String texto, String usuario) throws ApiException {
		var now = LocalDateTime.now();
		var dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		var dateFormatD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		var e = new Evento();
		e.setEvento(evento);
		e.setFecha(now.format(dateFormatD));
		e.setTexto(texto + " ,el " + now.format(dateFormat));
		e.setUsuario(usuario);
		entityManager.persist(e);
		return e;
	}
	
	public List<Evento> obtenerReporteEventos(String f1,String f2,String tipo) throws ApiException {
		return entityManager.createQuery("SELECT e FROM Evento AS e WHERE e.fecha BETWEEN :fechai AND :fechaf AND e.evento LIKE :evento", Evento.class)
				.setParameter("fechai", f1).setParameter("fechaf", f2).setParameter("evento", tipo).getResultList();
	}

}

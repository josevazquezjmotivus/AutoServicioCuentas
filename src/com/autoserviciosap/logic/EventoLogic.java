package com.autoserviciosap.logic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.autoserviciosap.ApiException;
import com.autoserviciosap.model.Evento;

@Stateless
public class EventoLogic {
	
	@PersistenceContext(unitName = "AutoServicioSAP")
	private EntityManager entityManager;
	
	public Evento crearEvento(String evento,String texto,String usuario)throws ApiException{
		DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
		DateFormat dateFormatD = new SimpleDateFormat("YYYY-MM-dd");
		Date date = new Date();

		Evento e = new Evento();
		e.setEvento(evento);
		e.setFecha(dateFormatD.format(date));
		e.setTexto(texto+ " ,el " + dateFormat.format(date));
		e.setUsuario(usuario);
		entityManager.persist(e);
		return e;
	}
	
	public List<Evento> obtenerReporteEventos(String f1,String f2,String tipo) throws ApiException {
		return entityManager.createQuery("SELECT e FROM Evento AS e WHERE e.fecha BETWEEN :fechai AND :fechaf AND e.evento LIKE :evento", Evento.class)
				.setParameter("fechai", f1).setParameter("fechaf", f2).setParameter("evento", tipo).getResultList();
	}

}

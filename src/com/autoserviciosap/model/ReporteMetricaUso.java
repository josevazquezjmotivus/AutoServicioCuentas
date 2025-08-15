package com.autoserviciosap.model;

import java.util.List;

public class ReporteMetricaUso {
	
	private List<Evento> eventos;
	private int totalEventos;
	private int totalEventosDesSAP;
	private int totalEventosDesAD;
	private int totalEventosResSAP;
	public List<Evento> getEventos() {
		return eventos;
	}
	public void setEventos(List<Evento> eventos) {
		this.eventos = eventos;
	}
	public int getTotalEventos() {
		return totalEventos;
	}
	public void setTotalEventos(int totalEventos) {
		this.totalEventos = totalEventos;
	}
	public int getTotalEventosDesSAP() {
		return totalEventosDesSAP;
	}
	public void setTotalEventosDesSAP(int totalEventosDesSAP) {
		this.totalEventosDesSAP = totalEventosDesSAP;
	}
	public int getTotalEventosDesAD() {
		return totalEventosDesAD;
	}
	public void setTotalEventosDesAD(int totalEventosDesAD) {
		this.totalEventosDesAD = totalEventosDesAD;
	}
	public int getTotalEventosResSAP() {
		return totalEventosResSAP;
	}
	public void setTotalEventosResSAP(int totalEventosResSAP) {
		this.totalEventosResSAP = totalEventosResSAP;
	}

}

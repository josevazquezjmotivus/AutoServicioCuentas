package com.autoserviciosap.model;

import java.util.List;

public record ReporteMetricaUso(
	List<Evento> eventos,
	int totalEventos,
	int totalEventosDesSAP,
	int totalEventosDesAD,
	int totalEventosResSAP
) {}

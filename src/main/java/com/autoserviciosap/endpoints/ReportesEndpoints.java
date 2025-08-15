package com.autoserviciosap.endpoints;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.autoserviciosap.logic.EventoLogic;
import com.autoserviciosap.logic.GeneralConstants;
import com.autoserviciosap.logic.GenerarXLS;
import com.autoserviciosap.model.Evento;
import com.autoserviciosap.model.ReporteMetricaUso;

@Path("reportes")
public class ReportesEndpoints {
	
	@Context
	private HttpServletResponse response;

	@Inject
	private EventoLogic eventoLogic;

	@GET
	@Path("obtener-metrica-uso")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerEventos(@QueryParam("fechai") String fechai, @QueryParam("fechaf") String fechaf,
			@QueryParam("tipo") String tipo) {

		ReporteMetricaUso reporte = new ReporteMetricaUso();

		System.out.println("Fechai:" + fechai);
		System.out.println("Fechaf:" + fechaf);
		System.out.println("Tipo:" + tipo);

		List<Evento> eventos = null;

		if (tipo.equals("")) {
			tipo = "%%";
		}else {
			tipo = "%"+tipo+"%";
		}
//			Date f1 = new SimpleDateFormat("yyyy-MM-dd").parse(fechai);
//			Date f2 = new SimpleDateFormat("yyyy-MM-dd").parse(fechaf);
		eventos = eventoLogic.obtenerReporteEventos(fechai, fechaf, tipo);

		reporte.setEventos(eventos);
		reporte.setTotalEventos(eventos.size());
		int totalEventosDesSAP = 0;
		int totalEventosResSAP = 0;
		int totalEventosDesAD = 0;
		
		for (Evento evento : eventos) {
			if (evento.getEvento().contains(GeneralConstants.DES_US_AD)) {
				totalEventosDesAD++;
			}
			if (evento.getEvento().contains(GeneralConstants.DES_US_SAP)) {
				totalEventosDesSAP++;
			}
			if (evento.getEvento().contains(GeneralConstants.RES_CONT_SAP)) {
				totalEventosResSAP++;
			}
		}
		reporte.setTotalEventosDesSAP(totalEventosDesSAP);
		reporte.setTotalEventosResSAP(totalEventosResSAP);
		reporte.setTotalEventosDesAD(totalEventosDesAD);

		return Response.ok(reporte).build();
	}

	@POST
	@Path("carga-evento")
	@Produces(MediaType.APPLICATION_JSON)
	public Response insertarEvento() {
		Evento evento = eventoLogic.crearEvento(GeneralConstants.DES_US_AD, "Carga", "joser");
		return Response.ok(evento).build();
	}
	
	@GET
	@Path("obtener-metrica-uso-excel")
	@Produces("application/vnd.ms-excel")
	public byte[] obtenerEventosExcel(@QueryParam("fechai") String fechai, @QueryParam("fechaf") String fechaf,
			@QueryParam("tipo") String tipo) {
		
		//response.setHeader("content-disposition", "attachment; filename='Reporte Totalizador.xls'");

		System.out.println("Fechai:" + fechai);
		System.out.println("Fechaf:" + fechaf);
		System.out.println("Tipo:" + tipo);

		List<Evento> eventos = null;
		
		if("TODOS".equals(tipo)) {
			tipo = "";
		}

		if (tipo.equals("")) {
			tipo = "%%";
		}else {
			tipo = "%"+tipo+"%";
		}
		eventos = eventoLogic.obtenerReporteEventos(fechai, fechaf, tipo);

		System.out.println("eventos:"+eventos.size());
		byte[] archivo = GenerarXLS.generarXML(eventos);

		return archivo;
	}

}

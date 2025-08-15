package com.autoserviciosap.logic;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.autoserviciosap.model.Evento;

public class GenerarXLS {

	Workbook wb;
	ByteArrayOutputStream fileOut;
	CellStyle styleNormal;
	CellStyle styleNegritas;
	CellStyle styleTitulo;
	CellStyle styleSumatorias;
	Font font;

	public GenerarXLS(String nombreArchivo) {
		try {
			wb = new HSSFWorkbook();
			// File archivo = new File(nombreArchivo + ".xls");
			// System.out.println("ruta de archivo :" +
			// archivo.getAbsolutePath());
			fileOut = new ByteArrayOutputStream();
			// declarar estilos

			// Estilo normal
			styleNormal = wb.createCellStyle();
			font = wb.createFont();
			font.setFontHeightInPoints((short) 8);
			font.setFontName("Calibri");
			font.setItalic(false);
			font.setBold(false);
			styleNormal.setFont(font);

			// Estilo normal negritas
			styleNegritas = wb.createCellStyle();
			font = wb.createFont();
			font.setFontHeightInPoints((short) 8);
			font.setFontName("Calibri");
			font.setItalic(false);
			font.setBold(true);
			styleNegritas.setFont(font);

			// Estilo titulo
			styleTitulo = wb.createCellStyle();
			font = null;
			font = wb.createFont();
			font.setFontHeightInPoints((short) 9);
			font.setFontName("Calibri");
			font.setItalic(false);
			font.setBold(true);
			styleTitulo.setFont(font);

			// Estilo sumatorias
			styleSumatorias = wb.createCellStyle();
			font = null;
			font = wb.createFont();
			font.setFontHeightInPoints((short) 8);
			font.setFontName("Calibri");
			font.setItalic(false);
			font.setBold(true);
			styleSumatorias.setFont(font);

		} catch (Exception e) {
			System.out.println("Error " + e);
		}
	}

	public Sheet crearHoja(String nombre) {
		Sheet sheet = wb.createSheet(nombre);
		return sheet;
	}

	public Row crearRenglon(Sheet sheet, int renglon) {
		Row row = sheet.createRow((short) renglon);
		return row;
	}

	public void pintarCelda(Row row, String valor, int celda, boolean tipoTitulo) {
		CellStyle style;
		if (!tipoTitulo) {
			style = styleNormal;
		} else {
			style = styleTitulo;
		}

		Cell cell = row.createCell(celda);
		cell.setCellValue(valor);
		cell.setCellStyle(style);
	}

	public void pintarCeldaNegritas(Row row, String valor, int celda, boolean tipoTitulo) {
		CellStyle style = styleNegritas;
		Cell cell = row.createCell(celda);
		cell.setCellValue(valor);
		cell.setCellStyle(style);
	}

	public void pintarCeldaNumero(Row row, String valor, int celda, boolean styleTitulo) {
		CellStyle style;
		if (!styleTitulo) {
			style = styleNormal;
		} else {
			style = styleSumatorias;
		}

		Cell cell = row.createCell(celda);
		try {
			cell.setCellValue(Double.parseDouble(valor));
		} catch (NumberFormatException e) {
			cell.setCellValue("");
		}
		style.setDataFormat((short) 7);
		cell.setCellStyle(style);
	}

	public void fusionarCeldas(Sheet sheet, int primerRenglon, int ultimoRenglon, int primerCelda, int ultimaCelda) {
		sheet.addMergedRegion(new CellRangeAddress(primerRenglon, // first row
																	// (0-based)
				ultimoRenglon, // last row (0-based)
				primerCelda, // first column (0-based)
				ultimaCelda // last column (0-based)
		));
	}

	public byte[] generarArchivo() {
		try {
			// escribir y cerrar el archivo
			wb.write(fileOut);
			fileOut.close();
			System.out.println("Se generó el archivo de xls");
			return fileOut.toByteArray();

		} catch (Exception e) {
			System.out.println("Error no se pudo generar el archivo " + e);
			return null;
		}
	}
	
	public void pintarLineaBlanca(Sheet sheet, GenerarXLS archivo, Row renglon) {
		// pintar titulos de tabla
		archivo.pintarCelda(renglon, "", 0, true);
		archivo.pintarCelda(renglon, "", 1, true);
		archivo.pintarCelda(renglon, "", 2, true);
		archivo.pintarCelda(renglon, "", 3, true);
		
	}

	
	public void pintarTotales(Sheet sheet, List<Evento> eventos, Row renglon) {
		
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
		
		pintarCelda(renglon,"Total de desbloqueos de SAP: "+totalEventosDesSAP 
				+ " / Total de restableciones en SAP: "+totalEventosResSAP 
				+ " / Total de desbloqueos AD: "+totalEventosDesAD ,0,false);
		
	}

	public void pintarTitulos(Sheet sheet, GenerarXLS archivo, Row renglon) {
		// pintar titulos de tabla
		archivo.pintarCelda(renglon, "Fecha", 0, true);
		archivo.pintarCelda(renglon, "Ambiente", 1, true);
		archivo.pintarCelda(renglon, "Evento", 2, true);
		archivo.pintarCelda(renglon, "Texto", 3, true);
		archivo.pintarCelda(renglon, "Usuario", 4, true);

		sheet.setColumnWidth(0, 70 * 37);
		sheet.setColumnWidth(1, 80 * 37);
		sheet.setColumnWidth(2, 300 * 37);
		sheet.setColumnWidth(3, 350 * 37);
		sheet.setColumnWidth(4, 80 * 37);
		
	}

	public void pintarDatos(Sheet sheet, List<Evento> eventos) {

		llenarDatos(sheet, eventos);
	}

	private void llenarDatos(Sheet sheet, List<Evento> eventos) {
		int contadorReng = 4;
		String ambiente = "";
		String ev = "";
		for (Evento evento : eventos) {
			Row renglon = crearRenglon(sheet, contadorReng);
			pintarCelda(renglon,evento.getFecha(),0,false);
			ev = evento.getEvento();
			if (ev.contains(GeneralConstants.DES_US_AD)) {
				ambiente = "AD";
			}
			if (ev.contains(GeneralConstants.DES_US_SAP)) {
				ambiente = ev.replaceAll(GeneralConstants.DES_US_SAP + " ", "");
			}
			if (ev.contains(GeneralConstants.RES_CONT_SAP)) {
				ambiente =ev.replaceAll(GeneralConstants.RES_CONT_SAP+" ", "");
			}
			pintarCelda(renglon,ambiente,1,false);
			pintarCelda(renglon,ev,2,false);
			pintarCelda(renglon,evento.getTexto(),3,false);
			pintarCelda(renglon,evento.getUsuario(),4,false);
			contadorReng++;
		}

	}
	
	public static byte[] generarXML(List<Evento> eventos) {
		
		GenerarXLS crearExcel = new GenerarXLS("Reporte metricas de uso");
		Sheet sheet = crearExcel.crearHoja("Reporte metricas de uso");
	
		Row renglon0 = crearExcel.crearRenglon(sheet, 0);
		crearExcel.pintarCelda(renglon0, "REPORTE METRICAS DE USO", 0, true);
		crearExcel.fusionarCeldas(sheet, 0, 0, 0, 3);
		
		Row renglon1 = crearExcel.crearRenglon(sheet, 1);
		crearExcel.pintarTotales(sheet, eventos,renglon1);
	
		Row renglon3 = crearExcel.crearRenglon(sheet, 3);
		crearExcel.pintarTitulos(sheet, crearExcel, renglon3);
		
		
		crearExcel.fusionarCeldas(sheet, 1, 1, 0, 3);
		
		
		crearExcel.pintarDatos(sheet, eventos);
		return crearExcel.generarArchivo();
	
	}

}

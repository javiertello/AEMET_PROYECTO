package aemetproyecto;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.axis.AxisFault;
import org.apache.commons.io.IOUtils;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * Clase que se encarga de generar la página HTML con el contenido de la
 * predicción con JDOM.
 * 
 * @author Javier Tello Alquézar
 *
 */
public class GenerarPaginaTiempo {
	
	/**
	 * Dado una lista de Dias pasada como parámetro, genera una página HTML que
	 * contiene una tabla con la información de la predicción meteorológica. Se 
	 * serializa como una cadena de bytes, para enviarlo a través de la red
	 * 
	 * @param lista La lista de Dias con la informacion
	 * @return Cadena de bytes que contiene la tabla HTML
	 * @throws AxisFault En el caso de saltar excepción
	 */
	public byte[] generar(List<? extends Dia> lista) throws AxisFault{
		
		// Ahora construyo el HTML a partir
		// de los objetos creados en "lista"
		DiaCompleto dc = (DiaCompleto) lista.get(0);
		DiaSemi ds = (DiaSemi) lista.get(1);
		DiaSemi ds2 = (DiaSemi) lista.get(2);

		Element root = new Element("table");

		root.setAttribute("class", "table table-condensed table-bordered text-center");
		
		
		String fecha1 = new SimpleDateFormat("EEE dd").format(dc.getFecha());
		String fecha2 = new SimpleDateFormat("EEE dd").format(ds.getFecha());
		String fecha3 = new SimpleDateFormat("EEE dd").format(ds2.getFecha());

		
		Element tr = new Element("tr");
		Element th = new Element("th");
		th.setAttribute("rowspan", "2");
		th.setAttribute("class", "fondoa");
		th.setText("Fecha");
		tr.addContent(th);
		th = new Element("th");
		th.setAttribute("colspan", "3");
		th.setAttribute("class", " fondoa text-center");
		th.setText(fecha1);
		tr.addContent(th);
		th = new Element("th");
		th.setAttribute("colspan", "2");
		th.setAttribute("class", " fondoa text-center");
		th.setText(fecha2);
		tr.addContent(th);
		th = new Element("th");
		th.setAttribute("colspan", "2");
		th.setAttribute("class", "fondoa text-center");
		th.setText(fecha3);
		tr.addContent(th);
		
		root.addContent(tr);
		
		
		tr = new Element("tr");
		th = new Element("th");
		th.setAttribute("class", "fondoac");
		th.setText("6-12");
		tr.addContent(th);
		th = new Element("th");
		th.setAttribute("class", "fondoac");
		th.setText("12-18");
		tr.addContent(th);
		th = new Element("th");
		th.setAttribute("class", "fondoac");
		th.setText("18-24");
		tr.addContent(th);
		th = new Element("th");
		th.setAttribute("class", "fondoac");
		th.setText("0-12");
		tr.addContent(th);
		th = new Element("th");
		th.setAttribute("class", "fondoac");
		th.setText("12-24");
		tr.addContent(th);
		th = new Element("th");
		th.setAttribute("class", "fondoac");
		th.setText("0-12");
		tr.addContent(th);
		th = new Element("th");
		th.setAttribute("class", "fondoac");
		th.setText("12-24");
		tr.addContent(th);
		root.addContent(tr);
		
		// Cielo
		tr = new Element("tr");
		th = new Element("th");
		th.setText("Cielo");
		tr.addContent(th);
		Element td = new Element("td");
		Element img = new Element("img");
		img.setAttribute("src", "http://www.aemet.es/imagenes/gif/estado_cielo/"+dc.getEstado_cielo_1()+".gif");
		td.addContent(img);
		tr.addContent(td);
		td = new Element("td");
		img = new Element("img");
		img.setAttribute("src", "http://www.aemet.es/imagenes/gif/estado_cielo/"+dc.getEstado_cielo_2()+".gif");
		td.addContent(img);
		tr.addContent(td);
		td = new Element("td");
		img = new Element("img");
		img.setAttribute("src", "http://www.aemet.es/imagenes/gif/estado_cielo/"+dc.getEstado_cielo_3()+".gif");
		td.addContent(img);
		tr.addContent(td);
		td = new Element("td");
		img = new Element("img");
		img.setAttribute("src", "http://www.aemet.es/imagenes/gif/estado_cielo/"+ds.getEstado_cielo_1()+".gif");
		td.addContent(img);
		tr.addContent(td);
		td = new Element("td");
		img = new Element("img");
		img.setAttribute("src", "http://www.aemet.es/imagenes/gif/estado_cielo/"+ds.getEstado_cielo_2()+".gif");
		td.addContent(img);
		tr.addContent(td);
		td = new Element("td");
		img = new Element("img");
		img.setAttribute("src", "http://www.aemet.es/imagenes/gif/estado_cielo/"+ds2.getEstado_cielo_1()+".gif");
		td.addContent(img);
		tr.addContent(td);
		td = new Element("td");
		img = new Element("img");
		img.setAttribute("src", "http://www.aemet.es/imagenes/gif/estado_cielo/"+ds2.getEstado_cielo_2()+".gif");
		td.addContent(img);
		tr.addContent(td);
		
		root.addContent(tr);
		
		// Prob. precip.
		tr = new Element("tr");
		th = new Element("th");
		th.setText("Precip.");
		tr.addContent(th);
		td = new Element("td");
		
		if(dc.getProb_precip_1()<0)
			td.setText("-");
		else
			td.setText(dc.getProb_precip_1()+"%");
		
		tr.addContent(td);
		td = new Element("td");
		
		if(dc.getProb_precip_2()<0)
			td.setText("-");
		else
			td.setText(dc.getProb_precip_2()+"%");
		
		tr.addContent(td);
		td = new Element("td");
		
		if(dc.getProb_precip_3()<0)
			td.setText("-");
		else
			td.setText(dc.getProb_precip_3()+"%");
		
		tr.addContent(td);
		td = new Element("td");
		
		if(ds.getProb_precip_1()<0)
			td.setText("-");
		else
			td.setText(ds.getProb_precip_1()+"%");
		
		tr.addContent(td);
		td = new Element("td");
		td.setText(ds.getProb_precip_2()+"%");
		tr.addContent(td);
		td = new Element("td");
		td.setText(ds2.getProb_precip_1()+"%");
		tr.addContent(td);
		td = new Element("td");
		td.setText(ds2.getProb_precip_2()+"%");
		tr.addContent(td);
		
		root.addContent(tr);
		
		//Cota nieve prov.
		tr = new Element("tr");
		th = new Element("th");
		th.setText("Cota");
		tr.addContent(th);
		td = new Element("td");
		td.setText(dc.getCota_nieve_1());
		tr.addContent(td);
		td = new Element("td");
		td.setText(dc.getCota_nieve_2());
		tr.addContent(td);
		td = new Element("td");
		td.setText(dc.getCota_nieve_3());
		tr.addContent(td);
		td = new Element("td");
		td.setText(ds.getCota_nieve_1());
		tr.addContent(td);
		td = new Element("td");
		td.setText(ds.getCota_nieve_2());
		tr.addContent(td);
		td = new Element("td");
		td.setText(ds2.getCota_nieve_1());
		tr.addContent(td);
		td = new Element("td");
		td.setText(ds2.getCota_nieve_2());
		tr.addContent(td);
		
		root.addContent(tr);
		
		// Temperaturas
		tr = new Element("tr");
		th = new Element("th");
		th.setText("Temp.");
		tr.addContent(th);
		td = new Element("td");
		td.setAttribute("colspan","3");
		td.setText(dc.getTempMinima()+" / "+dc.getTempMaxima());
		tr.addContent(td);
		td = new Element("td");
		td.setAttribute("colspan","2");
		td.setText(ds.getTempMinima()+" / "+ds.getTempMaxima());
		tr.addContent(td);
		td = new Element("td");
		td.setAttribute("colspan","2");
		td.setText(ds2.getTempMinima()+" / "+ds2.getTempMaxima());
		tr.addContent(td);
		
		root.addContent(tr);
		
		// Viento
		tr = new Element("tr");
		th = new Element("th");
		th.setText("Viento");
		tr.addContent(th);
		td = new Element("td");
		img = new Element("img");
		img.setAttribute("src", "http://www.aemet.es/imagenes/gif/iconos_viento/"+dc.getViento_1().getDireccion()+".gif");
		td.addContent(img);
		tr.addContent(td);
		td = new Element("td");
		img = new Element("img");
		img.setAttribute("src", "http://www.aemet.es/imagenes/gif/iconos_viento/"+dc.getViento_2().getDireccion()+".gif");
		td.addContent(img);
		tr.addContent(td);
		td = new Element("td");
		img = new Element("img");
		img.setAttribute("src", "http://www.aemet.es/imagenes/gif/iconos_viento/"+dc.getViento_3().getDireccion()+".gif");
		td.addContent(img);
		tr.addContent(td);
		td = new Element("td");
		img = new Element("img");
		img.setAttribute("src", "http://www.aemet.es/imagenes/gif/iconos_viento/"+ds.getViento_1().getDireccion()+".gif");
		td.addContent(img);
		tr.addContent(td);
		td = new Element("td");
		img = new Element("img");
		img.setAttribute("src", "http://www.aemet.es/imagenes/gif/iconos_viento/"+ds.getViento_2().getDireccion()+".gif");
		td.addContent(img);
		tr.addContent(td);
		td = new Element("td");
		img = new Element("img");
		img.setAttribute("src", "http://www.aemet.es/imagenes/gif/iconos_viento/"+ds2.getViento_1().getDireccion()+".gif");
		td.addContent(img);
		tr.addContent(td);
		td = new Element("td");
		img = new Element("img");
		img.setAttribute("src", "http://www.aemet.es/imagenes/gif/iconos_viento/"+ds2.getViento_2().getDireccion()+".gif");
		td.addContent(img);
		tr.addContent(td);
		
		root.addContent(tr);
		
		// Velocidad viento
		tr = new Element("tr");
		th = new Element("th");
		th.setText("(Km/h)");
		tr.addContent(th);
		td = new Element("td");
		td.setText(dc.getViento_1().getVelocidad()+"");
		tr.addContent(td);
		td = new Element("td");
		td.setText(dc.getViento_2().getVelocidad()+"");
		tr.addContent(td);
		td = new Element("td");
		td.setText(dc.getViento_3().getVelocidad()+"");
		tr.addContent(td);
		td = new Element("td");
		td.setText(ds.getViento_1().getVelocidad()+"");
		tr.addContent(td);
		td = new Element("td");
		td.setText(ds.getViento_2().getVelocidad()+"");
		tr.addContent(td);
		td = new Element("td");
		td.setText(ds2.getViento_1().getVelocidad()+"");
		tr.addContent(td);
		td = new Element("td");
		td.setText(ds2.getViento_2().getVelocidad()+"");
		tr.addContent(td);
		
		root.addContent(tr);
		
		// Indice UV
		String uv = "";
		tr = new Element("tr");
		th = new Element("th");
		th.setText("UV");
		tr.addContent(th);
		td = new Element("td");
		td.setAttribute("colspan","3");
		uv = (dc.getIndiceUV() < 0) ?"":dc.getIndiceUV()+"";
		td.setText(uv);
		tr.addContent(td);
		td = new Element("td");
		td.setAttribute("colspan","2");
		uv = (ds.getIndiceUV() < 0) ?"":ds.getIndiceUV()+"";
		td.setText(uv);
		tr.addContent(td);
		td = new Element("td");
		td.setAttribute("colspan","2");
		uv = (ds2.getIndiceUV() < 0) ?"":ds2.getIndiceUV()+"";
		td.setText(uv);
		tr.addContent(td);
		
		root.addContent(tr);
		
		
		//Generamos el fichero
		DocType dt = new DocType("html");
		Document docu = new Document(root, dt);
		try{
			XMLOutputter salidaXml = new XMLOutputter(Format.getPrettyFormat());

			FileOutputStream archivo = new FileOutputStream("/tmp/tabla.html");
			salidaXml.output(docu, archivo);
			archivo.flush();
			archivo.close();
			System.out.println("Tabla HTML generada. ");
			
			InputStream is = new FileInputStream("/tmp/tabla.html");
			byte[] barray = IOUtils.toByteArray(is);
			
			// borramos fichero temporal
			Path path = FileSystems.getDefault().getPath("/tmp", "tabla.html");
			Files.delete(path);
			
			return barray;
		} catch (Exception ex){
			throw new AxisFault(ex.getMessage());
		}
	}
}

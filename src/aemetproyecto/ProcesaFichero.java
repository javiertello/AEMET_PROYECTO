package aemetproyecto;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.axis.AxisFault;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * Clase que se encarga de parsear el fichero XML de predicción
 * y devolver una lista de Dias con la información requerida 
 * almacenada en sus campos
 * 
 * @author Javier Tello Alquézar
 *
 */
public class ProcesaFichero {
	/**
	 * Procesa el fichero XML o JSON, crea y rellena los objetos
	 * y los devuelve finalmente en una lista
	 * 
	 * @param xmlFormat Si xmlFormat == true, los datos están codificados en XML,
	 * en caso de false, están codificados en JSON
	 * 
	 * @return Lista de Dias (Dia, DiaSemi o DiaCompleto)
	 */
	public List<? extends Dia> procesar(boolean xmlFormat, String file) throws AxisFault{
		List<Dia> lista;
		
		if(xmlFormat){

			try{
				// Almacenamos los objetos Dia
				lista = new ArrayList<Dia>();
				
				// Creamos el Builder
				SAXBuilder constructor = new SAXBuilder();
				
				// Deshabilitamos validacion DTD
				//constructor.setFeature(
				//		  "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
				
				//Construimos el documento con el arbol XML a partir del archivo XML
				//pasado como argumento
				Document doc = constructor.build(file);
				
				//Comprobamos que el archivo sea del tipo root
				//leyendo el elemento raiz
				Element raiz = doc.getRootElement();
				
				if(raiz.getName().equals("root")){
					//Cogemos la prediccion
					Element prediccion = raiz.getChild("prediccion");
					//Sacamos los dias
					List dias = prediccion.getChildren();
					
					Iterator itDias = dias.iterator();
					
					
					// Obtenemos dia actual y comparamos 
					// que sea el mismo. Sino, next Dia
					Iterator itDiasTest = dias.iterator();
					Calendar c = new GregorianCalendar();
					int hoy = c.get(Calendar.DATE);
					Element diaTest = (Element) itDiasTest.next();
					String fTest = diaTest.getAttributeValue("fecha");
					String[] transTest = fTest.split("-");
					System.out.println("Hoy es: "+hoy+"XML dice: "+transTest[2]);
					
					if(Integer.parseInt(transTest[2]) != hoy){
						itDias.next(); // Me cargo el primero (es dia de ayer)
					}
					
					int numDia = 1;
					// Recorro los dias
					while(itDias.hasNext()){
						Element dia = (Element) itDias.next();
						if(numDia == 1){ // Primer dia
							DiaCompleto dc = new DiaCompleto();
							// Fecha
							String f = dia.getAttributeValue("fecha");
							String[] trans = f.split("-");
							@SuppressWarnings("deprecation")
							Date fecha = new Date(Integer.parseInt(trans[0])-1900,
									Integer.parseInt(trans[1])-1, 
									Integer.parseInt(trans[2]));
							dc.setFecha(fecha);
							// PROBABILIDAD DE PRECIPITACION
							List<Element> probs = dia.getChildren("prob_precipitacion");
							for(Element prob :probs){
								String periodo = prob.getAttributeValue("periodo");
								if(periodo.equals("06-12")){
									if(prob.getValue().isEmpty()){
										dc.setProb_precip_1(-1);
									}else{
										dc.setProb_precip_1(Integer.parseInt(prob.getValue()));
									}					
								}else if(periodo.equals("12-18")){
									if(prob.getValue().isEmpty()){
										dc.setProb_precip_2(-1);
									}else{
										dc.setProb_precip_2(Integer.parseInt(prob.getValue()));
									}		
								}else if(periodo.equals("18-24")){
									if(prob.getValue().isEmpty()){
										dc.setProb_precip_3(-1);
									}else{
										dc.setProb_precip_3(Integer.parseInt(prob.getValue()));
									}		
								}
							}
							
							// COTA DE NIEVE
							List<Element> cotas = dia.getChildren("cota_nieve_prov");
							for(Element cota :cotas){
								String periodo = cota.getAttributeValue("periodo");
								Integer cota_int = -1;
								if(!cota.getValue().equals("")){
									cota_int = Integer.parseInt(cota.getValue());
								}
								
								if(periodo.equals("06-12")){
									dc.setCota_nieve_1(cota_int);
								}else if(periodo.equals("12-18")){
									dc.setCota_nieve_2(cota_int);
								}else if(periodo.equals("18-24")){
									dc.setCota_nieve_3(cota_int);
								}
							}
							
							// ESTADO DEL CIELO
							List<Element> estados = dia.getChildren("estado_cielo");
							for(Element estado :estados){
								String periodo = estado.getAttributeValue("periodo");
								String valor = estado.getValue();
								if(periodo.equals("06-12")){
									dc.setEstado_cielo_1(valor);
								}else if(periodo.equals("12-18")){
									dc.setEstado_cielo_2(valor);
								}else if(periodo.equals("18-24")){
									dc.setEstado_cielo_3(valor);
								}
							}
							
							// VIENTO
							List<Element> vientos = dia.getChildren("viento");
							for(Element viento : vientos){
								String periodo = viento.getAttributeValue("periodo");
								Viento v;
								if(periodo.equals("06-12")){
									String dir = viento.getChild("direccion").getValue();
									int vel = Integer.parseInt(viento.getChild("velocidad").getValue());
									v = new Viento(dir, vel);
									dc.setViento_1(v);
								}else if(periodo.equals("12-18")){
									String dir = viento.getChild("direccion").getValue();
									int vel = Integer.parseInt(viento.getChild("velocidad").getValue());
									v = new Viento(dir, vel);
									dc.setViento_2(v);
								}else if(periodo.equals("18-24")){
									String dir = viento.getChild("direccion").getValue();
									int vel = Integer.parseInt(viento.getChild("velocidad").getValue());
									v = new Viento(dir, vel);
									dc.setViento_3(v);
								}
							}
							
							// TEMPERATURA
							Element temp = dia.getChild("temperatura");
							int max = Integer.parseInt(temp.getChild("maxima").getValue());
							int min = Integer.parseInt(temp.getChild("minima").getValue());
							dc.setTempMaxima(max);
							dc.setTempMinima(min);
							
							// Indice UV
							int uv = -1;
							if(dia.getChild("uv_max").getValue().length()!=0){
								uv = Integer.parseInt(dia.getChild("uv_max").getValue());
							}
							dc.setIndiceUV(uv);
							// Añado a la lista
							lista.add(dc);
						}else if(numDia == 2 || numDia == 3){ // 2o y 3er dia
							DiaSemi ds = new DiaSemi();
							// Fecha
							String f = dia.getAttributeValue("fecha");
							String[] trans = f.split("-");
							@SuppressWarnings("deprecation")
							Date fecha = new Date(Integer.parseInt(trans[0])-1900,
									Integer.parseInt(trans[1])-1,
									Integer.parseInt(trans[2]));
							ds.setFecha(fecha);
							// PROBABILIDAD DE PRECIPITACION
							List<Element> probs = dia.getChildren("prob_precipitacion");
							for(Element prob :probs){
								String periodo = prob.getAttributeValue("periodo");
								if(periodo.equals("00-12")){
									ds.setProb_precip_1(Integer.parseInt(prob.getValue()));
								}else if(periodo.equals("12-24")){
									ds.setProb_precip_2(Integer.parseInt(prob.getValue()));
								}
							}
							
							// COTA DE NIEVE
							List<Element> cotas = dia.getChildren("cota_nieve_prov");
							for(Element cota :cotas){
								String periodo2 = cota.getAttributeValue("periodo");
								Integer cota_int = -1;
								if(!cota.getValue().equals("")){
									cota_int = Integer.parseInt(cota.getValue());
								}
								
								if(periodo2.equals("00-12")){
									ds.setCota_nieve_1(cota_int);
								}else if(periodo2.equals("12-24")){
									ds.setCota_nieve_2(cota_int);
								}
							}
							
							// ESTADO DEL CIELO
							List<Element> estados = dia.getChildren("estado_cielo");
							for(Element estado :estados){
								String periodo3 = estado.getAttributeValue("periodo");
								String valor = estado.getValue();
								if(periodo3.equals("00-12")){
									ds.setEstado_cielo_1(valor);
								}else if(periodo3.equals("12-24")){
									ds.setEstado_cielo_2(valor);
								}
							}
							
							// VIENTO
							List<Element> vientos = dia.getChildren("viento");
							for(Element viento : vientos){
								String periodo4 = viento.getAttributeValue("periodo");
								Viento v;
								if(periodo4.equals("00-12")){
									String dir = viento.getChild("direccion").getValue();
									int vel = Integer.parseInt(viento.getChild("velocidad").getValue());
									v = new Viento(dir, vel);
									ds.setViento_1(v);
								}else if(periodo4.equals("12-24")){
									String dir = viento.getChild("direccion").getValue();
									int vel = Integer.parseInt(viento.getChild("velocidad").getValue());
									v = new Viento(dir, vel);
									ds.setViento_2(v);
								}
							}
							
							// TEMPERATURA
							Element temp = dia.getChild("temperatura");
							int max = Integer.parseInt(temp.getChild("maxima").getValue());
							int min = Integer.parseInt(temp.getChild("minima").getValue());
							ds.setTempMaxima(max);
							ds.setTempMinima(min);
							
							// Indice UV
							int uv = -1;
							if(dia.getChild("uv_max").getValue().length()!=0){
								uv = Integer.parseInt(dia.getChild("uv_max").getValue());
							}
							ds.setIndiceUV(uv);
							// Añado a la lista
							lista.add(ds);
						}else{ // 3 ultimos dias de la tabla
							Dia d = new Dia();
							// Fecha
							String f = dia.getAttributeValue("fecha");
							String[] trans = f.split("-");
							@SuppressWarnings("deprecation")
							Date fecha = new Date(Integer.parseInt(trans[0])-1900,
									Integer.parseInt(trans[1])-1,
									Integer.parseInt(trans[2]));
							d.setFecha(fecha);
							// PROBABILIDAD DE PRECIPITACION
							Element prob = dia.getChild("prob_precipitacion");
							d.setProb_precip_1(Integer.parseInt(prob.getValue()));
							
							
							// COTA DE NIEVE
							Element cota = dia.getChild("cota_nieve_prov");
							Integer cota_int = -1;
							if(!cota.getValue().equals("")){
								cota_int = Integer.parseInt(cota.getValue());
							}
							d.setCota_nieve_1(cota_int);
							
							// ESTADO DEL CIELO
							Element estado = dia.getChild("estado_cielo");
							String valor = estado.getValue();
							d.setEstado_cielo_1(valor);
							
							// VIENTO
							Element viento = dia.getChild("viento");
							Viento v;
							String dir = viento.getChild("direccion").getValue();
							int vel = Integer.parseInt(viento.getChild("velocidad").getValue());
							v = new Viento(dir, vel);
							d.setViento_1(v);
							
							// TEMPERATURA
							Element temp = dia.getChild("temperatura");
							int max = Integer.parseInt(temp.getChild("maxima").getValue());
							int min = Integer.parseInt(temp.getChild("minima").getValue());
							d.setTempMaxima(max);
							d.setTempMinima(min);
							
							// Indice UV
							Element uv = dia.getChild("uv_max");
							int uv2 = -1;
							if(uv != null && (uv.getValue().length()!=0)){
								uv2 = Integer.parseInt(uv.getValue());
							}
							d.setIndiceUV(uv2);
							
							// Añado a la lista
							lista.add(d);
						}
						numDia++;
					}	
				}
				return lista;
			} catch (Exception ex){
				ex.printStackTrace();
				throw new AxisFault(ex.getMessage());
			}
			
		} else{ // JSON
			List<? extends Dia> listaJSON;
			Gson g = new GsonBuilder().setDateFormat("MMM dd, yyyy").create();
			// Deserializamos el JSON para meterlo en los objetos
			Type listType = new TypeToken<ArrayList<DiaCompleto>>() {
	        }.getType();
			try {
				listaJSON = g.fromJson(new FileReader(file), listType);
				return listaJSON;
			} catch (JsonIOException | JsonSyntaxException
					| FileNotFoundException e) {
				System.out.println(e.getClass());
				System.out.println(e.getCause());
				throw new AxisFault(e.getMessage());
			}
			
		}
	}
}

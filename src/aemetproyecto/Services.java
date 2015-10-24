package aemetproyecto;

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.axis.AxisFault;
import org.apache.axis.encoding.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Clase que gestiona las operaciones públicadas al servicio Web, llamado AemetProyect.
 * Contiene 3 operaciones: DescargarInfoTiempo, GenerarHTML y GenerarJSON
 * 
 * @author Javier Tello Alquézar 
 *
 */
public class Services {
	
	/**
	 * Dado un id de un municipio, descarga el XML de su predicción correspondiente
	 * y devuelve el XML que contiene el XML del AEMET codificado en Base64, con el 
	 * siguiente formato:
	 * 
	 * <resultado>xml.codificado.encode64</resultado>
	 * 
	 * @param idXML XML que contiene el Identificador del municipio y su DTD
	 * @return Cadena de bytes del xml
	 * @throws AxisFault En el caso de id incorrecto
	 */
	public String DescargarInfoTiempo(String DescargarInfoTiempo) throws AxisFault{
		DescargarInfoTiempo = new String(Base64.decode(DescargarInfoTiempo));
		try{
			// Validar XML
			
			ValidarXML v = new ValidarXML();
			if(!v.validar(DescargarInfoTiempo)){
				throw new Exception("Documento XML no valido (DescargarInfoTiempo)\n"+DescargarInfoTiempo);
			}
		     
			// Extraer ID
			SAXBuilder constructor = new SAXBuilder();
			Document doc = constructor.build(new StringReader(DescargarInfoTiempo));
			String id = doc.getRootElement().getValue();
			URL url = new URL("http://www.aemet.es/xml/municipios/localidad_"+id+".xml");

			// Hago un GET a la URL
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			int responseCode = connection.getResponseCode();
			if (responseCode == 404){ // Si es 404, id no existe
				throw new AxisFault("El id de la ciudad no existe");
			}
			
			InputStream is = url.openStream();
			byte[] barray = IOUtils.toByteArray(is);
			String codificado = Base64.encode(barray);
	    	//FileUtils.copyURLToFile(url, f);
	    	System.out.println("Fichero de predicción meteorológica descargado con éxito.");
	    	
	    	String xmlReturn = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
	    						"<!DOCTYPE resultado [\n"+
	    						"<!ELEMENT resultado (#PCDATA)>\n"+
	    						"]>\n"+
	    						"<resultado>"+codificado+"</resultado>";
	    	return Base64.encode(xmlReturn.getBytes());
		} catch (MalformedURLException e){
			// Url mal formada
			throw new AxisFault("URL mal formada");
		} catch (Exception ex){
			throw AxisFault.makeFault(new Exception(ex.getCause()));
			//throw AxisFault.makeFault(ex);
		}
		
	}
	
	/**
	 * Dado un xml que contiene:
	 * 
	 * <raiz>
	 * 	<formato>xml|json</formato>
	 *  <content>aj4ksdjk2k==...</content>
	 * </raiz>
	 * 
	 * y su DTD correspondiente, se valida, se extraen el XML o JSON del AEMET 
	 * (y se valida también en el caso de ser XML)
	 * para devolver un XML que contiene la tabla de predicción codificada en base64,
	 * con el siguiente formato:
	 * <resultado>tabla.html.codificada.encode64</resultado>
	 * 
	 * @param xml con el xml o json de predicción
	 * @return XML que contiene la tabla de predicción codificada en base64 y su DTD
	 * @throws AxisFault En caso de error
	 */
	public String GenerarHTML(String GenerarHTML) throws AxisFault{
		
		GenerarHTML = new String(Base64.decode(GenerarHTML));
				
		try {
			boolean xmlFormat;
			
			/*Extraer formato y xml del AEMET
			 * <raiz>
			 * 	<formato>xml|json</formato>
			 *  <content>aj4ksdjk2k==...</content>
			 * </raiz>
			 */
			SAXBuilder constructor = new SAXBuilder();
			Document doc = constructor.build(new StringReader(GenerarHTML));
			Element raiz = doc.getRootElement();
			String formato = raiz.getChild("formato").getValue();
			if(formato.equals("xml")){
				xmlFormat=true;
			} else{
				xmlFormat=false;
			}
			if(xmlFormat){
				// Validar SOAP BODY 
				ValidarXML va = new ValidarXML();
				if(!va.validar(GenerarHTML)){
					throw new Exception("Documento XML no valido (GenerarHTML)");
				}
			}
			String base64XML = raiz.getChild("content").getValue();
			byte[] datos = Base64.decode(base64XML);
			
			FileUtils.writeByteArrayToFile(new File("/tmp/tmp.txt"), datos);
			if(xmlFormat){		
				// Validamos el xml del AEMET con el DTD
				ValidarXML v = new ValidarXML();
				if(!v.validarAEMET("/tmp/tmp.txt")){
					throw new Exception("Documento XML AEMET no valido (GenerarHTML)");
				}
			}
			ProcesaFichero procesa = new ProcesaFichero();
			List<? extends Dia> dias = procesa.procesar(xmlFormat, "/tmp/tmp.txt");
			
			// Borramos fichero temporal
			Path path = FileSystems.getDefault().getPath("/tmp", "tmp.txt");
			Files.delete(path);
			
			byte[] blob = new GenerarPaginaTiempo().generar(dias);
			String tablaHTMLencode64 = Base64.encode(blob);
			String xmlReturn = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
					"<!DOCTYPE resultado [\n"+
					"<!ELEMENT resultado (#PCDATA)>\n"+
					"]>\n"+
					"<resultado>"+tablaHTMLencode64+"</resultado>";
			return Base64.encode(xmlReturn.getBytes());
			
		} catch (Exception e) {
			throw AxisFault.makeFault(e);
		}
	}
	
	/**
	 * Dado un xml que contiene el XML del aemet codificado en Base64 tal que:
	 * <aemet>lkasdkls==...</aemet>
	 * y su correspondiente DTD, lo valida, y genera un XML con el mismo contenido
	 * pero en formato JSON codificado en Base64. El formato del xml de respuesta es:
	 * 
	 * <resultado>json.codificado.encode64</resultado>
	 * 
	 * @param xml que contiene el XML del aemet codificado en Base64 y su DTD
	 * @return XML con el contenido de predicción en JSON codificado en Base64
	 * @throws AxisFault En caso de error
	 */
	public String GenerarJSON(String GenerarJSON) throws AxisFault{
		
		GenerarJSON = new String(Base64.decode(GenerarJSON));
		
		try {
			// Validar XML
			ValidarXML v = new ValidarXML();
			if(!v.validar(GenerarJSON)){
				throw new Exception("Documento XML no valido (GenerarJSON)");
			}
			// Parseo el xml
			SAXBuilder constructor = new SAXBuilder();
			Document doc = constructor.build(new StringReader(GenerarJSON));
			String xmlBase64 = doc.getRootElement().getValue();
			byte[] tiempo = Base64.decode(xmlBase64);
			FileUtils.writeByteArrayToFile(new File("/tmp/tmp.xml"), tiempo);
			
			// Validamos el xml con el DTD
			ValidarXML v2 = new ValidarXML();
			if(!v2.validarAEMET("/tmp/tmp.xml")){
				throw new Exception("Documento XML AEMET no valido (GenerarJSON)");
			}
			ProcesaFichero procesa = new ProcesaFichero();
			List<? extends Dia> dias = procesa.procesar(true, "/tmp/tmp.xml");
			
			// Borramos fichero temporal
			Path path = FileSystems.getDefault().getPath("/tmp", "tmp.xml");
			Files.delete(path);
			
			Gson g = new GsonBuilder().setPrettyPrinting().create();
			String s = g.toJson(dias);
			String jsonBase64 = Base64.encode(s.getBytes());
			String xmlReturn = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
					"<!DOCTYPE resultado [\n"+
					"<!ELEMENT resultado (#PCDATA)>\n"+
					"]>\n"+
					"<resultado>"+jsonBase64+"</resultado>";
	
			return Base64.encode(xmlReturn.getBytes());
			
		} catch (Exception e) {
			throw AxisFault.makeFault(e);
		}
	}
	
}

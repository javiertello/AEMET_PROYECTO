package cliente;

import java.io.StringReader;

import org.apache.axis.AxisFault;
import org.apache.axis.encoding.Base64;import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;

import aemetproyecto.Services;

/**
 * Gestiona una prueba de los métodos que serán desplegados en el WS, pero de manera local.
 * 
 * @author Javier Tello
 *
 */
public class PruebaLocal {
	public static void  main (String[] args){
		try{
			Services s = new Services();
			String peticion = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
					"<!DOCTYPE id [\n"+
					"<!ELEMENT id (#PCDATA)>\n"+
					"]>\n"+
					"<id>50257</id>";
			String petCodif = Base64.encode(peticion.getBytes());
			System.out.println(petCodif);

			String res = s.DescargarInfoTiempo(petCodif);
			String decodif = new String(Base64.decode(res));
			
			//System.out.println(res);
			SAXBuilder constructor = new SAXBuilder();
			Document doc = constructor.build(new StringReader(decodif));
			String base64xml = doc.getRootElement().getValue();
			
			String json = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
					"<!DOCTYPE aemet [\n"+
					"<!ELEMENT aemet (#PCDATA)>\n"+
					"]>\n"+
					"<aemet>"+base64xml+"</aemet>";
			String JSONCodif = Base64.encode(json.getBytes());

			String JSONres = new String(Base64.decode(s.GenerarJSON(JSONCodif)));
			
			SAXBuilder constructor2 = new SAXBuilder();
			Document doc2 = constructor2.build(new StringReader(JSONres));
			String base64json = doc2.getRootElement().getValue();
			
			String html2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
					"<!DOCTYPE raiz [\n"+
					"<!ELEMENT raiz (formato, content)>\n"+
					"<!ELEMENT formato (#PCDATA)>\n"+
					"<!ELEMENT content (#PCDATA)>\n"+
					"]>\n"+
					"<raiz>\n"+
					"<formato>json</formato>\n"+
					"<content>"+base64json+"</content>\n"
					+ "</raiz>";
			
			String html = s.GenerarHTML(Base64.encode(html2.getBytes()));
			
			String htmlDecodif = new String(Base64.decode(html));
			
			SAXBuilder constructor3 = new SAXBuilder();
			Document doc3 = constructor3.build(new StringReader(htmlDecodif));
			String htmlFinal = doc3.getRootElement().getValue();
			
			System.out.println(new String(Base64.decode(htmlFinal)));
			//System.out.println(new String(blob));
			//byte[] json = s.GenerarJSON(blob);
			//System.out.println(new String(json));
			//byte[] html = s.GenerarHTML(false, json);
			//System.out.println(new String(html));
			
		} catch (AxisFault af){
			System.out.println(af.getMessage());
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}

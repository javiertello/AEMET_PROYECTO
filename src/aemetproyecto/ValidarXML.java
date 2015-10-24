package aemetproyecto;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
/*
 * Clase que se encarga de validar XML's
 */
public class ValidarXML {

	private final String dtd = "aemet.dtd";
	
	/**
	 * Valida el XML descargado del AEMET con el dtd "aemet.dtd"
	 * 
	 * @param rutaAlXML ruta al xml del AEMET
	 * @return true si es válido, false en caso contrario
	 * @throws Exception en caso de error
	 */
	public boolean validarAEMET(String rutaAlXML) throws Exception{
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			//factory.setValidating(true);
			
			DocumentBuilder db = factory.newDocumentBuilder();
			//parse file into DOM
			Document doc = db.parse(new File(rutaAlXML));
			DOMSource source = new DOMSource(doc);
			
			//now use a transformer to add the DTD element
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, 
						"../webapps/axis/WEB-INF/recursos/"+dtd);
			//transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, 
			//			"/home/me/apache-tomcat-7.0.62/webapps/axis/WEB-INF/recursos/aemet.dtd");
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			transformer.transform(source, result);
			
			factory.setValidating(true);
			DocumentBuilder db2 = factory.newDocumentBuilder();
			// Sobreescribo Handler
			db2.setErrorHandler(new ErrorHandler() {
			    @Override
			    public void error(SAXParseException exception) throws SAXException {
			        // do something more useful in each of these handlers
			        throw exception;
			    }
			    @Override
			    public void fatalError(SAXParseException exception) throws SAXException {
			    	throw exception;
			    }

			    @Override
			    public void warning(SAXParseException exception) throws SAXException {
			    	throw exception;
			    }
			});
			//finally parse the result. 
			//this will throw an exception if the doc is invalid
			db2.parse(new InputSource(new StringReader(writer.toString())));
			//System.out.println(writer.toString());
			return true;
		} catch (SAXException saxe){
			return false;
		} catch (Exception e){
			throw e;
		}
	}
	
	/**
	 * Valida un String que contiene un XML con un Dtd embebido.
	 * 
	 * @param xml EL string que contiene el XML con el DTD
	 * @return true si es válido, false en caso contrario
	 * @throws Exception en caso de error
	 */
	public boolean validar(String xml) throws Exception{
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true);

			DocumentBuilder builder = factory.newDocumentBuilder();

			// Sobreescribo Handler
			builder.setErrorHandler(new ErrorHandler() {
				@Override
				public void error(SAXParseException exception)
						throws SAXException {
					// do something more useful in each of these handlers
					throw exception;
				}

				@Override
				public void fatalError(SAXParseException exception)
						throws SAXException {
					throw exception;
				}

				@Override
				public void warning(SAXParseException exception)
						throws SAXException {
					throw exception;
				}
			});
			System.out.println(xml);
			builder.parse(new InputSource(new StringReader(xml)));

			//System.out.println(writer.toString());
			return true;
		} catch (SAXException saxe){
			return false;
		} catch (Exception e){
			throw e;
		}
	}
}

package cliente;

import org.apache.axis.AxisFault;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.Base64;

import javax.xml.namespace.QName;

/**
 * Gestiona una prueba del Web Service usando la librería de AXIS. 
 * LLama a las 3 operaciones disponibles e imprime por pantalla el resultado. 
 * En caso de Fault, muestra el error por pantalla.
 * 
 * @author Javier Tello
 *
 */
public class PruebaWS
{
	public static void main(String [] args)
	{
		try {
			
			String endpointURL = "http://localhost:8080/axis/services/AemetProyect";
			
			Service service = new Service();	
			// Obtengo el XML
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress( new java.net.URL(endpointURL) );
			call.setOperationName( new QName("http://soapinterop.es/", "DescargarInfoTiempo") );
			String peticion = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
					"<!DOCTYPE id [\n"+
					"<!ELEMENT id (#PCDATA)>\n"+
					"]>\n"+
					"<id>50257</id>";
			String petCodif = Base64.encode(peticion.getBytes());
			String xmlCodif = (String) call.invoke( new Object[] { petCodif } );
			System.out.println(xmlCodif);
			
			
		}catch (AxisFault af) {
			System.out.println(af.getMessage());
		}
		catch (Exception e){
			System.out.println(e.getMessage());
		}
	
	}
}
package cliente;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.xml.namespace.QName;

import org.apache.axis.AxisFault;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Clase que implementa al Cliente del Servicio WEB "AemetServices". Permite seleccionar un
 * municipio de cualquier provincia Española, y, mediante las operaciones remotas que ofrece el 
 * servicio Web, generar el HTML y JSON con el contenido de la predicción meteorológica.
 *  
 * @author Javier Tello Alquézar
 *
 */
public class Cliente {
	
	static final String endpointURL = "http://localhost:8080/axis/services/AemetServices";
	
	static JComboBox<Provincia> provincias;
	static JComboBox<Municipio> municipios;
	
	static List<Provincia> arrayProvincias;
	static List<Municipio> arrayMunicipios;
	
	/**
	 * Método principal
	 * @param args No necesita parametros
	 */
	public static void main(String[] args){
		arrayProvincias = obtenerProvincias();
		// A Coruña por defecto, id = 15
		arrayMunicipios = obtenerMunicipios("15"); 
		
		JFrame frame = new JFrame("AEMET Web Services");
		frame.setSize(300, 150);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		
		JPanel panel = new JPanel();
		panel.setLayout(null);

		frame.add(panel);
		
		JLabel provinciaLabel = new JLabel("Provincia: ");
		provinciaLabel.setBounds(10, 10, 80, 25);
		panel.add(provinciaLabel);
		
		provincias = new JComboBox(arrayProvincias.toArray());
		provincias.setBounds(90, 10, 160, 25);
		panel.add(provincias);
		
		provincias.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	actualizarMunicipios();
		    }
		});
		
		JLabel municipioLabel = new JLabel("Municipio: ");
		municipioLabel.setBounds(10, 40, 80, 25);
		panel.add(municipioLabel);
		
		municipios = new JComboBox(arrayMunicipios.toArray());
		municipios.setBounds(90, 40, 200, 25);
		panel.add(municipios);
		
		JButton htmlButton = new JButton("Generar HTML");
		htmlButton.setBounds(5, 80, 140, 25);
		panel.add(htmlButton);
		htmlButton.addActionListener(new ActionListener()
		{
		  public void actionPerformed(ActionEvent e)
		  {
		    mostrarHTML();
		  }
		});
		
		JButton jsonButton = new JButton("Generar JSON");
		jsonButton.setBounds(150, 80, 140, 25);
		panel.add(jsonButton);
		jsonButton.addActionListener(new ActionListener()
		{
		  public void actionPerformed(ActionEvent e)
		  {
		    mostrarJSON();
		  }
		});
		
		frame.setVisible(true);

	}
	
	/**
	 * Obtiene la lista de provincias que se encuentran en un mapa html en la url "url".
	 * Parsea el HTML y obtiene la URL y nombre a partir de los tags "area" dentro de "map".
	 * 
	 * @return La lista de las Provincias.
	 */
	public static List<Provincia> obtenerProvincias(){
		List<Provincia> lista = new ArrayList<Provincia>(50);
		String url = "http://www.aemet.es/es/eltiempo/prediccion/municipios";
		try {
			Document doc = Jsoup.connect(url).get();
			Elements areas = doc.select("area");
			
			for(Element area : areas){
				String ruta = area.attr("href");
				String provincia = area.attr("title");
				String indice = ruta.split("=")[1];
				lista.add(new Provincia(indice, provincia));
			}
			return lista;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Parsea el HTML de la página "url" y obtiene todos los municipios que se encuentran
	 * en la tabla.
	 * 
	 * @param idProvincia El id de la provincia
	 * @return Lista de Municipios de la provincia con id "idProvincia"
	 */
	public static List<Municipio> obtenerMunicipios(String idProvincia){
		List<Municipio> lista = new ArrayList<Municipio>(100);
		String url = "http://www.aemet.es/es/eltiempo/prediccion/municipios?p="+idProvincia+"&w=t";
		try {
			Document doc = Jsoup.connect(url).get();
			Element table = doc.select("table").first();
			Element tbody = table.select("tbody").first();
			Elements municipios = tbody.select("a[href]");
			for(Element municipio : municipios){
				String ruta = municipio.attr("href");
				String m = municipio.text();
				String indice = ruta.split("-id")[1];
				lista.add(new Municipio(indice, m));
			}
			return lista;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Actualizar el Combo box de Municipios en función de la provincia que
	 * se haya seleccionado.
	 */
	public static void actualizarMunicipios(){
		Provincia selected = (Provincia) provincias.getSelectedItem();
		arrayMunicipios = obtenerMunicipios(selected.getId());
		// Actualizo el JComboBox de municipios
		municipios.removeAllItems();
		for(Municipio m: arrayMunicipios){
			municipios.addItem(m);
		}
	}
	
	/**
	 * Muestra la tabla HTML de predicción del municipio seleccionado.
	 */
	public static void mostrarHTML(){
		Municipio selected = (Municipio) municipios.getSelectedItem();
		String idMunicipio = selected.getId();
		byte[] xml = obtenerXML(idMunicipio);
		if(xml == null){
			JOptionPane.showMessageDialog(new JFrame(),
				    "Se produjo un error al generar el XML",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
		}else{
			try{
				// Obtengo el HTML
				Service service2 =  new Service();
				Call call2 = (Call) service2.createCall();
				call2.setTargetEndpointAddress( new java.net.URL(endpointURL) );
				call2.setOperationName( new QName("http://soapinterop.org/", "GenerarHTML") );
				byte[] htmlbytes = (byte[]) call2.invoke( new Object[] { xml } );
				String html = new String(htmlbytes);
				System.out.println(html);
				// MUESTRO HTML
				JEditorPane editor = new JEditorPane();
				editor.setText(html);
				editor.setEditable(false);
				JFrame frame = new JFrame();

				JScrollPane pane = new JScrollPane(editor, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				frame.add(pane);
				frame.setSize(600, 600);
				frame.setVisible(true);
				
			} catch (AxisFault af) {
				System.out.println(af.getMessage());
				JOptionPane.showMessageDialog(new JFrame(),
					    af.getMessage(),
					    "Fault",
					    JOptionPane.ERROR_MESSAGE);
			}
			catch (Exception e){
				System.out.println(e.getMessage());
				JOptionPane.showMessageDialog(new JFrame(),
					    "Se produjo un error graveal generar el HTML",
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	/**
	 * Obtiene el xml del municipio con identificador "id" llamando a la operación que ofrece
	 * el servicio web a través de AXIS.
	 * @return el String que contiene el xml de predicción
	 */
	public static byte[] obtenerXML(String id){
		try{
			Service service = new Service();	
			// Obtengo el XML
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress( new java.net.URL(endpointURL) );
			call.setOperationName( new QName("http://soapinterop.org/", "DescargarInfoTiempo") );
			byte[] xml = (byte[]) call.invoke( new Object[] { id } );
			//System.out.println("XML generado correctamente");
			return xml;
			
		} catch (AxisFault af) {
			System.out.println(af.getMessage());
			return null;
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			return null;
		}
	}
	/**
	 * Muestra por pantalla el JSON del municipio seleccionado.
	 */
	public static void mostrarJSON(){
		Municipio selected = (Municipio) municipios.getSelectedItem();
		String idMunicipio = selected.getId();
		byte[] xml = obtenerXML(idMunicipio);
		if(xml == null){
			JOptionPane.showMessageDialog(new JFrame(),
				    "Se produjo un error al generar el XML",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
		}else{
			try{
				// Genero JSON
				Service service3 =  new Service();
				Call call3 = (Call) service3.createCall();
				call3.setTargetEndpointAddress( new java.net.URL(endpointURL) );
				call3.setOperationName( new QName("http://soapinterop.org/", "GenerarJSON") );
				byte[] jsonb = (byte[]) call3.invoke( new Object[] { xml } );
				System.out.println(new String(jsonb));
				String json = new String(jsonb);
				// MUESTRO JSON
				JEditorPane editor = new JEditorPane();
				editor.setText(json);
				editor.setEditable(false);
				JFrame frame = new JFrame();

				JScrollPane pane = new JScrollPane(editor, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				frame.add(pane);
				frame.setSize(600, 600);
				frame.setVisible(true);
				
			} catch (AxisFault af) {
				System.out.println(af.getMessage());
				JOptionPane.showMessageDialog(new JFrame(),
					    af.getMessage(),
					    "Fault",
					    JOptionPane.ERROR_MESSAGE);
			}
			catch (Exception e){
				System.out.println(e.getMessage());
				JOptionPane.showMessageDialog(new JFrame(),
					    "Se produjo un error graveal generar el JSON",
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}

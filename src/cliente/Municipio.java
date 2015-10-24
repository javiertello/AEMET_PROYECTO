package cliente;
/**
 * Encapsula un Municipio. Sintácticamente, 
 * se almacena igual que una provincia (id + nombre)
 * 
 * @author Javier Tello
 *
 */
public class Municipio extends Provincia{

	public Municipio(){ }
	
	public Municipio(String id, String nombre){
		super(id, nombre);
	}
}

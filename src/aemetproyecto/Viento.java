package aemetproyecto;

/**
 * Encapsula un objeto Viento, que almacena la dirección
 * y su velocidad.
 * 
 * @author Javier Tello Alquézar
 *
 */
public class Viento {
	
	// Atributos
	private String direccion;
	private int velocidad;
	
	/**
	 * Constructor 
	 * 
	 * @param dir
	 * @param vel
	 */
	public Viento(String dir, int vel){
		this.direccion = dir;
		this.velocidad = vel;
	}
	
	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public int getVelocidad() {
		return velocidad;
	}

	public void setVelocidad(int velocidad) {
		this.velocidad = velocidad;
	}

}

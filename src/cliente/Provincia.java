package cliente;
/**
 * Encapsula a un objeto provincia. Contiene una id única (1-52) y un nombre.
 * 
 * @author Javier Tello
 *
 */
public class Provincia {
	
	private String id;
	private String nombre;
	
	public Provincia(){	}
	
	public Provincia(String id, String nombre){
		this.id = id;
		this.nombre = nombre;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String toString(){		
		return this.nombre;
	}
}

package aemetproyecto;

import java.util.Date;
/**
 * Clase que encapsula un objeto Dia, con sus 
 * correspondientes métodos getters y setters.
 * 
 * @author Javier Tello Alquézar
 *
 */
public class Dia {
	
	// Atributos
	private int tempMinima;
	private int tempMaxima;
	private int indiceUV;
	private String estado_cielo_1;
	private int prob_precip_1;
	private int cota_nieve_1;
	private Viento viento_1;
	private Date fecha;
	
	/**
	 * Empty constructor
	 */
	public Dia(){
		
	}
	
	/**
	 * Constructor con parámetros
	 * @param min
	 * @param max
	 * @param uv
	 */
	public Dia(int min, int max, int uv){
		tempMinima = min;
		tempMaxima = max;
		indiceUV = uv;
	}

	public int getTempMinima() {
		return tempMinima;
	}

	public void setTempMinima(int tempMinima) {
		this.tempMinima = tempMinima;
	}

	public int getTempMaxima() {
		return tempMaxima;
	}

	public void setTempMaxima(int tempMaxima) {
		this.tempMaxima = tempMaxima;
	}

	public int getIndiceUV() {
		return indiceUV;
	}

	public void setIndiceUV(int indiceUV) {
		this.indiceUV = indiceUV;
	}
	
	public String getEstado_cielo_1() {
		return estado_cielo_1;
	}

	public void setEstado_cielo_1(String estado_cielo_1) {
		this.estado_cielo_1 = estado_cielo_1;
	}
	
	public int getProb_precip_1() {
		return prob_precip_1;
	}

	public void setProb_precip_1(int prob_precip_1) {
		this.prob_precip_1 = prob_precip_1;
	}
	
	public String getCota_nieve_1() {
		if(cota_nieve_1 <= 0 )
			return "";
		else
			return Integer.toString(cota_nieve_1);
	}

	public void setCota_nieve_1(int cota_nieve_1) {
		this.cota_nieve_1 = cota_nieve_1;
	}
	
	public Viento getViento_1() {
		return viento_1;
	}

	public void setViento_1(Viento viento_1) {
		this.viento_1 = viento_1;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
}

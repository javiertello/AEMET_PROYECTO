package aemetproyecto;

/**
 * Encapsula un objeto DiaSemi que extiende a Dia.
 * Constituye un Día con 2 periodos (0-12 y 12-24)
 * @author Javier Tello Alquézar
 *
 */
public class DiaSemi extends Dia{
	
	// Atributos
	private String estado_cielo_2;
	
	private int prob_precip_2;
	
	private int cota_nieve_2;
	
	private Viento viento_2;
	
	/**
	 * Empty constructor
	 */
	public DiaSemi(){
		super();
	}

	
	public String getEstado_cielo_2() {
		return estado_cielo_2;
	}

	public void setEstado_cielo_2(String estado_cielo_2) {
		this.estado_cielo_2 = estado_cielo_2;
	}

	

	public int getProb_precip_2() {
		return prob_precip_2;
	}

	public void setProb_precip_2(int prob_precip_2) {
		this.prob_precip_2 = prob_precip_2;
	}


	public String getCota_nieve_2() {
		if(cota_nieve_2 <= 0 )
			return "";
		else
			return Integer.toString(cota_nieve_2);
	}

	public void setCota_nieve_2(int cota_nieve_2) {
		this.cota_nieve_2 = cota_nieve_2;
	}


	public Viento getViento_2() {
		return viento_2;
	}

	public void setViento_2(Viento viento_2) {
		this.viento_2 = viento_2;
	}
	
}

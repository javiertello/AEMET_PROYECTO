package aemetproyecto;

/**
 * Encapsula un objeto DiaCompleto que extiende a DiaSemi.
 * Constituye un Día con 3 periodos (6-12, 12-18 y 18-24)
 * @author Javier Tello Alquézar
 *
 */
public class DiaCompleto extends DiaSemi{
	
	// Atributos
	private String estado_cielo_3;
	
	private int prob_precip_3;
	
	private int cota_nieve_3;
	
	private Viento viento_3;
	
	/**
	 * Empty constructor
	 */
	public DiaCompleto(){
		super();
	}

	public String getEstado_cielo_3() {
		return estado_cielo_3;
	}

	public void setEstado_cielo_3(String estado_cielo_3) {
		this.estado_cielo_3 = estado_cielo_3;
	}

	public int getProb_precip_3() {
		return prob_precip_3;
	}

	public void setProb_precip_3(int prob_precip_3) {
		this.prob_precip_3 = prob_precip_3;
	}

	public String getCota_nieve_3() {
		if(cota_nieve_3 <= 0 )
			return "";
		else
			return Integer.toString(cota_nieve_3);
	}

	public void setCota_nieve_3(int cota_nieve_3) {
		this.cota_nieve_3 = cota_nieve_3;
	}


	public Viento getViento_3() {
		return viento_3;
	}

	public void setViento_3(Viento viento_3) {
		this.viento_3 = viento_3;
	}
	
	
	
}

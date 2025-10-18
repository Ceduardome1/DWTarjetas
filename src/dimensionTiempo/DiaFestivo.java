package dimensionTiempo;

public class DiaFestivo {

	private final Fecha fecha;
	private final String festejo;
	private final boolean esLibre;
	
	public DiaFestivo( String festejo, Fecha fecha, boolean esLibre ) {
		this.fecha = fecha;
		this.festejo = festejo;
		this.esLibre = esLibre;
	}
	
	public Fecha getFecha() {
		return fecha;
	}
	public String getFestejo() {
		return festejo;
	}
	
	public String toString() {
		return festejo;
	}

	public boolean esLaboral() {
		return esLibre;
	}
	
}
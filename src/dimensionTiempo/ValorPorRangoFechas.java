package dimensionTiempo;

public class ValorPorRangoFechas {

	private final Fecha fechaIn, fechaFin;
	private final String valor;
	
	public ValorPorRangoFechas( String valor, Fecha fechaIn, Fecha fechaFin ) {
		this.valor = valor;
		this.fechaIn = fechaIn;
		this.fechaFin = fechaFin;
	}

	public Fecha getFechaIn() {
		return fechaIn;
	}

	public Fecha getFechaFin() {
		return fechaFin;
	}

	public String getValor() {
		return valor;
	}
	
	public boolean perteneceRango(Fecha fecha) {
	    int clave = fecha.getClaveCalendario();
	    int claveIn = fechaIn.getClaveCalendario();
	    int claveFin = fechaFin.getClaveCalendario();

	    if (claveIn <= claveFin) 
	    		return clave >= claveIn && clave <= claveFin;
	    else 
	    		return clave >= claveIn || clave <= claveFin;
	}
	
	public String toString() { return valor; }
	
}

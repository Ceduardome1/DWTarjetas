package dimensionTiempo;

import java.util.Calendar;

public class DimensionFecha {
	
	private static final String diasSemana[] = { "Domingo","Lunes","Martes","Miércoles","Jueves","Viernes","Sábado" };
	private static final String meses[] = {  "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" };
	
	private final Fecha fecha;
	private final DiaFestivo diaFestivo;
	private final Calendar calendario;
	private final byte codDiaSemana;
	private final short diaAño;

	public DimensionFecha ( Fecha fecha, Calendar calendario, GeneradorFestivos genFestivos ) {
		this.fecha = fecha;
		this.calendario = calendario;
		calendario.set( fecha.getAño(), fecha.getMes() - 1, fecha.getDia() ); 
		diaFestivo = genFestivos.getDiaFestivo( fecha );
		codDiaSemana = (byte) (calendario.get( Calendar.DAY_OF_WEEK ) - 1 );
		diaAño = (short) ( calendario.get(Calendar.DAY_OF_YEAR) );
	}
	
	public String getAnimalChino( GeneradorAñoChino generadorChino ) {
		return generadorChino.animalChino( fecha.toString() );
	}

	public String getElementoChino( GeneradorAñoChino generadorChino ) {
		return generadorChino.animalChino( fecha.toString() );
	}
	
	public String getSignoZodiacal( GeneradorSignoZodiaco generadorSigno ) {
		return generadorSigno.getSigno( fecha );
	}
	
	public String getDiaFestejo( ) {
		return diaFestivo.getFestejo();
	}
	
	public String getEstacion( GeneradorEstacion generadorEstacion ) {
		return generadorEstacion.getEstacion( fecha );
	}
	
	public String esBisiesto() {
		return fecha.esBisiesto()? "Sí":"No";
	}

	public String esQuincena() {
		return fecha.esQuincena()? "Sí":"No";
	}
	
	public String esFestivo() {
		return diaFestivo!=null? "Sí":"No";
	}
	
	public String esFestivoLaboral() {
		return diaFestivo!=null && diaFestivo.esLaboral()? "Sí":"No";
	}
	
	public String esLaboral() {
			if( codDiaSemana == 0 || codDiaSemana == 6 )
				return "No";
		return diaFestivo!=null && diaFestivo.esLaboral()? "Sí":"No" ;
	}
	
	public String getDia() {
		return diasSemana[ codDiaSemana ];
	}
	
	public String getMes() {
		return meses[ calendario.get( Calendar.MONTH ) ];
	}
	
	public String getQuincenaMes() {
		return String.format( "%02d", ( (fecha.getMes() - 1) / 15 ) + 1 );
	}
	
	public String getQuincenaAño() {
		return String.format( "%02d", ( (diaAño - 1) / 15 ) + 1 );
	}
	
	public String getSemanaAño() {
		
		return String.format( "%02d", calendario.get(Calendar.WEEK_OF_YEAR) );
	}
	
	public String getDiaAño() {
		return String.format( "%03d", diaAño );
	}
	
	public String getBimestre() {
	    return String.format("%02d", ((fecha.getMes() - 1) / 2) + 1);
	}

	public String getTrimestre() {
	    return String.format("%02d", ((fecha.getMes() - 1) / 3) + 1);
	}

	public String getCuatrimestre() {
	    return String.format("%02d", ((fecha.getMes() - 1) / 4) + 1);
	}

	public String getSemestre() {
	    return String.format("%02d", ((fecha.getMes() - 1) / 6) + 1);
	}
	
}
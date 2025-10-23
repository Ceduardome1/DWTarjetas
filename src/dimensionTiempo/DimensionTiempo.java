package dimensionTiempo;

import java.util.Calendar;

public class DimensionTiempo {
	
	private static final String diasSemana[] = { "Domingo","Lunes","Martes","Miércoles","Jueves","Viernes","Sábado" };
	private static final String meses[] = {  "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" };
	
	private final Fecha fecha;
	private final DiaFestivo diaFestivo;
	private final Calendar calendario;
	private final byte codDiaSemana;
	private final short diaAño;

	public DimensionTiempo ( Fecha fecha, Calendar calendario, GeneradorFestivos genFestivos ) {
		this.fecha = fecha;
		this.calendario = calendario;
		calendario.set( fecha.getAño(), fecha.getMes() - 1, fecha.getDia() ); 
		diaFestivo = genFestivos.getDiaFestivo( fecha );
		codDiaSemana = (byte) (calendario.get( Calendar.DAY_OF_WEEK ) - 1 );
		diaAño = (short) ( calendario.get(Calendar.DAY_OF_YEAR) );
	}
	
	public String getNomDia() {
		return diasSemana[ codDiaSemana ];
	}
	public byte getNumDia() {
		return fecha.getDia();
	}
	public String getNomMes() {
		return meses[ calendario.get( Calendar.MONTH ) ];
	}
	
	public short getAño() {
		return fecha.getAño();
	}
	
	public byte getBimestre() {
	    return (byte) ( ( ( fecha.getMes() - 1) / 2) + 1 );
	}

	public byte getTrimestre() {
	    return (byte) ( ((fecha.getMes() - 1) / 3) + 1);
	}

	public byte getCuatrimestre() {
	    return (byte) ( ((fecha.getMes() - 1) / 4) + 1);
	}

	public byte getSemestre() {
	    return (byte) ( ((fecha.getMes() - 1) / 6) + 1);
	}
	
	public byte getSemanaMes() {
	    return (byte) calendario.get(Calendar.WEEK_OF_MONTH);
	}

	public byte getSemanaAño() {
		
		return (byte) ( calendario.get(Calendar.WEEK_OF_YEAR) );
	}
	
	public short getDiaAño() {
		return diaAño;
	}
	
	public byte getQuincenaAño() {
		return (byte) ( ( (diaAño - 1) / 15 ) + 1 );
	}
	
	public byte getQuincenaMes() {
		return (byte) ( ( (fecha.getMes() - 1) / 15 ) + 1 );
	}
	
	public String esBisiesto() {
		return fecha.esBisiesto()? "Y":"N";
	}

	public String esQuincena() {
		return fecha.esQuincena()? "Y":"N";
	}
	
	public String esFestivo() {
		return diaFestivo!=null? "Y":"N";
	}
	
	public String esFestivoLaboral() {
		return diaFestivo!=null && diaFestivo.esLaboral()? 
			"Y":"N";
	}
	
	public String esLaboral() {
			if( codDiaSemana == 0 || codDiaSemana == 6 )
				return "N";
		return diaFestivo!=null && diaFestivo.esLaboral()? "Y":"N";
	}
	
	
	public String getFestejo() {
		return diaFestivo!=null? diaFestivo.getFestejo() : null;
	}
	
	public String getEstacion( GeneradorEstacion generadorEstacion ) {
		return generadorEstacion.getEstacion( fecha );
	}
	
	public String getSignoZodiacal( GeneradorSignoZodiaco generadorSigno ) {
		return generadorSigno.getSigno( fecha );
	}
	
	public String getAnimalChino( GeneradorAñoChino generadorChino ) {
		return generadorChino.animalChino( fecha.toString() );
	}

	public String getElementoChino( GeneradorAñoChino generadorChino ) {
		return generadorChino.elementoChino( fecha.toString() );
	}

}
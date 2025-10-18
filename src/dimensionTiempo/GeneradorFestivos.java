package dimensionTiempo;

import java.util.HashMap;

public class GeneradorFestivos {

	private HashMap< Short , DiaFestivo > mapaDiasFestivos;

	public GeneradorFestivos() {
		inicializaFestivos();
	}

	private void inicializaFestivos() {

		DiaFestivo diasFestivos[] = {
				new DiaFestivo( "Año Nuevo",                  new Fecha( 1, 1 ), false ),
				new DiaFestivo( "Día de la Constitución",     new Fecha( 5, 2 ), false ),
				new DiaFestivo( "Natalicio de Benito Juárez", new Fecha( 21, 3 ), false ),
				new DiaFestivo( "Día del Trabajo",            new Fecha( 1, 5 ), false ),
				new DiaFestivo( "Día de la Independencia",    new Fecha( 16, 9 ), false ),
				new DiaFestivo( "Día de la Revolución",       new Fecha( 20, 11 ), false ),
				new DiaFestivo( "Navidad",                    new Fecha( 25, 12 ), false ),
				new DiaFestivo( "Día de Reyes",               new Fecha( 6, 1 ), false ),
		//LABORALES
				new DiaFestivo( "Día de la Candelaria",       new Fecha( 2, 2 ), true ),
				new DiaFestivo( "Día del Amor y la Amistad",  new Fecha( 14, 2 ), true ),
				new DiaFestivo( "Día de la Bandera",          new Fecha( 24, 2 ), true ),
				new DiaFestivo( "Día del Niño",               new Fecha( 30, 4 ), true ),
				new DiaFestivo( "Día de las Madres",          new Fecha( 10, 5 ), true ),
				new DiaFestivo( "Día del Padre",              new Fecha( 16, 6 ), true ),
				new DiaFestivo( "Día de Muertos",             new Fecha( 2, 11 ), true ),
				new DiaFestivo( "Virgen de Guadalupe",        new Fecha( 12, 12 ), true )
		}; 

		mapaDiasFestivos = new HashMap<Short, DiaFestivo>();
		for ( DiaFestivo diaFestivo : diasFestivos )
			mapaDiasFestivos.put( diaFestivo.getFecha().getClaveCalendario(), diaFestivo );
	}
	
	public DiaFestivo getDiaFestivo( Fecha fecha ) {
		return mapaDiasFestivos.get( fecha.getClaveCalendario() );
	}

}
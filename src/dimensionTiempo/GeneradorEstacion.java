package dimensionTiempo;

public class GeneradorEstacion {

	private static final ValorPorRangoFechas estaciones[] = { 
			new ValorPorRangoFechas("Primavera", new Fecha(21, 3), new Fecha(20, 6)),
		    new ValorPorRangoFechas("Verano",    new Fecha(21, 6), new Fecha(22, 9)),
		    new ValorPorRangoFechas("Otoño",     new Fecha(23, 9), new Fecha(20, 12)),
		    new ValorPorRangoFechas("Invierno",  new Fecha(21, 12), new Fecha(20, 3))
	};
	
	
	public String getEstacion(Fecha fecha) {
			for( ValorPorRangoFechas estacion : estaciones )
					if( estacion.perteneceRango( fecha ) )
						return estacion.getValor();
		return null;
	}
	
}
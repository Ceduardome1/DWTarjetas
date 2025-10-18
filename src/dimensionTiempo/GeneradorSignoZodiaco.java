package dimensionTiempo;

public class GeneradorSignoZodiaco {

		private static final ValorPorRangoFechas signos[] = { 
			new ValorPorRangoFechas( "Aries",       new Fecha( 21, 3 ), new Fecha( 19, 4 ) ),
			new ValorPorRangoFechas( "Tauro",       new Fecha( 20, 4 ), new Fecha( 20, 5 ) ),
			new ValorPorRangoFechas( "Géminis",     new Fecha( 21, 5 ), new Fecha( 20, 6 ) ),
			new ValorPorRangoFechas( "Cáncer",      new Fecha( 21, 6 ), new Fecha( 22, 7 ) ),
			new ValorPorRangoFechas( "Leo",         new Fecha( 23, 7 ), new Fecha( 22, 8 ) ),
			new ValorPorRangoFechas( "Virgo",       new Fecha( 23, 8 ), new Fecha( 22, 9 ) ),
			new ValorPorRangoFechas( "Libra",       new Fecha( 23, 9 ), new Fecha( 22, 10 ) ),
			new ValorPorRangoFechas( "Escorpio",    new Fecha( 23, 10 ), new Fecha( 21, 11 ) ),
			new ValorPorRangoFechas( "Sagitario",   new Fecha( 22, 11 ), new Fecha( 21, 12 ) ),
			new ValorPorRangoFechas( "Capricornio", new Fecha( 22, 12 ), new Fecha( 19, 1 ) ),
			new ValorPorRangoFechas( "Acuario",     new Fecha( 20, 1 ), new Fecha( 18, 2 ) ),
			new ValorPorRangoFechas( "Piscis",      new Fecha( 19, 2 ), new Fecha( 20, 3 ) )
		};

		public String getSigno( Fecha fecha ) {
				for( ValorPorRangoFechas signo : signos )
						if( signo.perteneceRango( fecha ) )
							return signo.getValor();
			return null;
		}
		
		public static void main( String args[] ) {
			GeneradorSignoZodiaco genSigno = new GeneradorSignoZodiaco(); 
			Fecha fecha = new Fecha("25/9/2025");
			System.out.println( genSigno.getSigno( fecha ) );
			System.out.println(fecha);
		}
}
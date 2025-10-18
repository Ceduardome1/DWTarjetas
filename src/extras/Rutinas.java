/*
 * EQUIPO: 
 * -MEZA ESCOBAR CÉSAR EDUARDO.
 * -ELENES TERRAZAS CÉSAR IMANOL.
 * ID: 22170724.
 * DOCENTE: DR.CLEMENTE GARCIA GERARDO. 	
 * ENTREGA: 09/09/25. 
 * DESCRIPCION: RUTINAS DE USO GENERAL.
 */
package extras;

import javax.swing.JOptionPane;

public class Rutinas {

	public static int NAleatorio(int inf, int sup) {
		return (int) (inf + (Math.random() * (sup - inf)));
	}
	
	public static String CadenaNumerica( String txt ) {
	    StringBuilder salida = new StringBuilder();
			for(int i=0; i<txt.length(); i++) {
				 char c = txt.charAt( i );
				if( Character.isDigit( c ) ) 
					salida.append( c );
			}
		return salida.toString();
	}
	
	public static String CadenaNumericaSinDecimales( String txt ) {
	    StringBuilder salida = new StringBuilder();
			for(int i=0; i<txt.length(); i++) {
				char car = txt.charAt(i);
					if( !Character.isDigit( car )  ) {
							if( car == '.' ) return salida.toString();
							else continue;
					}
				salida.append( car );
			}			
		return salida.toString();
	}
	
	public static String CadenaNumericaDecimal( String txt ) {
	    StringBuilder salida = new StringBuilder();
		boolean decimalEncontrado=false;
			for(int i=0; i<txt.length(); i++) {
				char car = txt.charAt(i);
					if( !Character.isDigit( car ) ) {
							if( !decimalEncontrado && car == '.' )
								decimalEncontrado = true;
							else continue;
					}
				salida.append(car);
			}			
		return salida.toString();
	}
	
	public static boolean EsCadenaNumerica(String txt){
		for(int i=0; i<txt.length(); i++)
			if(txt.charAt(i)<48 || txt.charAt(i)>57) return false;
		return true;
	}
	
	public static String ConvertirFechaSQL( String fecha ) {
	//SE ESPERA EL FORMATO dd-mm-yyyy. Y LA SALIDA SERÁ yyyy-mm-dd
			if( fecha.length() != 10 ) {
				throw new IllegalArgumentException(
					"\nLa fecha "+ fecha +"debe contener al menos 8 cáracteres.\nSe espera el siguiente formato de fecha: dd-mm-yyyy"
				);
			}
			
		String dia = fecha.substring( 0, 2 );
		String mes = fecha.substring( 3, 5 );
		String año = fecha.substring( 6 );
		
		if( !EsCadenaNumerica(dia) )
			throw new IllegalArgumentException( "\nEl dia ingresado: "+dia+", debe ser númerico.");
		if( !EsCadenaNumerica(mes) )
			throw new IllegalArgumentException( "\nEl mes ingresado: "+mes+", debe ser númerico.");
		if( !EsCadenaNumerica(año) )
			throw new IllegalArgumentException( "\nEl año ingresado: "+año+", debe ser númerico.");
	
		int nDia = Integer.parseInt( dia );
		int nMes = Integer.parseInt( mes );
		int nAño = Integer.parseInt( año );
		
		int diasMeses[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
			if( nDia > diasMeses[ nMes - 1 ] 
			&& !( nMes == 2 && nAño % 4 == 0 && nDia == diasMeses[ 1 ] + 1 )
			) throw new IllegalArgumentException( "\nEl dia del año ingresado no corresponde a su mes.");
		
		return año + "-" + mes + "-" + dia ;
		
	}
	
	public static void MensajeError( String msg ) {
		JOptionPane.showMessageDialog( null, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	public static void Mensaje( String titulo, String msg ) {
		JOptionPane.showMessageDialog( null, msg, titulo, JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static int Confirmacion( String msg ) {
		return JOptionPane.showConfirmDialog( null, msg );
	}
	
}
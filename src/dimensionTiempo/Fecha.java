package dimensionTiempo;

public class Fecha {

	private static final byte diasMeses[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
	private static final char separador = '/';
	
	private final String fecha; //?d/?m/yyyy
	private byte dia;
	private byte mes;
	private short año;
	private boolean esBisiesto;
	
	public Fecha( String fecha ) throws IllegalArgumentException {
		
		this.fecha=fecha;
			{
				int nFecha = fecha.length();
					if( nFecha < 8 || nFecha > 10  )
						throw new IllegalArgumentException("Formato de fecha incorrecto, se esperába 'dd/mm/yyyy'");
			}	
			
		validarPartesFecha();
		calcularAtributosFecha();

	}
	
	public Fecha( int dia, int mes ) {
		this.fecha=null;
		this.dia=(byte)dia;
		this.mes=(byte)mes;
		this.año=0;
	}

	private void validarPartesFecha() {
		String partes[] = fecha.split("\\"+separador);
			if( partes.length != 3 )
				throw new IllegalArgumentException("Formato de fecha incorrecto, se esperába 'dd/mm/yyyy'");
					
		dia = Byte.parseByte( partes[0] );
		mes = Byte.parseByte( partes[1] );
			if( mes > 12 ) mes = 12;
			else  if ( mes < 1 ) mes = 1;
			
		año = Short.parseShort( partes[2] );
	}
	
	private void calcularAtributosFecha() throws IllegalArgumentException {
		esBisiesto = año % 4 == 0 && año % 1000 != 0;
			if( dia > diasMeses[ mes - 1 ] )	
				dia =  ( mes == 2 && esBisiesto )?
						(byte) ( diasMeses[ 1 ] + 1 ) : (byte) diasMeses[ mes - 1 ];
	}
	
	public String getFecha() {
		return fecha;
	}

	public byte getDia() {
		return dia;
	}

	public byte getMes() {
		return mes;
	}

	public short getAño() {
		return año;
	}
	
	public boolean esBisiesto() {
		return esBisiesto;
	}
	
	public boolean esQuincena() {
		
		return false;
	}
	
	public short getClaveCalendario() {
		return (short) (mes*100 + dia);
	}
	
	public int compareTo( Fecha otra ) {
	    if (this.año < otra.año) return -1;
	    if (this.año > otra.año) return 1;
	    
	    if (this.mes < otra.mes) return -1;
	    if (this.mes > otra.mes) return 1;
	    
	    if (this.dia < otra.dia) return -1;
	    if (this.dia > otra.dia) return 1;
	    
	    return 0;
	}
	
	public String toString() {
		return String.format( "%04d%02d%02d", año, mes, dia ); //yyyymmdd
	}

}
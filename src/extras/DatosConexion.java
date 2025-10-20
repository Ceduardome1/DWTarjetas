/*
 * DOCENTE: DR.CLEMENTE GARCIA GERARDO. 	
 * ENTREGA: 24/09/25. 
 * DESCRIPCION: INFO DE CADA FRAGMENTO.
 */
package extras;

import java.io.Serializable;

public class DatosConexion implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private final String host;
	private final short puerto;
	private final String BD;
	
	public DatosConexion( String host, short puerto, String BD ) {
		this.host = host;
		this.puerto = puerto;
		this.BD=BD;
	}
	
	public DatosConexion(DatosConexion datos) {
		this.host = datos.host;
		this.puerto = datos.puerto;
		this.BD=datos.BD;
	}

	public String getHost() {
		return host;
	}
	
	public short getPuerto() {
		return puerto;
	}
	
	public String getBD() {
		return BD;
	}
	
}
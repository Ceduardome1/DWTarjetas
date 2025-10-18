/*
 * DOCENTE: DR.CLEMENTE GARCIA GERARDO. 	
 * ENTREGA: 24/09/25. 
 * DESCRIPCION: IMPLEMENTACION DE LA CLASE ABSTRACTA PARA SQL.
 */
package extras;
import java.sql.*;

public class ConexionSQL {
	
private Connection conexion;
private datosConexion datos;

	public ConexionSQL( datosConexion datos, String usuario, String pass ) throws ErrorConexion {
		this.datos = datos;
		NuevaConexion( datos.getHost(), datos.getPuerto(), datos.getBD(), usuario, pass );
	}
	
	public Connection getConecion() { return conexion; }

	public void NuevaConexion( String host, short puerto, String nomBD, String usuario, String contraseña ) throws ErrorConexion {
			if( conexion != null ) CerrarConexion();
		
		String url = "jdbc:sqlserver://" + host + ":" + puerto + ";databaseName=" + nomBD + ";";
		
			try {
				conexion = DriverManager.getConnection( url, usuario, contraseña );
			} catch (SQLException e) {
				throw new ErrorConexion( datos, e.toString() );
			}
			
		datos =	new datosConexion( host, puerto, nomBD );
	}
	
	public void CerrarConexion(){ 
			try {
				conexion.close();
			} catch (SQLException e) {
				Rutinas.MensajeError(e.toString() );
			} 
	}
		
	public void IniciarTransaccion() throws ErrorConexion {
			try {
				conexion.setAutoCommit( false ); 
		
			} catch (SQLException e) {
				throw new ErrorConexion( datos, 
					"Error al iniciar la transacción:\n"
					+ e.getMessage() 
				);
			}
	}

	public void ConfirmarTransaccion() throws ErrorConexion {
			try {
				conexion.commit();

			} catch (SQLException e) {

				DeshacerTransaccion();
				throw new ErrorConexion( datos, 
					"Error al confirmar la transacción:\n"
					+ e.getMessage()
				);
			}
	}

	public void DeshacerTransaccion() {
		try {
			conexion.rollback();
		} catch ( SQLException e ) {
			Rutinas.MensajeError( "Error al deshacer la transacción:\n"+ e.getMessage());
		}
	}
		
	protected ResultSet ConsultarRegistros( String consulta ) throws ErrorConexion {
		Statement st;
		ResultSet rs = null;
			try {
				st = conexion.createStatement();
				rs = st.executeQuery( consulta );
			} catch (SQLException e) {
				throw new ErrorConexion( datos, e.toString() );
			}
		return rs;
	}
	
	public void CerrarResultSet( ResultSet rs ) throws ErrorConexion {
		try {
			Statement stmt = rs.getStatement();
			rs.close();
			stmt.close();
		} catch( SQLException e ) {
			throw new ErrorConexion( datos, e.toString() );
		}
	}
	
	public void Insertar( String consulta ) throws ErrorConexion {
		ModificarRegistros( consulta );
	}

	public int Actualizar(String consulta) throws ErrorConexion {
		return ModificarRegistros( consulta );
	}

	public int Eliminar(String consulta) throws ErrorConexion {
		return ModificarRegistros( consulta );
	}
	
	private int ModificarRegistros( String consulta ) throws ErrorConexion {
			try {
				return conexion.createStatement().executeUpdate( consulta );
			} catch (SQLException e) {
				DeshacerTransaccion();
				throw new ErrorConexion( datos, e.toString() );
			}
	}
	
}
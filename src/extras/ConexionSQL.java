/*
 * DOCENTE: DR.CLEMENTE GARCIA GERARDO. 	
 * ENTREGA: 24/09/25. 
 * DESCRIPCION: IMPLEMENTACION DE LA CLASE ABSTRACTA PARA SQL.
 */
package extras;
import java.sql.*;

public class ConexionSQL {
	
private Connection conexion;
private DatosConexion datos;

	public ConexionSQL( DatosConexion datos, String usuario, String pass ) throws SQLException {
		NuevaConexion( datos, usuario, pass );
	}
	
	public Connection getConecion() { return conexion; }

	public void NuevaConexion( DatosConexion datos, String usuario, String contraseña ) throws SQLException {
			if( conexion != null ) CerrarConexion();
		
		String url = "jdbc:sqlserver://" + datos.getHost() + ":" + datos.getPuerto() + ";databaseName=" + datos.getBD()
		+ ";encrypt=true;trustServerCertificate=true;";
	
		this.datos = new DatosConexion( datos );

		conexion = DriverManager.getConnection( url, usuario, contraseña );
	
	}
	
	public void CerrarConexion() { 
			try {
				conexion.close();
			} catch (SQLException e) {
				Rutinas.MensajeError(e.toString() );
			} 
	}
		
	public void IniciarTransaccion() throws SQLException {
		conexion.setAutoCommit( false ); 
	}

	public void ConfirmarTransaccion() throws SQLException {
			conexion.commit();
	}

	public void DeshacerTransaccion() {
		try {
			conexion.rollback();
		} catch ( SQLException e ) {
			Rutinas.MensajeError( "Error al deshacer la transacción:\n"+ e.getMessage());
		}
	}
		
	public ResultSet ConsultarRegistros( String consulta )  throws SQLException {

		Statement st;
		ResultSet rs = null;
		st = conexion.createStatement();
		rs = st.executeQuery( consulta );

		return rs;
	}
	
	public void CerrarResultSet( ResultSet rs ) throws SQLException {
		Statement stmt = rs.getStatement();
		rs.close();
		stmt.close();
	}
	
	public void Insertar( String consulta ) throws SQLException {
		ModificarRegistros( consulta );
	}

	public int Actualizar(String consulta)  throws SQLException {
		return ModificarRegistros( consulta );
	}

	public int Eliminar(String consulta) throws SQLException {
		return ModificarRegistros( consulta );
	}
	
	private int ModificarRegistros( String consulta ) throws SQLException {
			try {

				return conexion.createStatement().executeUpdate( consulta );
			} catch (SQLException e) {
				DeshacerTransaccion();
				throw new SQLException( e );
			}
	}

	public DatosConexion getDatos() {
		return datos;
	}
	
}
/*
 * EQUIPO: 
 * -MEZA ESCOBAR CÉSAR EDUARDO.
 * -ELENES TERRAZAS CÉSAR IMANOL.
 * -MACIAS BUSTAMANTE JAIME.
 * ID: 22170724.
 * DOCENTE: DR.CLEMENTE GARCIA GERARDO. 	
 * ENTREGA: 23/10/25. 
 * DESCRIPCION: ANALIZAR NOMBRES PARA UNIFICAR REGISTROS SEMANTICAMENTE DUPLICADOS.
 */
package limpieza;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

import extras.ConexionSQL;
import extras.DatosConexion;

public class AppLimpiezaProfunda {

	private static final String saltoLinea = "---------------------------------------------------------------------------------------------------------------------------------------------------------------";
	private static final String host="localhost", bd = "DWTarjetas", user="cesar", pass="1234";
	private static final short puerto = 1433;
	
	private static final int nPartesNombres = 3;
	
	private static final String tablaObjetivos = "Objetivos";
	
	private static final String tablaSolicitudes = "Solicitudes", tablaClientes = "Clientes",
		tablaEmpleados="Empleados", tablaControlIds = "ControlIds";
	
	private static final String[]
	    camposObjetivos = {
	        "objFecha",
	        "objEmpId",
	        "objMeta",
	        "objColocaciones"
	    },
	    camposControlIds = {
	    		"idRef",
	    		"tipo",
	    		"nuevoId"
		},
	    camposEmpleados = {
	    	"empId",
	    	"empNombre",
	    	"empApellidoMat",
	    	"empApellidoPat",
	    	"empGenero"
	    },
	    camposClientes = {
	    	"clId",
	    	"clNombre",
	    	"clApellidoMat",
	    	"clApellidoPat"
		},
	    camposSolicitudes = {
	    	"solClId",
	    	"solEmpId"
		};

	private static ConexionSQL conexion;
	private static HashSet<Integer> listaEmpleadosDuplicados, listaClientesDuplicados;
	private static int nuevoIdAct;
	private static char tipoAct;
	
	private static String armarSentenciaActualizacion( String tabla, String[] campos, String[] valores ) {
		String salida = "UPDATE "+tabla+ "\nSET ";
			for ( int i = 0, n=campos.length-1; i <n ; i++ )
				salida += campos[i] +" = " + valores[i]+", ";
		return salida + campos[campos.length-1] +" = " + valores[valores.length-1]+"\n";
	}
	private static String armarSentenciaActualizacion( String tabla, String campo, String valor ) {
		return "UPDATE "+tabla+ "\nSET "
			+ campo + " = " + valor +"\n";
	}
	
	private static String armarSentenciaEliminacion( String tabla, String campo, String valor ) {
		return "DELETE FROM "+tabla+"\n"
			+ "WHERE "+ campo + " = " + valor;
	}
	
	private static String armarSentenciaInsercion( String tabla, String[] campos, String[] valores ) {
		return "INSERT INTO " + tabla + 
			" ( " + String.join(", ", campos) + " )\nVALUES ( "+ String.join(", ", valores) +" )";
	}
	
	private static String armarSentenciaSeleccion( String tabla, String[] campos ) {
		return "SELECT "+ String.join(", ", campos) +" FROM " + tabla;
	}

	private static void limpiarNombresDuplicados() throws SQLException {
		listaEmpleadosDuplicados = new HashSet<Integer>();
		listaClientesDuplicados = new HashSet<Integer>();
		
		String[] nomCamposClientes = new String[] { "clNombre", "clApellidoMat", "clApellidoPat" },
			nomCamposEmpleados = new String[] { "clNombre", "clApellidoMat", "clApellidoPat" };
		
		tipoAct='C';
		analizarNombres( tablaClientes, camposClientes[0], nomCamposClientes, listaClientesDuplicados );
		eliminarNombresDuplicados( tablaClientes, camposClientes[0], listaClientesDuplicados );

		tipoAct='E';
		analizarNombres( tablaEmpleados, camposEmpleados[0], nomCamposEmpleados, listaEmpleadosDuplicados );
		eliminarNombresDuplicados( tablaEmpleados, camposEmpleados[0], listaEmpleadosDuplicados );
	}
	
	private static void analizarNombres( String tabla, String nomCampoId, String[] nomCamposClientes, HashSet<Integer> listaDuplicados ) throws SQLException {
		nuevoIdAct = 0;
		String consulta = armarSentenciaSeleccion( tabla, new String[] {nomCampoId} );

		ResultSet rs = conexion.ConsultarRegistros( consulta );

			while( rs.next() ) {
				
				int idAct = rs.getInt( nomCampoId );
				
					if( listaDuplicados.contains( idAct) )
						continue;
				
				listarDuplicadosNombre( 
					nomCampoId, idAct,
					nomCamposClientes, tabla,
					listaDuplicados
				);
				
				nuevoIdAct++;
			}
	
		conexion.CerrarResultSet( rs );	
	}
	
	private static void listarDuplicadosNombre( String nomCampoId, int idAct, String nomCampos[], String tabla, HashSet<Integer> listaDuplicados )
	throws SQLException {
		String consulta = armarSentenciaSeleccion( tabla, nomCampos )
				+ "WHERE " + nomCampoId + " = " + idAct;

		String [] nombres = new String[nPartesNombres];

		ResultSet rs = conexion.ConsultarRegistros( consulta ); 

			if( rs.next() ) {
				for( int i=0; i< nPartesNombres; i++ )
					nombres[i] = rs.getString( nomCampos[i] );
			}

		conexion.CerrarResultSet( rs );
		int idDuplicado = getIdNombreDuplicado( tabla, nomCampos, nomCampoId, nombres, listaDuplicados );

			while( idDuplicado > -1 ){
				agregarNuevoId( idDuplicado, tipoAct, nuevoIdAct );
				listaDuplicados.add( idDuplicado );
				idDuplicado = getIdNombreDuplicado( tabla, nomCampos, nomCampoId, nombres, listaDuplicados );
			}

		listaEmpleadosDuplicados.clear();	 
	}

	private static int getIdNombreDuplicado( String tabla, String nomCampos[], String nomCampoId, String nombres[],  HashSet<Integer> listaDuplicados ) throws SQLException {
		int idDuplicado = -1;
		String nombresCmp = String.join( ", ", nombres );

		String consulta = "SELECT " + nomCampoId + " FROM "+ tablaClientes+"\n"
				+ "WHERE (\n"; 	
			for( int i=0, n= nomCampos.length-1; i<n; i++ )
				consulta += "( "+nomCampos[i] + "IN ("+nombresCmp+") ) +\n";
		consulta += "( "+nomCampos[nomCampos.length-1] 
				+ "IN ("+nombresCmp+")\n) = 3";    

		ResultSet rs = conexion.ConsultarRegistros( consulta );		
			if( rs.next() )
				idDuplicado = rs.getInt( nomCampoId );
			else {
				conexion.CerrarResultSet( rs );
				return -1;
			}

		conexion.CerrarResultSet( rs );

			if( listaDuplicados.contains( idDuplicado ) )
				return -1;
			
		return idDuplicado;
	}
			
	private static void eliminarNombresDuplicados( String tabla, String nomCampoId, HashSet<Integer> listaDuplicados ) {

			for( Integer id : listaDuplicados ) {
				
				eliminarNombreDuplicados( tabla, nomCampoId, id );
			}
	
	}
	
	private static void eliminarNombreDuplicados( String tabla, String nomCampoId, Integer valorId ) throws SQLException {
		String consulta = 
			armarSentenciaEliminacion( tabla, nomCampoId, ""+valorId );
		conexion.Eliminar( consulta );
	}

	private static void agregarNuevoId( int idAct, char tipo, int nuevoId ) throws SQLException {
		String consultaAcualizacion = armarSentenciaActualizacion( 
			tablaControlIds, "nuevoId", ""+nuevoId
		);
		consultaAcualizacion += "WHERE idRef = "+ idAct+" AND tipo = '"+tipo+"'";
		conexion.Actualizar( consultaAcualizacion );
	}

	private static void actualizarSolicitud( String nomCampoSolicitud, int idInicial, int idNuevo ) throws SQLException {
		String consultaAcualizacion = armarSentenciaActualizacion( 
				tablaSolicitudes, nomCampoSolicitud, ""+idNuevo
			);
			consultaAcualizacion += "WHERE " + nomCampoSolicitud + " = "+ idInicial;
			conexion.Actualizar( consultaAcualizacion );
	}
	
	private static void actualizarObjetivos(
		    String objFecha, String objEmpId, String objMeta, String objColocaciones
		) throws SQLException {

			
	}
	
	private static void eliminarDuplicados( String nomCampoId, int valorId, String tabla ) throws SQLException {
		String consultaEliminar = armarSentenciaEliminacion( tabla, nomCampoId, ""+valorId );
		conexion.Eliminar( consultaEliminar );
	}
	
	private static void contabilizarObjetivos() throws SQLException {
		
	}
	
	private static void asignarNuevosIds() throws SQLException {
		
	}
	
	public static void main ( String args[] ){
		
		conexion = null;
		
			try {
				
				conexion = new ConexionSQL( new DatosConexion( host, puerto, bd ), user, pass );				
				conexion.IniciarTransaccion();
				
				System.out.println("UNIFICAR NOMBRES:");
				limpiarNombresDuplicados();
				System.out.println( saltoLinea );
				
				conexion.ConfirmarTransaccion();
			
			}

			catch( SQLException sQLE ) {
				System.out.println("Ocurrio un error en la BD:");
				System.out.println( sQLE.toString() );
				System.out.println( saltoLinea );
					if( conexion!=null)
						conexion.DeshacerTransaccion();
				sQLE.printStackTrace();
			}
			
			catch ( Exception e ) {
				System.out.println("Ocurrio un error inesperado:");
				System.out.println( e.toString() );
				System.out.println( saltoLinea );
				e.printStackTrace();
				conexion.DeshacerTransaccion();
			}
			
			if( conexion!=null)
				conexion.CerrarConexion();
		
    }
	
}

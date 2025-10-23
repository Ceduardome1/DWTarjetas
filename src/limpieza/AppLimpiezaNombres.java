/*
 * EQUIPO: 
 * -MEZA ESCOBAR CÉSAR EDUARDO.
 * -ELENES TERRAZAS CÉSAR IMANOL.
 * -MACIAS BUSTAMANTE JAIME.
 * ID: 22170724.
 * DOCENTE: DR.CLEMENTE GARCIA GERARDO. 	
 * ENTREGA: 23/10/25. 
 * DESCRIPCION: ANALIZAR NOMBRES PARA UNIFICAR NOMBRES SEMANTICAMENTE DUPLICADOS.
 */
package limpieza;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import extras.ConexionSQL;
import extras.DatosConexion;

public class AppLimpiezaNombres {

	private static final String saltoLinea = "---------------------------------------------------------------------------------------------------------------------------------------------------------------";
	private static final String host="localhost", bd = "DWTarjetas", user="sa", pass="gestion8.0";
	private static final short puerto = 1433;
	
	private static final int nPartesNombres = 3;
		
	private static final String tablaClientes = "Clientes",
		tablaEmpleados="Empleados", tablaControlIds = "ControlIds";
	
	private static final String[]
	    camposControlIds = {
	    	"idInicial",
	    	"tipo",
	    	"idFinal"
		},
	    camposEmpleados = {
	    	"empId",
	    	"empNombre",
	    	"empApellidoPat",
	    	"empApellidoMat",
	    },
	    camposClientes = {
	    	"clId",
	    	"clNombre",
	    	"clApellidoPat",
	    	"clApellidoMat"
		};

	private static ConexionSQL conexion;
	private static HashSet<Integer> listaEmpleadosDuplicados, listaClientesDuplicados, listaAnalizados;
	private static HashMap < Integer, String[] > partesNombresCoincidentes;

	private static int nuevoIdAct;
	private static char tipoAct;

	private static String armarSentenciaActualizacion( String tabla, String campo, String valor ) {
		return "UPDATE "+tabla+ "\nSET "
			+ campo + " = " + valor +"\n";
	}
	
	private static String armarSentenciaSeleccion( String tabla, String[] campos ) {
		return "SELECT "+ String.join(", ", campos) +" FROM " + tabla;
	}

	private static void limpiarNombresDuplicados() throws SQLException {
		listaEmpleadosDuplicados = new HashSet<Integer>();
		listaClientesDuplicados = new HashSet<Integer>();
		listaAnalizados = new HashSet<Integer>();
		partesNombresCoincidentes = new HashMap<Integer, String[]>();
		String[] nomCamposClientes = new String[] { camposClientes[1], camposClientes[2], camposClientes[3] },
			nomCamposEmpleados = new String[] { camposEmpleados[1], camposEmpleados[2], camposEmpleados[3] };
		
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
				
					if( listaAnalizados.contains( idAct) )
						continue;
				
				analizarNombre( 
					nomCampoId, idAct,
					nomCamposClientes, tabla,
					listaDuplicados
				);
				
				nuevoIdAct++;
			}
		
		conexion.CerrarResultSet( rs );	
		listaAnalizados.clear();
	}
	
	private static void analizarNombre( String nomCampoId, int idAct, String nomCampos[], String tabla, HashSet<Integer> listaDuplicados )
	throws SQLException {
		String consulta = armarSentenciaSeleccion( tabla, nomCampos )
			+ "\nWHERE " + nomCampoId + " = " + idAct;

		String [] nombres = new String[nPartesNombres];

		ResultSet rs = conexion.ConsultarRegistros( consulta ); 

			if( rs.next() ) {
				for( int i=0; i< nPartesNombres; i++ )
					nombres[i] = "'"+rs.getString( nomCampos[i] )+"'";
			}

		conexion.CerrarResultSet( rs );
		validarNombre( tabla, nomCampos, nombres, nomCampoId, idAct, listaDuplicados );

	}

	private static void validarNombre( String tabla, String nomCamposNombres[], String nombresAct[], String nomCampoId, Integer idAct, HashSet<Integer> listaDuplicados ) throws SQLException {
		String camposProyeccion = nomCampoId +", "
			+ String.join( ", ", nomCamposNombres );
		String nombresCmp = String.join( ", ", nombresAct );
		
		// OBTENER TODOS LOS NOMBRES QUE SON COMBINACIONES DEL ACTUAL.
		StringBuilder consulta = new StringBuilder();
		consulta.append("SELECT ").append(camposProyeccion)
		  	.append(" FROM ").append(tabla).append("\nWHERE ")
    		.append( nomCampoId + " <> "+idAct +"\nAND (\n" );

			for (int i = 0; i < nPartesNombres; i++) {
			    consulta.append( "  CASE WHEN " )
			    	.append( nomCamposNombres[i] )
			        .append( " IN (" )
			        .append( nombresCmp )
			        .append( ") THEN 1 ELSE 0 END" );
				    if (i < nomCamposNombres.length - 1)
				        consulta.append(" +\n");
				    else
				        consulta.append("\n");
			}
		consulta.append( ") = "+nPartesNombres );
		ResultSet rs = conexion.ConsultarRegistros( consulta.toString() );		
		
		boolean nombrePrincipalConRepeticiones
			= nombresAct[0].equals( nombresAct[1] )
			|| nombresAct[1].equals( nombresAct[2] );
			
			while( rs.next() ) {
				String partes[] = new String[] { 
					rs.getString( nomCamposNombres[0] ),
					rs.getString( nomCamposNombres[1] ),
					rs.getString( nomCamposNombres[2] )
				};
	
				boolean otroNombreConRepeticiones
					= partes[0].equals( partes[1] )
					|| partes[1].equals( partes[2] );
				
					if( !nombrePrincipalConRepeticiones
						&& otroNombreConRepeticiones )
						continue;
				
				int id = rs.getInt( nomCampoId );
				partesNombresCoincidentes.put( id, partes );
			} 
			
		conexion.CerrarResultSet( rs );

			if( partesNombresCoincidentes.isEmpty() ) {
				asignarNuevoId( idAct, tipoAct, nuevoIdAct );
				//listaAnalizados.add( idAct );
				return;
			}
				
System.out.println( "NOMBRES REPETIDOS:" );	
System.out.println( "PIVOTE: " + idAct + "->" + Arrays.toString( nombresAct ) );
for ( Map.Entry< Integer, String[]> e : partesNombresCoincidentes.entrySet() )
	System.out.println( e.getKey()+"->"+Arrays.toString( e.getValue() ) );
		
		partesNombresCoincidentes.put( idAct, nombresAct );		
		decidirIdNombreCorrecto( listaDuplicados );

System.out.println( saltoLinea );
	}
			
	private static void decidirIdNombreCorrecto( HashSet<Integer> listaDuplicados ) throws SQLException {
		Integer idOrdenCorrecto = null;
		String partes[];
		boolean coincidencia = false;

			Iterator< Map.Entry< Integer, String[] > > 
				iteradorPrincipal = partesNombresCoincidentes.entrySet().iterator();

			while( !coincidencia && iteradorPrincipal.hasNext() ) {
			    Map.Entry< Integer, String[] > entrada = iteradorPrincipal.next();
			    idOrdenCorrecto = entrada.getKey();
				partes = entrada.getValue();
				String primerosNombresAct = partes[0] + partes[1];
				String ultimosNombresAct = partes[1] + partes[2];
				
				Iterator< Map.Entry< Integer, String[] > >
					iteradorSecundario = partesNombresCoincidentes.entrySet().iterator();

					while( iteradorSecundario.hasNext() ) {
					    
						Map.Entry< Integer, String[] > 
					    	otraEntrada = iteradorSecundario.next();
					    		
					    	if( entrada == otraEntrada ) continue;
					    	
					    partes = otraEntrada.getValue();
						String primerosNombresOtro = partes[0] + partes[1];
						String ultimosNombresOtro = partes[1] + partes[2];
						
							if ( primerosNombresAct.equals( ultimosNombresOtro ) ) {
								idOrdenCorrecto = otraEntrada.getKey();
								coincidencia = true;
								break;
							} else if ( primerosNombresOtro.equals(ultimosNombresAct) ) {
								coincidencia = true;
								break;
							}
						
					}
			
			}
		
			for( Map.Entry< Integer, String[] > entrada : partesNombresCoincidentes.entrySet() ) {
				int idAct = entrada.getKey();
				asignarNuevoId( idAct, tipoAct, nuevoIdAct );
				listaAnalizados.add( idAct );
				
					if( idAct != idOrdenCorrecto )
						listaDuplicados.add( idAct );
else 
	System.out.println( "ELEGIDO: "+entrada.getKey()+"->"+Arrays.toString( entrada.getValue() ) );
					
			}
			
		partesNombresCoincidentes.clear();
	}
	
	private static void eliminarNombresDuplicados( String tabla, String nomCampoId, HashSet<Integer> listaDuplicados ) throws SQLException {
			if( listaDuplicados.isEmpty() ) {
System.out.println( tabla + " No contiene nombres duplicados");
System.out.println(saltoLinea);
				return;
			}
		
		String consulta = "DELETE FROM " + tabla
			+ "\nWHERE " + nomCampoId + " IN (" 
			+ listaDuplicados.stream()
			.map(String::valueOf)
			.collect(Collectors.joining(", ")) 
			+ ")";conexion.Eliminar( consulta );
	
System.out.println( consulta );
System.out.println(saltoLinea);
	}

	private static void asignarNuevoId( int idAct, char tipo, int nuevoId ) throws SQLException {
		String consultaAcualizacion = armarSentenciaActualizacion( 
			tablaControlIds, camposControlIds[2], ""+nuevoId
		);
		consultaAcualizacion += "WHERE "+camposControlIds[0]+" = "+ idAct+" AND "+camposControlIds[1]+" = '"+tipo+"'";
		conexion.Actualizar( consultaAcualizacion );
	}

	public static void main ( String args[] ){
		
		conexion = null;
		
			try {
				
				conexion = new ConexionSQL( new DatosConexion( host, puerto, bd ), user, pass );				
				conexion.IniciarTransaccion();
				
				System.out.println("LIMPIAR NOMBRES:");
				System.out.println( saltoLinea );
				limpiarNombresDuplicados();
				
				conexion.ConfirmarTransaccion();
			
			}

			catch( SQLException sQLE ) {
				System.out.println("Ocurrio un error en la BD:");
				System.out.println( sQLE.toString() );
				System.out.println( saltoLinea );
					if( conexion!=null)
						conexion.DeshacerTransaccion();
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
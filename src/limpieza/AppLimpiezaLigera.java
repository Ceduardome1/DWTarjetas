/*
 * EQUIPO: 
 * -MEZA ESCOBAR CÉSAR EDUARDO.
 * -ELENES TERRAZAS CÉSAR IMANOL.
 * -MACIAS BUSTAMANTE JAIME.
 * ID: 22170724.
 * DOCENTE: DR.CLEMENTE GARCIA GERARDO. 	
 * ENTREGA: 23/10/25. 
 * DESCRIPCION: PASAR AL ENTORNO RELACIONAL CON LIMPIEZA LIGERA Y GENERAR DIMENSION TIEMPO.
 */
package limpieza;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import dimensionTiempo.DimensionTiempo;
import dimensionTiempo.Fecha;
import dimensionTiempo.GeneradorAñoChino;
import dimensionTiempo.GeneradorEstacion;
import dimensionTiempo.GeneradorFestivos;
import dimensionTiempo.GeneradorSignoZodiaco;
import extras.ConexionSQL;
import extras.Rutinas;
import extras.DatosConexion;


public class AppLimpiezaLigera {
	
	private static final String saltoLinea = "---------------------------------------------------------------------------------------------------------------------------------------------------------------";
	private static final String host="localhost", bd = "DWTarjetas", user="sa", pass="gestion8.0";
	private static final short puerto = 1433;
	
	private static final int nCamposFuente= 9;
	private static final char separador = ',';
	
	private static String registro = null; 
	private static String[] campos = null, partesNombre;
		
	private static HashMap<String,Integer> mapaPaises, mapaRedes;
	private static HashSet<String> mapaFechas;
	private static Integer idPaisAcum, idRedAcum, idPaisAct, idRedAct, 
	idEmpAct, idClAct;

	private static boolean fueColocada;
	
	private static final String rutaFuente = "docs/fuentes/SOLICITUDES.CVS";
		
			private static final String tablaClientes = "Clientes", tablaEmpleados="Empleados", tablaSolicitudes = "Solicitudes", 
				tablaPaises="Paises", tablaRedes = "Redes", ControlIds = "ControlIds", tablaDimTiempo="dimensionTiempo", tablaObjetivos = "Objetivos";
			
	private static final String[] 
		    camposSolicitudes = {
		        "solIdTarjeta",
		        "solFecha",
		        "solColocada",
		        "solRedId",
		        "solPaisId",
		        "solClId",
		        "solEmpId"
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

		    camposPaises = {
		        "paisId",
		        "paisNom"
		    },

		    camposRedes = {
		        "redId",
		        "redNombre"
		    },
		    
		    camposControlIds = {
		    	"idRef",
		    	"tipo"
			},
		    
		    camposObjetivos = {
		    	"objFecha",
		    	"objEmpId",
		    	"objMeta",
		    	"objColocaciones"
			},
		    	    
		    camposDimTiempo = {
		    	"fecha",
		    	"dia",
		    	"mes",
		    	"año",
		    	"bimestre",
		    	"trimestre",
		    	"cuatrimestre",
		    	"semestre",
		    	"semanaAño",
		    	"diaAño",
		    	"quincenaAño",
		    	"quincenaMes",
		    	"esBisiesto",
		    	"esFestivo",
		    	"esFestivoLaboral",
		    	"esLaboral",
		    	"esQuincena",
		    	"festejo",
		    	"estacion",
		    	"signoZodiacal",
		    	"animalChino",
		    	"elementoChino"
			};
				
	private static String consultaInsercionSolicitudes, consultaInsercionEmpleados, 
	consultaInsercionClientes, consultaInsercionNombres, 
	consultaInsercionPaises,consultaInsercionRedes, consultaInsercionDimTiempo;
	
	private static ConexionSQL conexion;
	
	private static Fecha fechaAct;
	private static Calendar calendario;
	private static GeneradorSignoZodiaco generadorSignoZodiaco;
	private static GeneradorAñoChino generadorChino;
	private static GeneradorFestivos generadorFestivos;
	private static GeneradorEstacion generadorEstacion;

	private static BufferedReader IniciarBuffer( String ruta ) {
		
		try {
			
			BufferedReader bf = ValidarRuta( ruta );
			
			System.out.println("Archivo cargado exitosamente!");
			System.out.println(saltoLinea);
			return bf;
			
		} catch ( FileNotFoundException fNFE ) {
			System.out.println("Ruta proporcionada incorrecta!:\n"
		        + fNFE.getMessage() );
			
		} catch ( IOException iOE ) {
			System.out.println("Error de entrada o salida:\n"
		        + iOE.getMessage() );
			
		} catch ( Exception e) {
			System.out.println("Error desconocido:\n"
			  + e.getMessage() );
		}
		
	    System.out.println(saltoLinea);
	    System.exit(1);
		return null;
		
	}
	
	private static BufferedReader ValidarRuta( String ruta ) 
		throws FileNotFoundException, IOException {
		
		File archivo = new File( ruta );
		if( !archivo.exists() )
			throw new FileNotFoundException("El archivo proporcionado no existe.");
	
		if( archivo.isDirectory() )
			throw new FileNotFoundException("Se esperaba un archivo.");

		return new BufferedReader(  new FileReader( archivo ) );
	
	}
	
	
	private static void prepararInserciones() {
		campos = new String[ nCamposFuente ];
		consultaInsercionSolicitudes = 
			armarSentenciaInsercion( tablaSolicitudes, camposSolicitudes );

		consultaInsercionEmpleados = 
			armarSentenciaInsercion( tablaEmpleados, camposEmpleados );


		consultaInsercionClientes = 
			armarSentenciaInsercion( tablaClientes , camposClientes );

		consultaInsercionNombres = 
			armarSentenciaInsercion( ControlIds, camposControlIds );

		consultaInsercionPaises = 
			armarSentenciaInsercion( tablaPaises, camposPaises );

		consultaInsercionRedes = 
			armarSentenciaInsercion( tablaRedes, camposRedes );

		consultaInsercionDimTiempo = 
			armarSentenciaInsercion( tablaDimTiempo, camposDimTiempo );
	}
	
	private static String armarSentenciaActualizacion( String tabla, String[] campos, String[] valores ) {
		String salida = "UPDATE "+tabla+ "\nSET ";
			for ( int i = 0, n=campos.length-1; i <n ; i++ )
				salida += campos[i] +" = " + valores[i]+", ";
		return salida + campos[campos.length-1] +" = " + valores[valores.length-1]+"\n";
	}
	
	private static String armarSentenciaInsercion( String tabla, String[] campos ) {
		return "INSERT INTO " + tabla + 
			" ( " + String.join(", ", campos) + " )\nVALUES ( ";
	}
	
	private static void iniciarComponentes() throws IOException {
		calendario = Calendar.getInstance();
		generadorSignoZodiaco = new GeneradorSignoZodiaco();
		generadorChino = new GeneradorAñoChino();
		generadorFestivos = new GeneradorFestivos();
		generadorEstacion = new GeneradorEstacion();
		
		mapaPaises = new HashMap<String,Integer>();
		mapaRedes = new HashMap<String,Integer>();
		mapaFechas = new HashSet<String>();
		idPaisAcum=idRedAcum=idPaisAct=idRedAct=idEmpAct=idClAct=0;
	}
	
	private static void ExportarSolicitudes( BufferedReader bf ) throws IOException, SQLException {
		prepararInserciones();
		iniciarComponentes();
		
			while( ( registro=bf.readLine() )!= null ) {
				campos = registro.split( "\\" + separador );
				limpiarCampos();
				exportarCampos();
			}
			
		exportarCatalogo( consultaInsercionRedes, mapaRedes );	
		exportarCatalogo( consultaInsercionPaises, mapaPaises);	
		bf.close();		
	}
	
	private static void limpiarCampos(){
		// [ 0 ]: Red, [ 1 ]: Cliente, [ 2 ]: Pais, [ 3 ]: Fecha Solicitud, 
		// [ 4 ]: Empleado, [ 5 ]: Aceptada?, [ 6 ]: IdTarjeta, [ 7 ]: Objetivo, [8]: Genero
		campos[0] = campos[0].toUpperCase();
		idRedAct = null;
			if( ( idRedAct = mapaRedes.putIfAbsent( campos[0], idRedAcum ) ) == null ) {
				idRedAct = idRedAcum;
				idRedAcum++;
			}
	
		campos[1] = campos[1].toUpperCase(); 
			
		campos[2] = campos[2].toUpperCase();
		idPaisAct = null;
			if( ( idPaisAct = mapaPaises.putIfAbsent( campos[2], idPaisAcum ) ) == null ) {
				idPaisAct = idPaisAcum;
				idPaisAcum++;
			}
			
		fechaAct = new Fecha( campos[3] );
		
		campos[4] = campos[4].toUpperCase(); 
		fueColocada = campos[5].equalsIgnoreCase("Si");
		campos[5] = fueColocada ? "'Y'":"'N'";
		campos[6] = Rutinas.CadenaNumerica( campos[6] );
		campos[7] = Rutinas.CadenaNumerica( campos[7] );
		campos[8] = "'"+campos[8].charAt(0)+"'";
	}
	
	private static void exportarCampos() 
		throws SQLException {
		exportarSolicitudes();
		exportarClientes();
		exportarEmpleados();
	
			if( mapaFechas.add( fechaAct.toString() ) )
				exportarDimTiempo( 
					new DimensionTiempo( fechaAct, calendario, generadorFestivos )
				);
	}
	
	private static void exportarSolicitudes() throws SQLException {
		/*	SI HAY IDTARJETA + IDRED + IDPAIS REPETIDOS:
		SE DARÁ PRIORIDAD A LA QUE FUE COLOCADA.
		SI NINGUNA FUE COLOCADA NOS QUEDAMOS CON EL ULTIMO.
		 */
		String [] valoresSolicitudes = new String[] { 
				campos[6], "'"+fechaAct.toString()+"'", campos[5],
				""+idRedAct, ""+idPaisAct, ""+idClAct, ""+idEmpAct
			};
		
		String condicion = "WHERE solPaisId = " + idPaisAct
			+ " AND solRedId = " + idRedAct + " AND solIdTarjeta = "+campos[6];
		String consultaActualizar =  armarSentenciaActualizacion( tablaSolicitudes, camposSolicitudes, valoresSolicitudes ) + condicion;

			if( fueColocada ) { //MAXIMA PRIORIDAD.	
				actualizarSolicitud( consultaActualizar, valoresSolicitudes );
				return;
			}
		
	//VERIFICAR SI, NO FUE COLOCADA ANTES
		String consulta = "SELECT solColocada FROM SOLICITUDES\n"
			+ condicion;
			
		ResultSet rs = conexion.ConsultarRegistros( consulta );
			if( rs.next() ) {
				String valor = rs.getString("solColocada");
				System.out.println(valor);
				conexion.CerrarResultSet( rs );
				
				if( valor.equals("Y") )
					return; // <- DEJAR EL REGISTRO CON COLOCACIÓN.
				
				actualizarSolicitud( consultaActualizar, valoresSolicitudes );
			}
			else {
				conexion.CerrarResultSet( rs );
				exportarDatos( 
					consultaInsercionSolicitudes,
					valoresSolicitudes
				);
			}
	}
	
	private static void actualizarSolicitud( String consultaActualizar, String[] valores ) throws SQLException{
		System.out.println(consultaActualizar);
		System.out.println(saltoLinea);
		
		int filasAfectadas = conexion.Actualizar( consultaActualizar );
			if( filasAfectadas == 0 ) {
				exportarDatos( 
					consultaInsercionSolicitudes,
					valores
				);
			}
	}
	
	private static void partirNombre( String nombre ) throws SQLException {
		partesNombre = nombre.split( "\\" + ' ' );
			for (int i = 0; i < partesNombre.length; i++ )
				partesNombre[i]="'"+partesNombre[i]+"'";
	}
	
	private static void exportarClientes() throws SQLException {
		partirNombre( campos[1] );
			
		exportarDatos( 
			consultaInsercionClientes,
			new String[] { 
				""+idClAct, partesNombre[0], partesNombre[1], partesNombre[2]
			}
		);
		
		exportarDatos( 
				consultaInsercionNombres,
				new String[] { 
					""+idClAct, "'C'"
				}
			);
		idClAct++;
	}
	
	private static void exportarEmpleados() throws SQLException {
		partirNombre( campos[4] );
			
		exportarDatos( 
			consultaInsercionEmpleados,
			new String[] { 
				""+idEmpAct, partesNombre[0], partesNombre[1], 
				partesNombre[2], campos[8] 
			}
		);
		
		exportarDatos( 	
			consultaInsercionNombres,
			new String[] { 
				""+idEmpAct, "'E'"
			}
		);
		idEmpAct++;
	}
	
	
	private static void exportarCatalogo( 
			String consultaInsercion, HashMap< String, Integer > mapa 
	) throws SQLException {
		for ( Map.Entry<String, Integer> entry : mapa.entrySet() )
			exportarDatos( 
					consultaInsercion,
					new String[] { 
						""+entry.getValue(), "'"+entry.getKey()+"'"
					}
			);
	}

	private static void exportarDatos ( 
		String consultaInsercion, String[] valores 
	) throws SQLException {
		String consulta = consultaInsercion + String.join( ", ", valores ) + ");";
		
		System.out.println( consulta );
		System.out.println( saltoLinea );
		conexion.Insertar( consulta );
	}

	private static void exportarDimTiempo( 
		DimensionTiempo dt 
	) throws SQLException {
	    
		String[] valoresDimTiempo = {
				"'"+fechaAct.toString()+"'",
			    "'"+dt.getDia()+"'",
			    "'"+dt.getMes()+"'",
			    String.valueOf( dt.getAño() ),
			    "'"+dt.getBimestre()+"'",
			    "'"+dt.getTrimestre()+"'",
			    "'"+dt.getCuatrimestre()+"'",
			    "'"+dt.getSemestre()+"'",
			    "'"+dt.getSemanaAño()+"'",
			    String.valueOf( dt.getDiaAño() ),
			    "'"+dt.getQuincenaAño()+"'",
			    "'"+dt.getQuincenaMes()+"'",
			    "'"+dt.esBisiesto()+"'",
			    "'"+dt.esFestivo()+"'",
			    "'"+dt.esFestivoLaboral()+"'",
			    "'"+dt.esLaboral()+"'",
			    "'"+dt.esQuincena()+"'",
			    "'"+dt.getFestejo()+"'",
			    "'"+dt.getEstacion(generadorEstacion)+"'",
			    "'"+dt.getSignoZodiacal( generadorSignoZodiaco )+"'",
			    "'"+dt.getAnimalChino(generadorChino)+"'",
			    "'"+dt.getElementoChino(generadorChino)+"'"
			};
		
			exportarDatos( 
				consultaInsercionDimTiempo,
				valoresDimTiempo
			);
	}

	public static void main ( String args[] ){
		
		conexion = null;
		BufferedReader bf  = null;	
		
			try {
				
				conexion = new ConexionSQL( new DatosConexion( host, puerto, bd ), user, pass );
				bf = IniciarBuffer( rutaFuente );
				
				conexion.IniciarTransaccion();
				
				System.out.println("LECTURA DE SOLICITUDES:");
				System.out.println( saltoLinea );
				ExportarSolicitudes( bf );
				
				conexion.ConfirmarTransaccion();
			
			}
			
			catch ( IOException iOE ) {
				System.out.println("Ocurrio un error al leer los registros del buffer:");
				System.out.println( iOE.toString() );
				System.out.println( saltoLinea );
				iOE.printStackTrace();
				conexion.DeshacerTransaccion();
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
/*
 * EQUIPO: 
 * - MEZA ESCOBAR CÉSAR EDUARDO.
 * - ELENES TERRAZAS CÉSAR IMANOL.
 * - MACIAS BUSTAMANTE JAIME.
 * ID: 22170724.
 * DOCENTE: DR.CLEMENTE GARCIA GERARDO. 	
 * ENTREGA: 23/10/25. 
 * DESCRIPCION: PASAR AL ENTORNO RELACIONAL CON LIMPIEZA LIGERA Y GENERAR DIMENSION TIEMPO.
 */
package limpieza;
import java.io.*;
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


public class AppExportarDatos {
	
	private static final String saltoLinea = "---------------------------------------------------------------------------------------------------------------------------------------------------------------";
	private static final String host="localhost", bd = "DWTarjetas", user="sa", pass="gestion8.0";
	private static final short puerto = 1433;
	
	private static final int nCamposSolicitudes= 9, nCamposTransacciones = 4;
	private static final char separador = ',';
	
	private static String registro = null; 
	private static String[] campos = null, partesNombre;
		
	private static HashMap<String,Integer> mapaPaises, mapaRedes;
	private static HashSet<String> mapaFechas;
	private static Integer idPaisAcum, idRedAcum, idPaisAct, idRedAct, 
	idEmpAct, idClAct;
	
	private static final String dirFuentes="docs/fuentes/", rutaSolicitudes = dirFuentes+"SOLICITUDES.CVS", rutaTransacciones = dirFuentes+"transacciones.cvs";
		
	private static final String tablaClientes = "Clientes", tablaEmpleados="Empleados", 
		tablaSolicitudes = "Solicitudes", ControlIds = "ControlIds", tablaDimTiempo="dimensionTiempo",
		tablaTransacciones="Transacciones", tablaPaises="Paises", tablaRedes = "Redes";
		
	private static final String[] 
			camposTransacciones = {
				"tranIdTarjeta",
				"tranFecha",
				"tranPaisId",
				"tranImporte"	
			},
			
		    camposSolicitudes = {
		        "solIdTarjeta",
		        "solFecha",
		        "solColocada",
		        "solRedId",
		        "solPaisId",
		        "solClId",
		        "solEmpId",
		        "solObjetivo"
		    },

		    camposEmpleados = {
		        "empId",
		        "empNombre",
		        "empApellidoPat",
		        "empApellidoMat",
		        "empGenero"
		    },

		    camposClientes = {
		        "clId",
		        "clNombre",
		        "clApellidoPat",
		        "clApellidoMat",
		    },

		    camposControlIds = {
		    	"idInicial",
		    	"tipo"
			},
		      
		    camposDimTiempo = {
		    	"fecha",
		    	"numDia",
		    	"nomDia",
		    	"nomMes",
		    	"año",
		    	"bimestre",
		    	"trimestre",
		    	"cuatrimestre",
		    	"semestre",
		    	"semanaAño",
		    	"diaAño",
		    	"quincenaAño",
		    	"quincenaMes",
		    	"semanaMes",
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
			},
		    
		    camposPaises = {
		    		"paisId",
		    		"paisNombre"
			},

		    camposRedes = {
		    		"redId",
		    		"redNombre"
			};
				
	private static String consultaInsercionSolicitudes, consultaInsercionEmpleados, 
	consultaInsercionClientes, consultaInsercionControlIds, consultaInsercionDimTiempo,
	consultaInsercionTransacciones, consultaInsercionPaises, consultaInsercionRedes;
	
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
		consultaInsercionSolicitudes = 
			armarSentenciaInsercion( tablaSolicitudes, camposSolicitudes );

		consultaInsercionEmpleados = 
			armarSentenciaInsercion( tablaEmpleados, camposEmpleados );

		consultaInsercionClientes = 
			armarSentenciaInsercion( tablaClientes , camposClientes );

		consultaInsercionControlIds = 
			armarSentenciaInsercion( ControlIds, camposControlIds );

		consultaInsercionDimTiempo = 
			armarSentenciaInsercion( tablaDimTiempo, camposDimTiempo );
		
		consultaInsercionTransacciones = 
				armarSentenciaInsercion( tablaTransacciones, camposTransacciones );
		
		consultaInsercionPaises = 
			armarSentenciaInsercion( tablaPaises, camposPaises );

		consultaInsercionRedes = 
			armarSentenciaInsercion( tablaRedes, camposRedes );
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
		mapaPaises = new HashMap<String, Integer>();
		mapaRedes = new HashMap<String, Integer>();
		mapaFechas = new HashSet<String>();
		idRedAcum=idPaisAcum=idEmpAct=idClAct=0;
	}
	
	private static void exportarDatos ( 
		String consultaInsercion, String[] valores 
	) throws SQLException {
		String consulta = consultaInsercion 
			+ String.join( ", ", valores ) + ");";
		
		System.out.println( consulta );
		System.out.println( saltoLinea );
		conexion.Insertar( consulta );
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
	
	private static void ExportarSolicitudes( BufferedReader bf ) throws IOException, SQLException {
		campos = new String[ nCamposSolicitudes ];

			while( ( registro=bf.readLine() )!= null ) {
				campos = registro.split( "\\" + separador );
				limpiarCamposSolicitudes();
				exportarCamposSolicitudes();
			}
			
		bf.close();
	}
	
	private static void limpiarCamposSolicitudes(){
		// [ 0 ]: Red, [ 1 ]: Cliente, [ 2 ]: Pais, [ 3 ]: Fecha Solicitud, 
		// [ 4 ]: Empleado, [ 5 ]: Aceptada?, [ 6 ]: IdTarjeta, [ 7 ]: Objetivo, [8]: Genero
		campos[0] = campos[0].toUpperCase();
		idRedAct = null;
			if( ( idRedAct = mapaRedes.putIfAbsent( campos[0], idRedAcum ) ) == null ) {
				idRedAct = idRedAcum;
				idRedAcum++;
			}
		campos[0] = "'"+campos[0]+"'";

		campos[1] = campos[1].toUpperCase(); 
			
		campos[2] = campos[2].toUpperCase();
		idPaisAct = null;
			if( ( idPaisAct = mapaPaises.putIfAbsent( campos[2], idPaisAcum ) ) == null ) {
				idPaisAct = idPaisAcum;
				idPaisAcum++;
			}
		campos[2] = "'"+campos[2]+"'";

		fechaAct = new Fecha( campos[3] );
		
		campos[4] = campos[4].toUpperCase(); 
		
		campos[6] = Rutinas.CadenaNumerica( campos[6] ); //ID
		Integer idTarjeta = Integer.parseInt( campos[6] );
			
			if( idTarjeta == 0 ) {
				campos[6] = "NULL";
				campos[5] = "'N'";
			}
			else campos[5] = "'Y'";
		
		campos[7] = Rutinas.CadenaNumerica( campos[7] );
		campos[8] = "'"+campos[8].charAt(0)+"'";
	}
	
	private static void exportarCamposSolicitudes() 
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
	/*NO HAY IDS DE TARJETAS REPETIDOS MÁS QUE 0 = NO COLOCADA.*/
		
		String [] valoresSolicitudes = new String[] { 
				campos[6], "'"+fechaAct.toString()+"'", campos[5],
				""+idRedAct, ""+idPaisAct, ""+idClAct, ""+idEmpAct, campos[7]
		};

		exportarDatos( 
			consultaInsercionSolicitudes,
			valoresSolicitudes
		);
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
				consultaInsercionControlIds,
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
			consultaInsercionControlIds,
			new String[] { 
				""+idEmpAct, "'E'"
			}
		);
		idEmpAct++;
	}

	private static void exportarDimTiempo( 
		DimensionTiempo dt 
	) throws SQLException {
	    
		String[] valoresDimTiempo = {
				"'"+fechaAct.toString()+"'",
			    ""+dt.getNumDia(),
			    "'"+dt.getNomDia()+"'",
			    "'"+dt.getNomMes()+"'",
			    "'"+dt.getAño()+"'",
			    ""+dt.getBimestre(),
			    ""+dt.getTrimestre(),
			    ""+dt.getCuatrimestre(),
			    ""+dt.getSemestre(),
			    ""+dt.getSemanaAño(),
			    ""+dt.getDiaAño(),
			    ""+dt.getQuincenaAño(),
			    ""+dt.getQuincenaMes(),
			    ""+dt.getSemanaMes(),
			    "'"+dt.esBisiesto()+"'",
			    "'"+dt.esFestivo()+"'",
			    "'"+dt.esFestivoLaboral()+"'",
			    "'"+dt.esLaboral()+"'",
			    "'"+dt.esQuincena()+"'",
			    dt.getFestejo()== null? 
					null : "'"+dt.getFestejo()+"'",
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

	private static void ExportarTransacciones( BufferedReader bf ) throws IOException, SQLException {
		campos = new String[ nCamposTransacciones ];
			while( ( registro=bf.readLine() )!= null ) {
				campos = registro.split( "\\" + separador );
				limpiarCamposTransacciones();
				exportarCamposTransacciones();
			}
			
		bf.close();
	}
	
	private static void limpiarCamposTransacciones(){
	// [ 0 ]: idTarjeta, [ 1 ]: fecha, [ 2 ]: Pais, [3]: Importe. 
		campos[0] =  Rutinas.CadenaNumerica( campos[0] );

		fechaAct = new Fecha( campos[1] );

		campos[2] = campos[2].toUpperCase();
		idPaisAct = null;
			if( ( idPaisAct = mapaPaises.putIfAbsent( campos[2], idPaisAcum ) ) == null ) {
				idPaisAct = idPaisAcum;
				idPaisAcum++;
			}

		campos[3] = Rutinas.CadenaNumerica( campos[3] );
		
	}
	
	private static void exportarCamposTransacciones() 
		throws SQLException {
		exportarTransacciones();
	}
	
	
	private static void exportarTransacciones() throws SQLException {

			exportarDatos( 
				consultaInsercionTransacciones,
				new String[] { 
					campos[0], "'"+fechaAct.toString()+"'",
					""+idPaisAct, campos[3]
				}
			);
			
			if( mapaFechas.add( fechaAct.toString() ) )
				exportarDimTiempo( 
					new DimensionTiempo( fechaAct, calendario, generadorFestivos )
				);
		
	}

	private static void exportarCatalogos() throws SQLException {
		exportarCatalogo( consultaInsercionRedes, mapaRedes );	
		exportarCatalogo( consultaInsercionPaises, mapaPaises);	
	}
	
	public static void main ( String args[] ){
		
		conexion = null;
		BufferedReader bfSolicitudes  = null;	
		BufferedReader bfTransacciones  = null;
		prepararInserciones();
		
			try {
				
				iniciarComponentes();
				bfSolicitudes = IniciarBuffer( rutaSolicitudes );
				bfTransacciones = IniciarBuffer( rutaTransacciones );
				
				conexion = new ConexionSQL( new DatosConexion( host, puerto, bd ), user, pass );
				conexion.IniciarTransaccion();
				
				System.out.println("EXPORTAR SOLICITUDES:");
				System.out.println( saltoLinea );
				ExportarSolicitudes( bfSolicitudes );
				
				bfSolicitudes = IniciarBuffer( rutaSolicitudes );
				System.out.println("EXPORTAR TRANSACCIONES:");
				System.out.println( saltoLinea );
				ExportarTransacciones( bfTransacciones );
				
				System.out.println("EXPORTAR CATALOGOS:");
				System.out.println( saltoLinea );
				exportarCatalogos();
				
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
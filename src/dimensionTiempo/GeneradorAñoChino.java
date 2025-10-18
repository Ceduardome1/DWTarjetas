package dimensionTiempo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class GeneradorAñoChino {

	private static final String dir = "docs/añoChino/",		
	rutaAnimales = dir+"animales.csv",
	rutaFechas = dir+"fechas.csv";

    private String limInf;
    String limSup;
    private FileReader frFechas;
    private BufferedReader bfFechas;
    private FileReader frAnimales;
    private BufferedReader bfAnimales;
    private ArrayList<String> fechas;
    private ArrayList<String> elementos;
    private ArrayList<String> animales;
    
    public GeneradorAñoChino() throws IOException {
    	iniciarBufferFechas();
        cargarFechas();
        
        iniciarBufferAnimales();
        cargarAnimalesYElementos();
        
        limInf = fechas.getFirst();
        limSup = fechas.getLast();
    }
    
    public String animalChino( String fecha ) {

        // fecha 20221030

        int inicio = 0;
        int fin = animales.size() - 1;
        int medio;
        int resultado = 0;

        if (fecha.compareTo(limInf) < 0 
        || fecha.compareTo(limSup) > 0) {

            System.out.println("Fecha fuera de los rangos: "+fecha);
            return null;

        }

        while (inicio <= fin) {
            medio = inicio + (fin - inicio) / 2;

            if (fechas.get(medio).compareTo(fecha) == 0) {

                return animales.get(medio);

            } else if (fechas.get(medio).compareTo(fecha) < 0) {

                resultado = medio;
                inicio = medio + 1;

            } else {

                fin = medio - 1;
            }
        }

        return animales.get(resultado);

    }

    public String elementoChino( String fecha ) {
    	
        int inicio = 0;
        int fin = animales.size() - 1;
        int medio;
        int resultado = 0;

        while (inicio <= fin) {
            medio = inicio + (fin - inicio) / 2;

            if (fechas.get(medio).compareTo(fecha) == 0) {

                return elementos.get(medio);

            } else if (fechas.get(medio).compareTo(fecha) < 0) {

                resultado = medio;
                inicio = medio + 1;

            } else {

                fin = medio - 1;
            }
        }

        return elementos.get(resultado);

    }

    private void iniciarBufferFechas() throws IOException {

        try {
            frFechas = new FileReader(rutaFechas);
            bfFechas = new BufferedReader(frFechas);

        } catch (IOException e) {
        	throw new IOException( "No se pudo abrir el archivo de fechas correctamente.\n"+e.toString());
        }

    }

    private void iniciarBufferAnimales() throws IOException {

        try {
        	
            frAnimales = new FileReader(rutaAnimales);
            bfAnimales = new BufferedReader(frAnimales);

        } catch (IOException e) {
        	throw new IOException( "No se pudo abrir el archivo animales correctamente.\n"+e.toString());
        }

    }

    private void cargarFechas() throws IOException {

        String linea;

        fechas = new ArrayList<String>();

        try {

            while ( (linea = bfFechas.readLine()) != null ) {

                fechas.add(linea);

            }

            frFechas.close();
            bfFechas.close();
            
        } catch (IOException e) {
        	throw new IOException( "Error al cargar las fechas en el array.\n"+e.toString() );
        }

    }

    private void cargarAnimalesYElementos() throws IOException {

        String linea;
        String[] aux;
        elementos = new ArrayList<String>();
        animales = new ArrayList<String>();
        
	        try {
	
		            while ( (linea = bfAnimales.readLine()) != null ) {
		
		                aux = linea.split(",");
		                elementos.add(aux[0]);
		                animales.add(aux[1]);
		
		            }
	
	            frAnimales.close();
	            bfAnimales.close();
	            
	        } catch (IOException e) {
	        	throw new IOException( "Error al cargar los elementos y animales en le array.\n"+e.toString() );
	        }

    }

    public static void main(String[] args) {
    	
    	String fecha = "20041030";
        GeneradorAñoChino año = null;
			try {
				año = new GeneradorAñoChino();
			} catch ( IOException e ) {
				e.printStackTrace();
				System.exit(0);
			}

        System.out.println( año.elementoChino( fecha ) );
        System.out.println( año.animalChino( fecha ) );
        
    }

}
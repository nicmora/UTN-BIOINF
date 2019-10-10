package ar.frba.utn.bioinf;

import java.io.File;

public class Ejercicio1 {
	
	public static void main(String[] args) {

		System.out.println("---> Ejercicio 1 <---");
		try {
			String path_input = args[0];
			String name_input = args[1];
			String path_output = args[0];
			String name_output = "output.fasta";
			
			File file_input = new File(path_input);
			
			System.out.println("Stop");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: debe ejecutar la aplicacion indicando el path de entrada y salida.");
		}
		
	}

}

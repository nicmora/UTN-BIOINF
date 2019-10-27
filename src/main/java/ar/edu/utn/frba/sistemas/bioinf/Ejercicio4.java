package ar.edu.utn.frba.sistemas.bioinf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;
import org.biojava.nbio.core.sequence.loader.StringProxySequenceReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ejercicio4 {

	private static final Logger LOGGER = LoggerFactory.getLogger(Ejercicio4.class);

	private static final String PATH_INPUT = "data/input/";
	private static final String NAME_INPUT = "input_ej4.txt";

	public static void main(String[] args) {
		LOGGER.info("---> Ejercicio 4 <---");

		try {
			File inputFile = new File(PATH_INPUT + NAME_INPUT);

			if (!inputFile.exists()) {
				LOGGER.error("Error: No se pudo leer el archivo de entrada.");
				System.exit(0);
			}

			LOGGER.info("Cargando los datos del archivo.");
			
			String blastOutput;
			StringBuilder fileContents = new StringBuilder((int) inputFile.length());
			Scanner scanner = new Scanner((Readable) new BufferedReader(new FileReader(inputFile)));
			String lineSeparator = System.getProperty("line.separator");

			while (scanner.hasNextLine()) {
				fileContents.append(scanner.nextLine() + lineSeparator);
			}
			
			blastOutput = fileContents.toString();
				
			scanner.close();
			

			// Separamos cada hit en un String diferente
			String[] hits = blastOutput.split("ALIGNMENTS");
			hits = hits[1].split(">");

			String inputSequenceSeparator = StringUtils.repeat(lineSeparator, 4);

			// Eliminamos lineas en blanco redundantes
			for (int i = 0; i < hits.length; i++) {
//				if (hits[i].indexOf(inputSequenceSeparator) >= 0) {
//					hits[i] = hits[i].substring(0, hits[i].indexOf(inputSequenceSeparator));
//				}
				hits[i] = hits[i].replace("\n", "");
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String pattern = "";
			boolean huboResultados;

			while (true) {
				LOGGER.info("Ingrese un pattern para buscar: ");
				huboResultados = false;
				pattern = br.readLine();
				for (int i = 1; i < hits.length; i++) {
					if (StringUtils.containsIgnoreCase(hits[i], pattern)) {
						LOGGER.info("Accession = " + hits[i].substring(0, hits[i].indexOf(" ")));
						LOGGER.info("Hits = " + StringUtils.countMatches(hits[i], pattern));
						huboResultados = true;
					}
				}
				if (!huboResultados) {
					LOGGER.info("No hay resultados.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Se produjo un error en la ejecución.");
			System.exit(0);
		}

		LOGGER.info("Finalizó correctamente.");
	}

}

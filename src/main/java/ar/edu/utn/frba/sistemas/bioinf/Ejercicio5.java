package ar.edu.utn.frba.sistemas.bioinf;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ejercicio5 {

	private static final Logger LOGGER = LoggerFactory.getLogger(Ejercicio5.class);

	private static final String PATH_INPUT = "data/input/";
	private static final String NAME_INPUT = "input_ej5.fasta";
	private static final String PATH_OUTPUT = "data/output/";
	private static final String NAME_OUTPUT = "output_ej5.txt";

	private static final String PATH_TEMP_GETORF = "data/output/Ex4_temp_getorf/";
	private static final String PATH_TEMP_PATMATMOTIFS = "data/output/Ex4_temp_patmatmotifs/";

	public static void main(String[] args) {

		LOGGER.info("########## Ejercicio 5 ##########");

		// Obtengo las secuencias de aminoácidos posibles a partir de la secuencia de nucleótidos del ejercicio 1...
		createTempDirectory(PATH_TEMP_GETORF);

		Path inputPath = Paths.get(PATH_INPUT + NAME_INPUT);
		Path tempOutputGetorfPath = Paths.get(PATH_TEMP_GETORF);

		LOGGER.info("Creando los archivos orf temporales...");
		
		String getorfCommand = "getorf -sequence " + inputPath.toString()
//				+ " -table 0 -minsize 300 -maxsize 1000000 -find 0 -methionine -nocircular -reverse -flanking 100 -ossingle2 -osdirectory2 "
				+ " -minsize 300 -find 1 -ossingle2 -osdirectory2 "
				+ tempOutputGetorfPath.toString() + " -auto";

		executeCommand(getorfCommand);

		LOGGER.info("Analizando dominios...");
		
		// Por cada secuencia de aminoácidos obtenida hago un análisis de dominios...
		createTempDirectory(PATH_TEMP_PATMATMOTIFS);

		String patmatmotifsCommand = null;

		Path tempEachSequencePath;
		Path tempOutputMotifPath;

		for (String filename : getDirectoryFilenameList(PATH_TEMP_GETORF)) {
			tempEachSequencePath = Paths.get(PATH_TEMP_GETORF + filename);
			tempOutputMotifPath = Paths.get(PATH_TEMP_PATMATMOTIFS + filename);

			patmatmotifsCommand = "patmatmotifs -sequence " + tempEachSequencePath.toString() + " -outfile "
					+ tempOutputMotifPath.toString() + ".patmatmotifs -nofull -prune -rformat dbmotif -auto";
			executeCommand(patmatmotifsCommand);
		}

		// Reúno los resultados en un único archivo...
		LOGGER.info("Juntando resultados en un archivo...");
		
		List<Path> inputs = new ArrayList<Path>();

		for (String filename : getDirectoryFilenameList(PATH_TEMP_PATMATMOTIFS))
			inputs.add(Paths.get(PATH_TEMP_PATMATMOTIFS + filename));

		Path output = Paths.get(PATH_OUTPUT + NAME_OUTPUT);

		Charset charset = StandardCharsets.UTF_8;

		for (Path path : inputs) {
			List<String> lines;
			try {
				lines = Files.readAllLines(path, charset);
				Files.write(output, lines, charset, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		LOGGER.info("Eliminando directorios temporales...");
		
		// Elimino los directorios temporales...
		try {
			FileUtils.deleteDirectory(new File(PATH_TEMP_GETORF));
			FileUtils.deleteDirectory(new File(PATH_TEMP_PATMATMOTIFS));
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error("Se produjo un error en la ejecución.");
			System.exit(0);
		}

		LOGGER.info("Finalizó correctamente.");
	}

	private static void createTempDirectory(String path) {
		File file = null;

		try {
			file = new File(path);
			FileUtils.forceMkdir(file);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private static void executeCommand(String command) {
		StringBuffer output = new StringBuffer();
		Process p;

		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				output.append(line + System.getProperty("line.separator"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Se produjo un error en la ejecución.");
			System.exit(0);
		}
		System.out.println(output.toString());
	}

	private static List<String> getDirectoryFilenameList(String directoryPath) {
		File folder = new File(directoryPath);
		File[] listOfFiles = folder.listFiles();

		List<String> filenameList = new ArrayList<String>();

		for (int i = 0; i < listOfFiles.length; i++)
			if (listOfFiles[i].isFile())
				filenameList.add(listOfFiles[i].getName());

		return filenameList;
	}
}
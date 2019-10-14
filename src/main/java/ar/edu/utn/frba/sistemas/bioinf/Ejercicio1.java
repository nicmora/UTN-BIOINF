package ar.edu.utn.frba.sistemas.bioinf;

import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.RNASequence;
import org.biojava.nbio.core.sequence.io.FastaWriterHelper;
import org.biojava.nbio.core.sequence.io.GenbankReaderHelper;
import org.biojava.nbio.core.sequence.transcription.Frame;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ejercicio1 {

	private static final Logger LOGGER = LoggerFactory.getLogger(Ejercicio1.class);
	
	private static String path_input;
	private static String name_input;
	private static String path_output;
	private static String name_output;

	public static void main(String[] args) {
		LOGGER.info("---> Ejercicio 1 <---");

		try {
			// Define el path del input/output y nombre de los archivos
			defineFiles(args[0]);
			File file_input = new File(path_input);			

			// Leemos el archivo en formato genBank
			Map<String, DNASequence> dnaSequences = readGenBank(file_input);
			
			// Obtenemos una lista de secuencias de proteinas, a partir de los rna
			List<ProteinSequence> proteinSequences = translatorForORF(dnaSequences);
			
			// Creamos el archivo en formato Fasta y guardamos los datos
			File outputFile = createFileFasta();			
			writeFileFasta(outputFile, proteinSequences);
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Se produjo un error en la ejecución.");
			System.exit(0);
		}
		
		LOGGER.info("Finalizó correctamente.");
	}
	
	private static void defineFiles(String path) {
		try {
			LOGGER.info("Definiendo archivos de entrada y salida.");
			path_input = path;
			String[] aux = path_input.split("/");
			name_input = aux[aux.length-1];
			aux = Arrays.copyOf(aux, aux.length-1);
			path_output = String.join("/", Arrays.asList(aux));
			name_output = ("result_"+name_input+".fasta").replace(".gb", "");
		} catch(Exception e) {
			e.printStackTrace();
			LOGGER.error("Error: debe ejecutar la aplicacion indicando el path de entrada y salida.");
			System.exit(0);
		}
	}

	private static Map<String, DNASequence> readGenBank(File inputFile){
		LOGGER.info("Iniciando lectura del archivo: " + inputFile.toString());
		Map<String, DNASequence> dnaSequences = new LinkedHashMap<>();
		try {
			dnaSequences = GenbankReaderHelper.readGenbankDNASequence(inputFile);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error al leer el archivo en formato GenBank");
			System.exit(0);
		}
		return dnaSequences;
	}

	private static List<ProteinSequence> translatorForORF(Map<String, DNASequence> dnaSequences){
		LOGGER.info("Obteniendo secuencia de proteínas de los 6 ORF.");
		AtomicInteger count = new AtomicInteger(0);

		List<RNASequence> rnaSequences = new LinkedList<>();
		List<ProteinSequence> proteinSequences = new LinkedList<>();

		// Traduccion a ARN en los seis marcos de lectura posibles
		dnaSequences.values().stream().forEach(t -> {
			rnaSequences.add(t.getRNASequence(Frame.ONE));
			rnaSequences.add(t.getRNASequence(Frame.TWO));
			rnaSequences.add(t.getRNASequence(Frame.THREE));
			rnaSequences.add(t.getRNASequence(Frame.REVERSED_ONE));
			rnaSequences.add(t.getRNASequence(Frame.REVERSED_TWO));
			rnaSequences.add(t.getRNASequence(Frame.REVERSED_THREE));

			// Realiza la transcription de ARN a aminoacidos para cada rnaSequences
			rnaSequences.stream().forEach(rna -> proteinSequences.add(rna.getProteinSequence()));

			// Se agrega encabezado ficticio por cada secuencia para poder leer
			proteinSequences.stream().forEach(p -> p.setOriginalHeader(Integer.toString(count.getAndIncrement())));

		});
		
		return proteinSequences;
	}

	private static File createFileFasta() {
		LOGGER.info("Creando archivo: " + name_output);
		File outputFile = new File(path_output + "/" + name_output);

		try {
			outputFile.createNewFile();
		} catch (Exception e){
			e.printStackTrace();
			LOGGER.error("Error al crear el archivo en formato Fasta.");
			System.exit(0);
		}

		return outputFile;
	}

	private static void writeFileFasta(File outputFile, List<ProteinSequence> proteinSequence) {
		LOGGER.info("Guardando los datos.");
		try {
			FastaWriterHelper.writeProteinSequence(outputFile, proteinSequence);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error al guardar los cambios en el archivo Fasta.");
			System.exit(0);
		}

	}

}

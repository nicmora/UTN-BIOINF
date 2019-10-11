package ar.frba.utn.bioinf;

import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.RNASequence;
import org.biojava.nbio.core.sequence.io.GenbankReaderHelper;
import org.biojava.nbio.core.sequence.transcription.Frame;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
			
			// Iniciamos los files
			File file_input = new File(path_input);
//			File file_output = new File(path_output + "/" + name_output);
			
			Map<String, DNASequence> dnaSequences = readGenBank(file_input);
			List<RNASequence> rnaSequences = translatorForORF(dnaSequences);
			
			System.out.println("Stop");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: debe ejecutar la aplicacion indicando el path de entrada y salida.");
		}
	}
	
	private static void defineFiles(String path) {
		path_input = path;
		String[] aux = path_input.split("/");
		name_input = aux[aux.length-1];
		aux = Arrays.copyOf(aux, aux.length-1);
		path_output = String.join("/", Arrays.asList(aux));
		name_output = "result_"+name_input+".fasta";
		return;
	}

	private static Map<String, DNASequence> readGenBank(File inputFile){
		LOGGER.info("readGenBank("+ inputFile.toString() + ")");
		Map<String, DNASequence> dnaSequences = new LinkedHashMap<>();
		try {
			// Leer el archivo en formato GenBank, devuelve una secuencia de ADN
			dnaSequences = GenbankReaderHelper.readGenbankDNASequence(inputFile);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return dnaSequences;
	}

	private static List<RNASequence> translatorForORF(Map<String, DNASequence> dnaSequences){
		LOGGER.info("translatorForORF()");
		List<RNASequence> rnaSequences = new LinkedList<>();
		// Traduccion a ARN en los seis marcos de lectura posibles
		dnaSequences.values().stream().forEach(t -> {
			rnaSequences.add(t.getRNASequence(Frame.ONE));
			rnaSequences.add(t.getRNASequence(Frame.TWO));
			rnaSequences.add(t.getRNASequence(Frame.THREE));
			rnaSequences.add(t.getRNASequence(Frame.REVERSED_ONE));
			rnaSequences.add(t.getRNASequence(Frame.REVERSED_TWO));
			rnaSequences.add(t.getRNASequence(Frame.REVERSED_THREE));
		});
		return rnaSequences;
	}

	private List<ProteinSequence> getProteinSequence(RNASequence rnaSequence) {
		return null;
	}

}

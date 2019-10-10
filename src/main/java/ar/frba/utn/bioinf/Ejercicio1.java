package ar.frba.utn.bioinf;

import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.RNASequence;
import org.biojava.nbio.core.sequence.io.GenbankReaderHelper;
import org.biojava.nbio.core.sequence.transcription.Frame;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ejercicio1 {

	private static final Logger LOGGER = LoggerFactory.getLogger(Ejercicio1.class);


	public static void main(String[] args) {
		LOGGER.info("Ejercicio 1");
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

	private Map<String, DNASequence> readGenBank(File inputFile){
		LOGGER.info("readGenBank("+ inputFile.toString() + ")");
		Map<String, DNASequence> dnaSequences = new LinkedHashMap<>();
		try {
			dnaSequences = GenbankReaderHelper.readGenbankDNASequence(inputFile);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return dnaSequences;
	}

	private List<RNASequence> translatorForORF(Map<String, DNASequence> dnaSequences){
		LOGGER.info("translatorForORF()");
		List<RNASequence> rnaSequences = new LinkedList<>();
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

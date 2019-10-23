package ar.edu.utn.frba.sistemas.bioinf;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.RNASequence;
import org.biojava.nbio.core.sequence.io.FastaWriterHelper;
import org.biojava.nbio.core.sequence.io.GenbankReaderHelper;
import org.biojava.nbio.core.sequence.transcription.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ejercicio1 {

	private static final Logger LOGGER = LoggerFactory.getLogger(Ejercicio1.class);
	
	private static final String PATH_INPUT = "data/input/";
	private static final String NAME_INPUT = "input_ej1.gb";
	private static final String PATH_OUTPUT = "data/output/";
	private static final String NAME_OUTPUT = "output_ej1.txt";

	public static void main(String[] args) {
		LOGGER.info("########## Ejercicio 1 ##########");

		try {
			// Inicializando archivo input
			File file_input = new File(PATH_INPUT + NAME_INPUT);			

			// Leemos el archivo en formato genBank
			Map<String, DNASequence> dnaSequences = readGenBank(file_input);
			
			// Obtenemos una lista de secuencias de proteinas, a partir de los rna
			List<ProteinSequence> proteinSequences = translatorForORF(dnaSequences);
			
			// Creamos el archivo en formato fasta y guardamos los datos
			File outputFile = createFileFasta();			
			writeFileFasta(outputFile, proteinSequences);
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Se produjo un error en la ejecución.");
			System.exit(0);
		}
		
		LOGGER.info("Finalizó correctamente.");
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
		LOGGER.info("Creando archivo: " + NAME_OUTPUT);
		File outputFile = new File(PATH_OUTPUT + NAME_OUTPUT);

		try {
			outputFile.createNewFile();
		} catch (Exception e){
			e.printStackTrace();
			LOGGER.error("Error al crear el archivo de salida.");
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
			LOGGER.error("Error al guardar los cambios en el archivo de salida.");
			System.exit(0);
		}

	}

}

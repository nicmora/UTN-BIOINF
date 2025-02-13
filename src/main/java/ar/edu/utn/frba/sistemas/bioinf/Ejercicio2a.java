package ar.edu.utn.frba.sistemas.bioinf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;
import org.biojava.nbio.core.sequence.io.util.IOUtils;
import org.biojava.nbio.ws.alignment.qblast.BlastOutputFormatEnum;
import org.biojava.nbio.ws.alignment.qblast.BlastProgramEnum;
import org.biojava.nbio.ws.alignment.qblast.NCBIQBlastAlignmentProperties;
import org.biojava.nbio.ws.alignment.qblast.NCBIQBlastOutputProperties;
import org.biojava.nbio.ws.alignment.qblast.NCBIQBlastService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ejercicio2a {

	private static final Logger LOGGER = LoggerFactory.getLogger(Ejercicio2a.class);

	private static final String PATH_INPUT = "data/output/";
	private static final String NAME_INPUT = "output_ej1.fasta";
	private static final String PATH_OUTPUT = "data/output/";
	private static final String NAME_OUTPUT = "output_ej2a.blast";

	public static void main(String[] args) {
		LOGGER.info("########## Ejercicio 2a ##########");

		try {
			// Inicializando archivo input
			File inputFile = new File(PATH_INPUT + NAME_INPUT);

			// Leemos el archivo fasta y obtenemos las secuencias de aminoacidos
			Map<String, ProteinSequence> secuenciasDeAminoacidos = readFastaFile(inputFile);

			// Nos quedamos con la secuencia correcta
			LOGGER.info("Obteniendo la secuencia de proteína correcta.");
//			ProteinSequence proteinSequece = secuenciasDeAminoacidos.get(secuenciasDeAminoacidos.keySet().toArray()[1]);
			ProteinSequence proteinSequence = selectProtein(secuenciasDeAminoacidos);

			// Obtenemos los resultados del alineamiento del servicio de NCBI Blast
			InputStream inputStream = callNCBIBlastService(proteinSequence);

			// Creamos el archivo de salida y guardamos los datos
			File outputFile = createBlastFile();
			writeBlastFile(outputFile, inputStream);
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Se produjo un error en la ejecución.");
			System.exit(0);
		}
		
		LOGGER.info("Finalizó correctamente.");
	}

	private static Map<String, ProteinSequence> readFastaFile(File inputfile) {
		LOGGER.info("Leyendo secuencia de proteínas del archivo en formato fasta.");
		Map<String, ProteinSequence> secuenciasDeAminoacidos = new LinkedHashMap<String, ProteinSequence>();
		
		try {
			secuenciasDeAminoacidos = FastaReaderHelper.readFastaProteinSequence(inputfile);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error al leer el archivo de entrada");
			System.exit(0);
		}

		return secuenciasDeAminoacidos;
	}
	
	private static ProteinSequence selectProtein(Map<String, ProteinSequence> secuenciasDeAminoacidos) {
		ProteinSequence proteinSequence = null;
		int maxLength = 0;
		
		for (Map.Entry<String, ProteinSequence> entry : secuenciasDeAminoacidos.entrySet()) {
			String proteinSequenceAux = entry.getValue().getSequenceAsString();
			Character firstProtein = proteinSequenceAux.charAt(0);
			int length = proteinSequenceAux.indexOf('*');
			
			if(firstProtein.equals('M') && length > maxLength) {
				maxLength = length;
				proteinSequence = entry.getValue();
			}
		}
		
		return proteinSequence;
	}

	private static InputStream callNCBIBlastService(ProteinSequence proteinSequece) {
		LOGGER.info("Llamando al servicio de NCBI para realizar el alineamiento. Por favor, esperar..");
		NCBIQBlastService service = new NCBIQBlastService();
		
		// Confiruacion del servicio
		NCBIQBlastAlignmentProperties props = new NCBIQBlastAlignmentProperties();
		props.setBlastProgram(BlastProgramEnum.blastp);
		props.setBlastDatabase("swissprot");

		// Formato del resultado
		NCBIQBlastOutputProperties outputProps = new NCBIQBlastOutputProperties();
		outputProps.setOutputFormat(BlastOutputFormatEnum.Text);

		InputStream inputStream = null;
		
		try {
			String requestId = service.sendAlignmentRequest(proteinSequece.getSequenceAsString(), props);
			
			while (!service.isReady(requestId)) {
				Thread.sleep(5000);
			}

			inputStream = service.getAlignmentResults(requestId, outputProps);

			service.sendDeleteRequest(requestId);

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error al comunicarse con el servicio de NCBIQBlast.");
			System.exit(0);
		}
		
		return inputStream;
	}

	private static File createBlastFile() {
		LOGGER.info("Creando archivo: " + NAME_OUTPUT);
		File outputFile = new File(PATH_OUTPUT + NAME_OUTPUT);

		try {
			outputFile.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error al crear el archivo de salida.");
			System.exit(0);
		}

		return outputFile;
	}

	private static void writeBlastFile(File outputFile, InputStream inputStream) {
		LOGGER.info("Guardando los datos.");
		FileWriter writer = null;
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new InputStreamReader(inputStream));
			writer = new FileWriter(outputFile);
			
			String line;
			while ((line = reader.readLine()) != null) {
				writer.write(line + System.getProperty("line.separator"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error al guardar los cambios en el archivo de salida.");
			System.exit(0);
		} finally {
			IOUtils.close(writer);
			IOUtils.close(reader);
		}
	}

}

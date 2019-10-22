package ar.edu.utn.frba.sistemas.bioinf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
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

	private static String path_input;
	private static String name_input;
	private static String path_output;
	private static String name_output;

	public static void main(String[] args) {
		LOGGER.info("---> Ejercicio 2a <---");

		try {
			// Define el path del input/output y nombre de los archivos
			defineFiles(args[0]);
			File inputFile = new File(path_input);

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

	private static void defineFiles(String path) {
		try {
			LOGGER.info("Definiendo archivos de entrada y salida.");
			path_input = path;
			String[] aux = path_input.split("/");
			name_input = aux[aux.length - 1];
			aux = Arrays.copyOf(aux, aux.length - 1);
			path_output = String.join("/", Arrays.asList(aux));
			name_output = ("blast" + ".txt");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error: debe ejecutar la aplicacion indicando el path de entrada y salida.");
		}
	}

	private static Map<String, ProteinSequence> readFastaFile(File inputfile) {
		LOGGER.info("Leyendo secuencia de proteínas del archivo en formato Fasta.");
		Map<String, ProteinSequence> secuenciasDeAminoacidos = new LinkedHashMap<String, ProteinSequence>();
		
		try {
			secuenciasDeAminoacidos = FastaReaderHelper.readFastaProteinSequence(inputfile);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error al leer el archivo en formato Fasta");
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
		LOGGER.info("Creando archivo: " + name_output);
		File outputFile = new File(path_output + "/" + name_output);

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

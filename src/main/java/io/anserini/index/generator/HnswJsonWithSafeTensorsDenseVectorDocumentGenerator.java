package io.anserini.index.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default;

import io.anserini.collection.SourceDocument;
import io.anserini.index.Constants;
import io.anserini.index.IndexCollection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.BinaryDocValuesField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.KnnFloatVectorField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.VectorSimilarityFunction;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Converts a {@link SourceDocument} from SafeTensors into a Lucene
 * {@link Document}, ready to be indexed.
 *
 * @param <T> type of the source document
 */
public class HnswJsonWithSafeTensorsDenseVectorDocumentGenerator<T extends SourceDocument>
		extends DefaultLuceneDocumentGenerator<T> {
	private static final Logger LOG = LogManager.getLogger(HnswJsonWithSafeTensorsDenseVectorDocumentGenerator.class);
	protected IndexCollection.Args args;
	private HashSet<String> allowedFileSuffix;

	public HnswJsonWithSafeTensorsDenseVectorDocumentGenerator() {
		super();
		this.allowedFileSuffix = new HashSet<>(Arrays.asList(".json", ".jsonl", ".gz"));
		LOG.info("V1 Initializing HnswJsonWithSafeTensorsDenseVectorDocumentGenerator...");
	}

	public HnswJsonWithSafeTensorsDenseVectorDocumentGenerator(IndexCollection.Args args) {
		super(args);
		this.allowedFileSuffix = new HashSet<>(Arrays.asList(".json", ".jsonl", ".gz"));
		LOG.info("HnswJsonWithSafeTensorsDenseVectorDocumentGenerator initialized with arguments:");
		LOG.info(" - Input path: " + this.args.input);
		LOG.info(" - Allowed file suffixes: " + this.allowedFileSuffix);
	}

	public void setArgs(IndexCollection.Args args) {
		this.args = args;
		LOG.info("Args set via setter method:");
		LOG.info(" - Input path: " + this.args.input);
	}

	@Override
	public Document createDocument(T src) throws InvalidDocumentException {

		// Create the Lucene document

		// Contents of the document

		try {

			Path inputFolder = Paths.get(this.args.input);
			FilePaths filePaths = generateFilePaths(inputFolder);

			LOG.info("Generated file paths: ");
			LOG.info(" - Vectors: " + filePaths.vectorsFilePath);
			LOG.info(" - Docids: " + filePaths.docidsFilePath);
			LOG.info(" - Docid to Index: " + filePaths.docidToIdxFilePath);

			// Read and deserialize the SafeTensors files
			byte[] vectorsData = Files.readAllBytes(Paths.get(filePaths.vectorsFilePath));
			byte[] docidsData = Files.readAllBytes(Paths.get(filePaths.docidsFilePath));

			// Deserialize docid_to_idx.json
			ObjectMapper objectMapper = new ObjectMapper();
			@SuppressWarnings("unchecked")
			Map<String, Integer> docidToIdx = objectMapper
					.readValue(Files.readAllBytes(Paths.get(filePaths.docidToIdxFilePath)), Map.class);
			Map<Integer, String> idxToDocid = new LinkedHashMap<>();
			for (Map.Entry<String, Integer> entry : docidToIdx.entrySet()) {
				idxToDocid.put(entry.getValue(), entry.getKey());
			}

			// Deserialize docids
			Map<String, Object> docidsHeader = parseHeader(docidsData);
			int[] docidIndices = extractDocidIndices(docidsData, docidsHeader);
			String[] docids = new String[docidIndices.length];
			for (int i = 0; i < docidIndices.length; i++) {
				docids[i] = idxToDocid.get(docidIndices[i]);
			}

			// Deserialize vectors
			Map<String, Object> vectorsHeader = parseHeader(vectorsData);
			double[][] vectors = extractVectors(vectorsData, vectorsHeader);

			// Create the Lucene document
			String id = src.id();
			Integer indexObj = idxToDocid.entrySet().stream().filter(entry -> entry.getValue().equals(id)).findFirst()
					.orElse(null).getKey();
			if (indexObj == null) {
				throw new InvalidDocumentException();
			}
			int index = indexObj;
			float[] contents = new float[vectors[index].length];
			for (int i = 0; i < contents.length; i++) {
				contents[i] = (float) vectors[index][i];
			}

			final Document document = new Document();
			document.add(new StringField(Constants.ID, id, Field.Store.YES));
			document.add(new BinaryDocValuesField(Constants.ID, new BytesRef(id)));
			document.add(new KnnFloatVectorField(Constants.VECTOR, contents, VectorSimilarityFunction.DOT_PRODUCT));

			return document;

		} catch (Exception e) {
			throw new InvalidDocumentException();
		}
	}

	public FilePaths generateFilePaths(Path inputFolder) throws IOException {
		String inputFileName;
		try (Stream<Path> files = Files.list(inputFolder)) {
			inputFileName = files
					.filter(file -> allowedFileSuffix.stream().anyMatch(suffix -> file.getFileName().toString().endsWith(suffix)))
					.map(file -> file.getFileName().toString())
					.findFirst()
					.orElseThrow(() -> new IOException("No valid input file found in the directory"));
		}

		Path grandParent = inputFolder.getParent().getParent();
		Path parent = inputFolder.getParent();
		Path safetensorsFolder = Paths.get(grandParent.toString() + "/" + parent.getFileName().toString() + ".safetensors",
				inputFolder.getFileName().toString());

		String baseName = inputFileName.replace(".jsonl", "").replace(".json", "").replace(".gz", "");
		String vectorsFilePath = Paths.get(safetensorsFolder.toString(), baseName + "_vectors.safetensors").toString();
		String docidsFilePath = Paths.get(safetensorsFolder.toString(), baseName + "_docids.safetensors").toString();
		String docidToIdxFilePath = Paths.get(safetensorsFolder.toString(), baseName + "_docid_to_idx.json").toString();

		return new FilePaths(vectorsFilePath, docidsFilePath, docidToIdxFilePath);
	}

	public static class FilePaths {
		public String vectorsFilePath;
		public String docidsFilePath;
		public String docidToIdxFilePath;

		public FilePaths(String vectorsFilePath, String docidsFilePath, String docidToIdxFilePath) {
			this.vectorsFilePath = vectorsFilePath;
			this.docidsFilePath = docidsFilePath;
			this.docidToIdxFilePath = docidToIdxFilePath;
		}
	}

	private Map<String, Object> parseHeader(byte[] data) throws IOException {
		ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
		long headerSize = buffer.getLong();
		byte[] headerBytes = new byte[(int) headerSize];
		buffer.get(headerBytes);
		String headerJson = new String(headerBytes, StandardCharsets.UTF_8).trim();
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(headerJson, Map.class);
	}

	private double[][] extractVectors(byte[] data, Map<String, Object> header) {
		@SuppressWarnings("unchecked")
		Map<String, Object> vectorsInfo = (Map<String, Object>) header.get("vectors");
		String dtype = (String) vectorsInfo.get("dtype");

		@SuppressWarnings("unchecked")
		List<Integer> shapeList = (List<Integer>) vectorsInfo.get("shape");
		int rows = shapeList.get(0);
		int cols = shapeList.get(1);
		@SuppressWarnings("unchecked")
		List<Number> dataOffsets = (List<Number>) vectorsInfo.get("data_offsets");
		long begin = dataOffsets.get(0).longValue();

		ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
		buffer.position((int) (begin + buffer.getLong(0) + 8));

		double[][] vectors = new double[rows][cols];
		if (dtype.equals("F64")) {
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					vectors[i][j] = buffer.getDouble();
				}
			}
		} else {
			throw new UnsupportedOperationException("Unsupported data type: " + dtype);
		}

		return vectors;
	}

	private int[] extractDocidIndices(byte[] data, Map<String, Object> header) {
		@SuppressWarnings("unchecked")
		Map<String, Object> docidsInfo = (Map<String, Object>) header.get("docids");
		String dtype = (String) docidsInfo.get("dtype");

		@SuppressWarnings("unchecked")
		List<Integer> shapeList = (List<Integer>) docidsInfo.get("shape");
		int length = shapeList.get(0);

		@SuppressWarnings("unchecked")
		List<Number> dataOffsets = (List<Number>) docidsInfo.get("data_offsets");
		long begin = dataOffsets.get(0).longValue();

		ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
		buffer.position((int) (begin + buffer.getLong(0) + 8));

		int[] docidIndices = new int[length];
		if (dtype.equals("I64")) {
			for (int i = 0; i < length; i++) {
				docidIndices[i] = (int) buffer.getLong();
			}
		} else {
			throw new UnsupportedOperationException("Unsupported data type: " + dtype);
		}

		return docidIndices;
	}
}

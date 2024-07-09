package io.anserini.index.generator;

import io.anserini.collection.SourceDocument;
import io.anserini.index.AbstractIndexer;
import io.anserini.index.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.BinaryDocValuesField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.KnnFloatVectorField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.VectorSimilarityFunction;
import org.apache.lucene.util.BytesRef;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.stream.Stream;

public class HnswJsonWithSafeTensorsDenseVectorDocumentGenerator<T extends SourceDocument>
        implements LuceneDocumentGenerator<T> {
    private static final Logger LOG = LogManager.getLogger(HnswJsonWithSafeTensorsDenseVectorDocumentGenerator.class);
    protected AbstractIndexer.Args args;
    private HashSet<String> allowedFileSuffix;

    public HnswJsonWithSafeTensorsDenseVectorDocumentGenerator(AbstractIndexer.Args args) {
        this.args = args;
        this.allowedFileSuffix = new HashSet<>(Arrays.asList(".json", ".jsonl", ".gz"));
        LOG.info("Initializing HnswJsonWithSafeTensorsDenseVectorDocumentGenerator with Args...");
    }

    @Override
    public Document createDocument(T src) throws InvalidDocumentException {
        try {
            LOG.info("Input path for createDocument: " + this.args.input);

            if (this.args.input == null) {
                LOG.error("Input path is null");
                throw new InvalidDocumentException();
            }

            Path inputFolder = Paths.get(this.args.input);

            FilePaths filePaths = generateFilePaths(inputFolder);

            if (filePaths == null) {
                LOG.error("Error generating file paths");
                throw new InvalidDocumentException();
            }

            LOG.info("Generated file paths: ");
            LOG.info(" - Vectors: " + filePaths.vectorsFilePath);
            LOG.info(" - Docids: " + filePaths.docidsFilePath);

            if (filePaths.vectorsFilePath == null || filePaths.docidsFilePath == null) {
                LOG.error("Error generating file paths");
                throw new InvalidDocumentException();
            }

            // Read vectors and docids from safetensors
            double[][] vectors = readVectors(filePaths.vectorsFilePath);
            String[] docids = readDocidAsciiValues(filePaths.docidsFilePath);

            String id = src.id();
            LOG.info("Processing document ID: " + id);
            int index = Arrays.asList(docids).indexOf(id);

            if (index == -1) {
                LOG.error("Error finding index for document ID: " + id);
                LOG.error("Document ID ASCII: " + Arrays.toString(id.chars().toArray()));
                LOG.error("Available IDs ASCII: " + Arrays.deepToString(docids));
                throw new InvalidDocumentException();
            }

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
            LOG.error("Error creating document", e);
            LOG.error("trace: " + Arrays.toString(e.getStackTrace()));
            LOG.error("Document ID: " + src.id());
            LOG.error("Document contents: " + src.contents());
            LOG.error("Paths: " + this.args.input);

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

        return new FilePaths(vectorsFilePath, docidsFilePath);
    }

    public static class FilePaths {
        public String vectorsFilePath;
        public String docidsFilePath;

        public FilePaths(String vectorsFilePath, String docidsFilePath) {
            this.vectorsFilePath = vectorsFilePath;
            this.docidsFilePath = docidsFilePath;
        }
    }

    private double[][] readVectors(String filePath) throws IOException {
        byte[] data = Files.readAllBytes(Paths.get(filePath));
        Map<String, Object> header = parseHeader(data);
        return extractVectors(data, header);
    }

    private String[] readDocidAsciiValues(String filePath) throws IOException {
        byte[] data = Files.readAllBytes(Paths.get(filePath));
        Map<String, Object> header = parseHeader(data);
        return extractDocids(data, header);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> parseHeader(byte[] data) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        long headerSize = buffer.getLong();
        byte[] headerBytes = new byte[(int) headerSize];
        buffer.get(headerBytes);
        String headerJson = new String(headerBytes, StandardCharsets.UTF_8).trim();
        System.out.println("Header JSON: " + headerJson);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(headerJson, Map.class);
    }

    private static double[][] extractVectors(byte[] data, Map<String, Object> header) {
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
        long end = dataOffsets.get(1).longValue();

        System.out.println("Vectors shape: " + rows + "x" + cols);
        System.out.println("Data offsets: " + begin + " to " + end);
        System.out.println("Data type: " + dtype);

        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        // Correctly position the buffer to start reading after the header
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

        // Log the first few rows and columns to verify the content
        System.out.println("First few vectors:");
        for (int i = 0; i < Math.min(5, rows); i++) {
            for (int j = 0; j < Math.min(10, cols); j++) {
                System.out.print(vectors[i][j] + " ");
            }
            System.out.println();
        }

        return vectors;
    }

    @SuppressWarnings("unchecked")
    private static String[] extractDocids(byte[] data, Map<String, Object> header) {
        Map<String, Object> docidsInfo = (Map<String, Object>) header.get("docids");
        String dtype = (String) docidsInfo.get("dtype");
        
        List<Integer> shapeList = (List<Integer>) docidsInfo.get("shape");
        int length = shapeList.get(0);
        int maxCols = shapeList.get(1);

        List<Number> dataOffsets = (List<Number>) docidsInfo.get("data_offsets");
        long begin = dataOffsets.get(0).longValue();
        long end = dataOffsets.get(1).longValue();

        System.out.println("Docids shape: " + length + "x" + maxCols);
        System.out.println("Data offsets: " + begin + " to " + end);
        System.out.println("Data type: " + dtype);

        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        // Correctly position the buffer to start reading after the header
        buffer.position((int) (begin + buffer.getLong(0) + 8));

        String[] docids = new String[length];
        StringBuilder sb = new StringBuilder();
        if (dtype.equals("I64")) {
            for (int i = 0; i < length; i++) {
                sb.setLength(0);
                for (int j = 0; j < maxCols; j++) {
                    char c = (char) buffer.getLong();
                    if (c != 0) sb.append(c);
                }
                docids[i] = sb.toString();
            }
        } else {
            throw new UnsupportedOperationException("Unsupported data type: " + dtype);
        }

        // Log the first few docid indices to verify the content
        System.out.println("First few docids:");
        for (int i = 0; i < Math.min(10, docids.length); i++) {
            System.out.println(docids[i]);
        }

        return docids;
    }
}

package io.anserini.index.generator;

import io.anserini.collection.SourceDocument;
import io.anserini.index.Constants;
import io.anserini.index.IndexHnswDenseVectors;
import io.anserini.index.IndexHnswDenseVectors.Args;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Stream;

public class HnswJsonWithSafeTensorsDenseVectorDocumentGenerator<T extends SourceDocument>
        implements LuceneDocumentGenerator<T> {
    private static final Logger LOG = LogManager.getLogger(HnswJsonWithSafeTensorsDenseVectorDocumentGenerator.class);
    protected Args args;
    private HashSet<String> allowedFileSuffix;

    public HnswJsonWithSafeTensorsDenseVectorDocumentGenerator() {
        this.allowedFileSuffix = new HashSet<>(Arrays.asList(".json", ".jsonl", ".gz"));
        LOG.info("V1 Initializing HnswJsonWithSafeTensorsDenseVectorDocumentGenerator...");
    }

    public void setArgs(IndexHnswDenseVectors.Args args) {
        this.args = args;
        LOG.info("Args set via setter method:");
        LOG.info(" - Input path: " + this.args.input);
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

            // Read and deserialize the SafeTensors files
            byte[] vectorsData = Files.readAllBytes(Paths.get(filePaths.vectorsFilePath));
            byte[] docidsData = Files.readAllBytes(Paths.get(filePaths.docidsFilePath));

            // Deserialize vectors and docid ASCII values
            double[][] vectors = extractVectors(vectorsData);
            int[][] docidAsciiValues = extractDocidAsciiValues(docidsData);

            // Create the Lucene document
            String id = src.id();
            LOG.info("Processing document ID: " + id);
            int[] docidAscii = id.chars().toArray();

            Integer index = null;
            for (int i = 0; i < docidAsciiValues.length; i++) {
                if (Arrays.equals(docidAscii, docidAsciiValues[i])) {
                    index = i;
                    break;
                }
            }

            if (index == null) {
                LOG.error("Error finding index for document ID: " + id);
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

    private double[][] extractVectors(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        int rows = buffer.getInt();
        int cols = buffer.getInt();

        double[][] vectors = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                vectors[i][j] = buffer.getDouble();
            }
        }
        return vectors;
    }

    private int[][] extractDocidAsciiValues(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        int rows = buffer.getInt();
        int maxCols = buffer.getInt();

        int[][] docidAsciiValues = new int[rows][maxCols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < maxCols; j++) {
                docidAsciiValues[i][j] = buffer.getInt();
            }
        }
        return docidAsciiValues;
    }
}

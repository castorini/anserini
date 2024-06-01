package io.anserini.index.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.anserini.collection.SourceDocument;
import io.anserini.index.Constants;
import io.anserini.index.SafeTensorsIndexCollection;
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
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Converts a {@link SourceDocument} from SafeTensors into a Lucene {@link Document}, ready to be indexed.
 *
 * @param <T> type of the source document
 */
public class HnswSafetensorsDenseVectorDocumentGenerator<T extends SourceDocument> implements LuceneDocumentGenerator<T> {
    private final SafeTensorsIndexCollection.Args args;

    public HnswSafetensorsDenseVectorDocumentGenerator(SafeTensorsIndexCollection.Args args) {
        this.args = args;
    }

    @Override
    public Document createDocument(T src) throws InvalidDocumentException {
        try {
            // Read and deserialize the SafeTensors files
            byte[] vectorsData = Files.readAllBytes(Paths.get(args.vectorsPath));
            byte[] docidsData = Files.readAllBytes(Paths.get(args.docidsPath));

            // Deserialize docid_to_idx.json
            ObjectMapper objectMapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
            Map<String, Integer> docidToIdx = objectMapper.readValue(Files.readAllBytes(Paths.get(args.docidToIdxPath)), Map.class);
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
            int index = idxToDocid.entrySet().stream().filter(entry -> entry.getValue().equals(id)).findFirst().orElseThrow().getKey();
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

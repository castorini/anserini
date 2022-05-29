package io.anserini.search.queryencoder;

import ai.djl.modality.nlp.DefaultVocabulary;
import ai.djl.modality.nlp.bert.BertFullTokenizer;
import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;

public class UnicoilMsmarcoPassageEncoder extends QueryEncoder{
    static private final String MODEL_URL = "https://dl.dropboxusercontent.com/s/39jqt27b6efuyry/UnicoilEncoder.onnx?dl=0";

    static private final String VOCAB_URL = "https://dl.dropboxusercontent.com/s/2kgkvw6gm37ghc8/vocab.txt?dl=0RYd";

    static private final String MODEL_NAME = "UnicoilEncoder.onnx";

    static private final String VOCAB_NAME = "UnicoilVocab.txt";

    private final BertFullTokenizer tokenizer;

    private final OrtEnvironment environment;

    private final OrtSession session;

    static private Path getModelPath() throws IOException {
        File modelFile = new File(getCacheDir(), MODEL_NAME);
        FileUtils.copyURLToFile(new URL(MODEL_URL), modelFile);
        return modelFile.toPath();
    }

    static private Path getVocabPath() throws IOException {
        File vocabFile = new File(getCacheDir(), VOCAB_NAME);
        FileUtils.copyURLToFile(new URL(VOCAB_URL), vocabFile);
        return vocabFile.toPath();
    }

    public UnicoilMsmarcoPassageEncoder(int weightRange, int quantRange) throws IOException, OrtException {
        super(weightRange, quantRange);
        DefaultVocabulary vocabulary = DefaultVocabulary.builder()
                .addFromTextFile(getVocabPath())
                .optUnknownToken("[UNK]")
                .build();
        this.tokenizer = new BertFullTokenizer(vocabulary, true);
        this.environment = OrtEnvironment.getEnvironment();
        this.session = environment.createSession(getModelPath().toString(), new OrtSession.SessionOptions());
    }

    @Override
    public String encode(String query) throws OrtException {
        String encodedQuery = "";
        List<String> queryTokens = new ArrayList();
        queryTokens.add("[CLS]");
        queryTokens.addAll(tokenizer.tokenize(query));
        queryTokens.add("[SEP]");

        Map<String, OnnxTensor> inputs = new HashMap<>();
        long[] queryTokenIds = convertTokensToIds(tokenizer, queryTokens);
        long [][] inputTokenIds = new long[1][queryTokenIds.length];
        inputTokenIds[0] = queryTokenIds;
        inputs.put("inputIds", OnnxTensor.createTensor(environment, inputTokenIds));

        try (OrtSession.Result results = session.run(inputs)) {
            float[] computedWeights = flatten(results.get(0).getValue());
            LinkedHashMap<String, Float> tokenWeightMap = generateTokenWeightMap(queryTokens, computedWeights);
            encodedQuery = generateEncodedQuery(tokenWeightMap);
        }
        return encodedQuery;
    }

    private long[] convertTokensToIds(BertFullTokenizer tokenizer, List<String> tokens) {
        int numTokens = tokens.size();
        long[] tokenIds = new long[numTokens];
        for(int i = 0; i < numTokens; ++i) {
            tokenIds[i] = tokenizer.getVocabulary().getIndex(tokens.get(i));
        }
        return tokenIds;
    }

    private float[] flatten(Object obj) {
        List<Float> weightsList = new ArrayList<>();
        Object[] inputs = (Object[]) obj;
        for (Object input : inputs) {
            float[][] weights = (float[][]) input;
            for (float[] weight : weights) {
                weightsList.add(weight[0]);
            }
        }
        return toArray(weightsList);
    }

    private float[] toArray(List<Float> input) {
        float[] output = new float[input.size()];
        for (int i = 0; i < output.length; i++) {
            output[i] = input.get(i);
        }
        return output;
    }

    private LinkedHashMap<String, Float> generateTokenWeightMap(List<String> tokens, float[] computedWeights) {
        LinkedHashMap<String, Float> tokenWeightMap = new LinkedHashMap<>();
        for (int i = 0; i < tokens.size(); ++i) {
            String token = tokens.get(i);
            float tokenWeight = computedWeights[i];
            if (token.equals("[CLS]")) {
                continue;
            } else if (token.equals("[PAD]")) {
                break;
            } else if (tokenWeightMap.containsKey(token)) {
                Float accumulatedWeight = tokenWeightMap.get(token);
                tokenWeightMap.put(token, accumulatedWeight + tokenWeight);
            } else {
                tokenWeightMap.put(token, tokenWeight);
            }
        }
        return tokenWeightMap;
    }

    private String generateEncodedQuery(LinkedHashMap<String, Float> tokenWeightMap) {
        List<String> encodedQuery = new ArrayList();
        for (Map.Entry<String, Float> entry : tokenWeightMap.entrySet()) {
            String token = entry.getKey();
            Float tokenWeight = entry.getValue();
            int weightQuanted = Math.round(tokenWeight / weightRange * quantRange);
            for (int i = 0; i < weightQuanted; ++i) {
                encodedQuery.add(token);
            }
        }
        return String.join(" ", encodedQuery);
    }
}

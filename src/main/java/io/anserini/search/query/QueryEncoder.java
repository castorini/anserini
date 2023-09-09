package io.anserini.search.query;

import ai.djl.modality.nlp.DefaultVocabulary;
import ai.djl.modality.nlp.Vocabulary;
import ai.djl.modality.nlp.bert.BertFullTokenizer;
import ai.onnxruntime.OrtException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.apache.commons.io.FileUtils;

public abstract class QueryEncoder {
  static private final String CACHE_DIR = Paths.get(System.getProperty("user.home"), "/.cache/anserini/encoders").toString();

  protected int weightRange;

  protected int quantRange;

  static protected Path getModelPath(String modelName, String modelURL) throws IOException {
    File modelFile = new File(getCacheDir(), modelName);
    if (!modelFile.exists()) {
      System.out.println("Downloading model");
      FileUtils.copyURLToFile(new URL(modelURL), modelFile);
    } else{
      System.out.println("Model already exists");
    }
    return modelFile.toPath();
  }

  static protected Path getVocabPath(String vocabName, String vocabURL) throws IOException {
    File vocabFile = new File(getCacheDir(), vocabName);
    if (!vocabFile.exists()) {
      System.out.println("Downloading vocab");
      FileUtils.copyURLToFile(new URL(vocabURL), vocabFile);
    } else{
      System.out.println("Vocab already exists");
    }
    return vocabFile.toPath();
  }

  static protected String getCacheDir() {
    File cacheDir = new File(CACHE_DIR);
    if (!cacheDir.exists()) {
      cacheDir.mkdir();
    }
    return cacheDir.getPath();
  }

  public QueryEncoder(int weightRange, int quantRange) {
    this.weightRange = weightRange;
    this.quantRange = quantRange;
  }

  public abstract String encode(String query) throws OrtException;

  protected static long[] convertTokensToIds(BertFullTokenizer tokenizer, List<String> tokens, Vocabulary vocab) {
    int numTokens = tokens.size();
    long[] tokenIds = new long[numTokens];
    for (int i = 0; i < numTokens; ++i) {
      tokenIds[i] = vocab.getIndex(tokens.get(i));
    }
    return tokenIds;
  }

  public String generateEncodedQuery(Map<String, Float> tokenWeightMap) {
    /*
     * This function generates the encoded query.
     */
    List<String> encodedQuery = new ArrayList<>();
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

  public Map<String, Integer> getEncodedQueryMap(Map<String, Float> tokenWeightMap) throws OrtException {
    Map<String, Integer> encodedQuery = new HashMap<>();
    for (Map.Entry<String, Float> entry : tokenWeightMap.entrySet()) {
      String token = entry.getKey();
      Float tokenWeight = entry.getValue();
      int weightQuanted = Math.round(tokenWeight / weightRange * quantRange);
      encodedQuery.put(token, weightQuanted);
    }
    return encodedQuery;
  }

  public Map<String, Integer> getEncodedQueryMap(String query) throws OrtException {
    Map<String, Float> tokenWeightMap = getTokenWeightMap(query);
    return getEncodedQueryMap(tokenWeightMap);
  }

  static protected Map<String, Float> getTokenWeightMap(long[] indexes, float[] computedWeights, DefaultVocabulary vocab) {
    /*
     * This function returns a map of token to its weight.
     */
    Map<String, Float> tokenWeightMap = new LinkedHashMap<>();

    for (int i = 0; i < indexes.length; i++) {
      if (indexes[i] == 101 || indexes[i] == 102 || indexes[i] == 0) {
        continue;
      }
      tokenWeightMap.put(vocab.getToken(indexes[i]), computedWeights[i]);
    }
    return tokenWeightMap;
  }

  protected abstract Map<String, Float> getTokenWeightMap(String query) throws OrtException;
}
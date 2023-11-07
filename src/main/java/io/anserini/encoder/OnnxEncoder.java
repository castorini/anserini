package io.anserini.encoder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;

import ai.djl.modality.nlp.DefaultVocabulary;
import ai.djl.modality.nlp.Vocabulary;
import ai.djl.modality.nlp.bert.BertFullTokenizer;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;

public abstract class OnnxEncoder <T>{
  protected final BertFullTokenizer tokenizer;

  protected final DefaultVocabulary vocab;

  protected final OrtEnvironment environment;

  protected final OrtSession session;

  static private final String CACHE_DIR = Paths.get(System.getProperty("user.home"), "/.cache/anserini/encoders")
      .toString();

  static protected Path getVocabPath(String vocabName, String vocabURL) throws IOException {
    File vocabFile = new File(getCacheDir(), vocabName);
    if (!vocabFile.exists()) {
      System.out.println("Downloading vocab");
      FileUtils.copyURLToFile(new URL(vocabURL), vocabFile);
    } else {
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

  static protected Path getModelPath(String modelName, String modelURL) throws IOException {
    File modelFile = new File(getCacheDir(), modelName);
    if (!modelFile.exists()) {
      System.out.println("Downloading model");
      FileUtils.copyURLToFile(new URL(modelURL), modelFile);
    } else {
      System.out.println("Model already exists");
    }
    return modelFile.toPath();
  }

  protected static long[] convertTokensToIds(BertFullTokenizer tokenizer, List<String> tokens, Vocabulary vocab) {
    int numTokens = tokens.size();
    long[] tokenIds = new long[numTokens];
    for (int i = 0; i < numTokens; ++i) {
      tokenIds[i] = vocab.getIndex(tokens.get(i));
    }
    return tokenIds;
  }

  public abstract T encode(String query) throws OrtException;

  public OnnxEncoder(String modelName, String modelURL, String vocabName, String vocabURL)
      throws IOException, OrtException {
    this.vocab = DefaultVocabulary.builder()
        .addFromTextFile(getVocabPath(vocabName, vocabURL))
        .optUnknownToken("[UNK]")
        .build();
    this.tokenizer = new BertFullTokenizer(vocab, true);
    this.environment = OrtEnvironment.getEnvironment();
    this.session = environment.createSession(getModelPath(modelName, modelURL).toString(),
        new OrtSession.SessionOptions());
    System.out.println("Model loaded.");
  }

}

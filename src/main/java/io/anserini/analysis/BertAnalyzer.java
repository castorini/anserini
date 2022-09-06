package io.anserini.analysis;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ai.djl.huggingface.tokenizers.Encoding;
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import org.apache.commons.io.IOUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;


public class BertAnalyzer extends Analyzer {
  
  private final HuggingFaceTokenizer tokenizer;
  private static String huggingFaceModelId = "bert-base-multilingual-uncased";
  
  public BertAnalyzer(){
    Map<String, String> options = new ConcurrentHashMap<>();
    options.put("addSpecialTokens", "false");
    this.tokenizer = HuggingFaceTokenizer.newInstance(this.huggingFaceModelId, options);
  }
  
  @Override
  protected Reader initReader(String fieldName, Reader reader) {
    try {
      return tokenizeReader(reader);
    } catch (IOException e) {
      return reader;
    }
  }
  
  @Override
  protected TokenStreamComponents createComponents(String fieldName) {
    Tokenizer tokenizer = new WhitespaceTokenizer();
    TokenStream stream = new LowerCaseFilter(tokenizer);
    return new TokenStreamComponents(tokenizer, stream);
  }
  
  /**
   * Tokenizes a String Object
   * @param reader
   * @return
   * @throws IOException
   */
  public Reader tokenizeReader(Reader reader) throws IOException {
    String targetString = IOUtils.toString(reader);
    Encoding encoding = this.tokenizer.encode(targetString);
    String[] tokens = encoding.getTokens();
    String tokenizedString = String.join(" ", tokens);
    return new StringReader(tokenizedString);
  }
  
}

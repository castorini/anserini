/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 package io.anserini.analysis;

 import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
 import org.apache.commons.io.IOUtils;
 import org.apache.logging.log4j.LogManager;
 import org.apache.logging.log4j.Logger;
 import org.apache.lucene.analysis.Analyzer;
 import org.apache.lucene.analysis.TokenStream;
 import org.apache.lucene.analysis.Tokenizer;
 import org.apache.lucene.analysis.core.WhitespaceTokenizer;
 import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
 
 import java.io.IOException;
 import java.io.Reader;
 import java.io.StringReader;
 import java.nio.file.Files;
 import java.nio.file.Path;
 import java.nio.file.Paths;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 
public class CompositeAnalyzer extends Analyzer {
  private static final Logger LOG = LogManager.getLogger(CompositeAnalyzer.class);
  private final HuggingFaceTokenizer tokenizer;
  private final Analyzer analyzer;

  private static HuggingFaceTokenizer makeTokenizer(String tokenizerNameOrPath) throws IOException {
    Map<String, String> options = new ConcurrentHashMap<>();
    options.put("addSpecialTokens", "false");
    // Note upgrading from djl v0.21.0 to v0.28.0 (June 2024)
    //
    // In theory, since we're just tokenizing, we shouldn't be constrained by the modelMaxLength.
    // Previously, at v0.21.0, we were able to tokenize arbitrarily long sequences.
    // However, the implementation seems to have changed.
    //
    // As of the v0.28.0 upgrade, if we put a large value, we get the warning:
    // "maxLength is greater then (sic) modelMaxLength, change to: 512"
    //
    // On the other hand, if we don't set this value, we get the warning:
    // "maxLength is not explicitly specified, use modelMaxLength: 512".
    //
    // In other words, the implementation forces truncation, even for our IR application, i.e., it
    // forces FirstP retrieval.
    options.put("maxLength", "512");

    Path path = Paths.get(tokenizerNameOrPath);
    
    if (Files.exists(path) == true) {
      return HuggingFaceTokenizer.newInstance(path, options);
    } else {
      return HuggingFaceTokenizer.newInstance(tokenizerNameOrPath, options);
    }
  }
 
  public CompositeAnalyzer(String tokenizerNameOrPath, Analyzer analyzer) throws IOException {
    this(makeTokenizer(tokenizerNameOrPath), analyzer);
  }

  public CompositeAnalyzer(HuggingFaceTokenizer tokenizer, Analyzer analyzer) {
    this.tokenizer = tokenizer;
    this.analyzer = analyzer;	
  }
 
  private Reader applyAnalyzers(Reader reader) throws IOException {
    String readerString = IOUtils.toString(reader);
    TokenStream tokenStream = analyzer.tokenStream(null, readerString);
    CharTermAttribute termAtt = tokenStream.addAttribute(CharTermAttribute.class);
    List<String> analyzerOutput = new ArrayList<>();

    tokenStream.reset();

    while (tokenStream.incrementToken()) {
      String termAttStr = termAtt.toString();
      if (termAtt.toString().length() == 0) { continue; }
      analyzerOutput.add("bm25_" + termAttStr);
    }

    tokenStream.end();
    tokenStream.close();

    String analyzedString = String.join(" ", analyzerOutput);
    String[] tokens = this.tokenizer.encode(readerString).getTokens();
    // Important for empty documents
    String tokenizedString = tokens.length > 0 ? "bm25wp_" + String.join(" bm25wp_", tokens) : "";

    return new StringReader(String.join(" ", 	new String[]{tokenizedString, analyzedString}));
  }
 
  @Override
  protected Reader initReader(String fieldName, Reader reader) {
    try { 
      return applyAnalyzers(reader);
    } catch (IOException e) {
      LOG.error("Error applying analyzers: " + e);
    }
    return reader;
  }
 
  @Override
  protected TokenStreamComponents createComponents(String fieldName) {
    Tokenizer wTokenizer = new WhitespaceTokenizer();
    return new TokenStreamComponents(wTokenizer);
  }
}
 
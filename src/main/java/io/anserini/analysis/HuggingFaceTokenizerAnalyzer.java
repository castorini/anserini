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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ai.djl.huggingface.tokenizers.Encoding;
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import org.apache.commons.io.IOUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;


public class HuggingFaceTokenizerAnalyzer extends Analyzer {
  
  private final HuggingFaceTokenizer tokenizer;
  
  public HuggingFaceTokenizerAnalyzer(String huggingFaceModelId) throws IOException {
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
    Path path = Paths.get(huggingFaceModelId);
    
    if(Files.exists(path) == true){
      this.tokenizer = HuggingFaceTokenizer.newInstance(path, options);
    } else {
      this.tokenizer = HuggingFaceTokenizer.newInstance(huggingFaceModelId, options);
    }
    
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
    return new TokenStreamComponents(tokenizer);
  }
  
  /**
   * Tokenizes a String Object
   * @param reader String Object
   * @return Reader
   * @throws IOException IOException
   */
  public Reader tokenizeReader(Reader reader) throws IOException {
    String targetString = IOUtils.toString(reader);
    Encoding encoding = this.tokenizer.encode(targetString);
    String[] tokens = encoding.getTokens();
    String tokenizedString = String.join(" ", tokens);
    return new StringReader(tokenizedString);
  }
  
}

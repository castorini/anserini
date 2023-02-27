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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CompositeTokenizerTest {
Object[][] examples = new Object[][]{
  {"Ṣé Wàá Fọkàn sí Àwọn Ohun Tá A Ti Kọ Sílẹ̀?",
    new String[] {
      "bm25wp_se", "bm25wp_wa", "bm25wp_##a", 
      "bm25wp_fo", "bm25wp_##kan", "bm25wp_si", 
      "bm25wp_awon", "bm25wp_oh", "bm25wp_##un", 
      "bm25wp_ta", "bm25wp_a", "bm25wp_ti", "bm25wp_ko", 
      "bm25wp_sile", "bm25wp_?", "bm25_Ṣé", "bm25_Wàá", 
      "bm25_Fọkàn", "bm25_sí", "bm25_Àwọn", "bm25_Ohun", 
      "bm25_Tá", "bm25_A", "bm25_Ti", "bm25_Kọ", "bm25_Sílẹ̀?"} }
  };
  String huggingFaceModelId = "bert-base-multilingual-uncased";

  @Test
  public void basic() throws Exception {
    Analyzer analyzer = new CompositeTokenizer(huggingFaceModelId, new WhitespaceAnalyzer());

    for (int i = 0; i < examples.length; i++) {
      verify((String[]) examples[i][1], parseKeywords(analyzer, (String) examples[i][0]));
    }
  }

  public void verify(String[] truth, List<String> tokens) {
    assertEquals(truth.length, tokens.size());
    for ( int i=0; i<truth.length; i++) {
      assertEquals(truth[i], tokens.get(i));
    }
  }

  public List<String> parseKeywords(Analyzer analyzer, String keywords) throws IOException {
    List<String> list = new ArrayList<>();
    
    TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(keywords));
    CharTermAttribute cattr = tokenStream.addAttribute(CharTermAttribute.class);
    tokenStream.reset();
    while (tokenStream.incrementToken()) {
      if (cattr.toString().length() == 0) {
        continue;
      }
      list.add(cattr.toString());
    }
    tokenStream.end();
    tokenStream.close();

    System.out.println(list);
    
    return list;
  }

}

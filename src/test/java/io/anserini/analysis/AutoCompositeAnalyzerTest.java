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

import junit.framework.JUnit4TestAdapter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class AutoCompositeAnalyzerTest {
  @BeforeClass
  public static void setupClass() {
    Configurator.setLevel(AutoCompositeAnalyzer.class.getName(), Level.ERROR);
  }

  private static final Map<String, Object[][]> examples = new HashMap<>() {
    {
      // Case (1): Both Lucene analyzer & monolingual tokenizer exist
      put("fi", new Object[][] {{"Erektiohäiriön taustalla olevat syyt voivat olla fyysisiä tai psyykkisiä",
        new String[] {"bm25wp_E", "bm25wp_##re", "bm25wp_##k", "bm25wp_##tio", "bm25wp_##h", "bm25wp_##ä", 
          "bm25wp_##ir", "bm25wp_##i", "bm25wp_##ön", "bm25wp_ta", "bm25wp_##usta", "bm25wp_##lla", "bm25wp_ole", 
          "bm25wp_##vat", "bm25wp_sy", "bm25wp_##yt", "bm25wp_voi", "bm25wp_##vat", "bm25wp_olla", "bm25wp_f", 
          "bm25wp_##yys", "bm25wp_##isiä", "bm25wp_tai", "bm25wp_p", "bm25wp_##sy", "bm25wp_##y", "bm25wp_##kki", 
          "bm25wp_##si", "bm25wp_##ä", "bm25_erektiohäiriö", "bm25_taust", "bm25_olev", "bm25_syyt", "bm25_voiva", 
          "bm25_fyysis", "bm25_psyykkis"}}});
      
      // Case (2): No Lucene analyzer but monolingual tokenizer exists
      put("yo", new Object[][] {{"Àpèmọ́ra sẹ́ làá ń pe tèmídire",
        new String[] {"ape", "##mora", "se", "laa", "n", "pe", "temi", "##di", "##re"}}});

      // Case (3): No Lucene analyzer exists & no monolingual tokenizer exists
      put("ha", new Object[][] {{"Wikipedia da yaren Hausa ta kai muƙaloli guda dubu goma a yanzu",
        new String[] {"bm25wp_wikipedia", "bm25wp_da", "bm25wp_ya", "bm25wp_##ren", "bm25wp_haus", 
          "bm25wp_##a", "bm25wp_ta", "bm25wp_kai", "bm25wp_[UNK]", "bm25wp_gud", "bm25wp_##a", "bm25wp_dub", 
          "bm25wp_##u", "bm25wp_gom", "bm25wp_##a", "bm25wp_a", "bm25wp_yan", "bm25wp_##zu", "bm25_Wikipedia", 
          "bm25_da", "bm25_yaren", "bm25_Hausa", "bm25_ta", "bm25_kai", "bm25_muƙaloli", "bm25_guda", "bm25_dubu", 
          "bm25_goma", "bm25_a", "bm25_yanzu"}}});

      // Case (4): Lucene analyzer exists but no monolingual tokenizer
      put("es", new Object[][] {{"cielos nubosos y luces difusas y variables",
        new String[] {"ciel", "nubos", "luz", "difus", "variabl"}}});
    }
  };
  
  @Test
  public void case1() throws Exception {
    String language = "fi";
    Object[][] example = examples.get(language);
    Analyzer analyzer = AutoCompositeAnalyzer.getAnalyzer(language);

    for (int i = 0; i < example.length; i++) {
      List<String> tokens = parseKeywords(analyzer, (String) example[i][0]);
      verify((String[]) example[i][1], tokens);
    }
  }

  @Test
  public void case2() throws Exception {
    String language = "yo";
    Object[][] example = examples.get(language);
    Analyzer analyzer = AutoCompositeAnalyzer.getAnalyzer(language);
    
    for (int i = 0; i < example.length; i++) {
      verify((String[]) example[i][1], parseKeywords(analyzer, (String) example[i][0]));
    }
  }

  @Test
  public void case3() throws Exception {
    String language = "ha";
    Object[][] example = examples.get(language);
    Analyzer analyzer = AutoCompositeAnalyzer.getAnalyzer(language);
    
    for (int i = 0; i < example.length; i++) {
      verify((String[]) example[i][1], parseKeywords(analyzer, (String) example[i][0]));
    }
  }

  @Test
  public void case4() throws Exception {
    String language = "es";
    Analyzer analyzer = AutoCompositeAnalyzer.getAnalyzer(language);
    Object[][] example = examples.get(language);

    for (int i = 0; i < example.length; i++) {
      verify((String[]) example[i][1], parseKeywords(analyzer, (String) example[i][0]));
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
    
    return list;
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(AutoCompositeAnalyzerTest.class);
  }
}
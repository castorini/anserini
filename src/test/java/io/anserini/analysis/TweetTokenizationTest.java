/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
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
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TweetTokenizationTest {

  Object[][] examples = new Object[][] {
      {"AT&T getting secret immunity from wiretapping laws for government surveillance http://vrge.co/ZP3Fx5",
       new String[] {"att", "get", "secret", "immun", "from", "wiretap", "law", "for", "govern", "surveil", "http://vrge.co/ZP3Fx5"}},

      {"want to see the @verge aston martin GT4 racer tear up long beach? http://theracersgroup.kinja.com/watch-an-aston-martin-vantage-gt4-tear-around-long-beac-479726219 …",
       new String[] {"want", "to", "see", "the", "@verge", "aston", "martin", "gt4", "racer", "tear", "up", "long", "beach", "http://theracersgroup.kinja.com/watch-an-aston-martin-vantage-gt4-tear-around-long-beac-479726219"}},

      {"Incredibly good news! #Drupal users rally http://bit.ly/Z8ZoFe  to ensure blind accessibility contributor gets to @DrupalCon #Opensource",
       new String[] {"incred", "good", "new", "#drupal", "user", "ralli", "http://bit.ly/Z8ZoFe", "to", "ensur", "blind", "access", "contributor", "get", "to", "@drupalcon", "#opensource"}},

      {"We're entering the quiet hours at #amznhack. #Rindfleischetikettierungsüberwachungsaufgabenübertragungsgesetz",
       new String[] {"were", "enter", "the", "quiet", "hour", "at", "#amznhack", "#rindfleischetikettierungsüberwachungsaufgabenübertragungsgesetz"}},

      {"The 2013 Social Event Detection Task (SED) at #mediaeval2013, http://bit.ly/16nITsf  supported by @linkedtv @project_mmixer @socialsensor_ip",
       new String[] {"the", "2013", "social", "event", "detect", "task", "sed", "at", "#mediaeval2013", "http://bit.ly/16nITsf", "support", "by", "@linkedtv", "@project_mmixer", "@socialsensor_ip"}},

      {"U.S.A. U.K. U.K USA UK #US #UK #U.S.A #U.K ...A.B.C...D..E..F..A.LONG WORD",
       new String[] {"usa", "uk", "uk", "usa", "uk", "#us", "#uk", "#u", "sa", "#u", "k", "abc", "d", "e", "f", "a", "long", "word"}},

      {"this is @a_valid_mention and this_is_multiple_words",
       new String[] {"thi", "is", "@a_valid_mention", "and", "thi", "is", "multipl", "word"}},

      {"PLEASE BE LOWER CASE WHEN YOU COME OUT THE OTHER SIDE - ALSO A @VALID_VALID-INVALID",
       new String[] {"pleas", "be", "lower", "case", "when", "you", "come", "out", "the", "other", "side", "also", "a", "@valid_valid", "invalid"}},

      // Note: the at sign is not the normal (at) sign and the crazy hashtag is not the normal #
      {"＠reply @with #crazy ~＃at",
       new String[] {"＠reply", "@with", "#crazy", "＃at"}},

      {":@valid testing(valid)#hashtags. RT:@meniton (the last @mention is #valid and so is this:@valid), however this is@invalid",
       new String[] {"@valid", "test", "valid", "#hashtags", "rt", "@meniton", "the", "last", "@mention", "is", "#valid", "and", "so", "is", "thi", "@valid", "howev", "thi", "is", "invalid"}},

      {"this][is[lots[(of)words+with-lots=of-strange!characters?$in-fact=it&has&Every&Single:one;of<them>in_here_B&N_test_test?test\\test^testing`testing{testing}testing…testing¬testing·testing what?",
       new String[] {"thi", "is", "lot", "of", "word", "with", "lot", "of", "strang", "charact", "in", "fact", "it", "ha", "everi", "singl", "on", "of", "them", "in", "here", "bn", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "what"}},
  };

  @Test
  public void basic() throws Exception {
    Analyzer analyzer = new TweetAnalyzer();

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

    return list;
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(TweetTokenizationTest.class);
  }
}

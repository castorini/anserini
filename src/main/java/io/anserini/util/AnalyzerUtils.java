/**
 * Anserini: An information retrieval toolkit built on Lucene
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

package io.anserini.util;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

final public class AnalyzerUtils {
  static public List<String> tokenize(Analyzer analyzer, String s) {
    List<String> list = new ArrayList<>();

    try {
      TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(s));
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
    } catch (IOException e) {
      e.printStackTrace();
    }

    return list;
  }

  static public Query buildBagOfWordsQuery(String field, Analyzer analyzer, String queryText) {
    List<String> tokens = tokenize(analyzer, queryText);

    BooleanQuery.Builder builder = new BooleanQuery.Builder();
    for (String t : tokens) {
      builder.add(new TermQuery(new Term(field, t)), BooleanClause.Occur.SHOULD);
    }

    return builder.build();
  }

  /* Build the Sequence Dependence Model query. See:
   *  D. Metzler and W. B. Croft. A markov random field model for term dependencies. In SIGIR ’05.
   *
   */
  static public Query buildSdmQuery(String field, Analyzer analyzer, String queryText,
                                    float termWeight, float orderWindowWeight, float unorderWindowWeight) {
    List<String> tokens = tokenize(analyzer, queryText);

    BooleanQuery.Builder termsBuilder = new BooleanQuery.Builder();
    if (tokens.size() == 1) {
      termsBuilder.add(new TermQuery(new Term(field, tokens.get(0))), BooleanClause.Occur.SHOULD);
      return termsBuilder.build();
    }

    BooleanQuery.Builder orderedWindowBuilder = new BooleanQuery.Builder();
    BooleanQuery.Builder unorderedWindowBuilder = new BooleanQuery.Builder();
    for (int i = 0; i < tokens.size()-1; i++) {
      termsBuilder.add(new TermQuery(new Term(field, tokens.get(i))), BooleanClause.Occur.SHOULD);

      SpanTermQuery t1 = new SpanTermQuery(new Term(field, tokens.get(i)));
      SpanTermQuery t2 = new SpanTermQuery(new Term(field, tokens.get(i+1)));
      SpanNearQuery orderedQ = new SpanNearQuery(new SpanQuery[] {t1, t2}, 1, true);
      SpanNearQuery unorderedQ = new SpanNearQuery(new SpanQuery[] {t1, t2}, 8, false);

      orderedWindowBuilder.add(orderedQ, BooleanClause.Occur.SHOULD);
      unorderedWindowBuilder.add(unorderedQ, BooleanClause.Occur.SHOULD);
    }
    termsBuilder.add(new TermQuery(new Term(field, tokens.get(tokens.size()-1))), BooleanClause.Occur.SHOULD);

    BooleanQuery.Builder builder = new BooleanQuery.Builder();
    builder.add(new BoostQuery(termsBuilder.build(), termWeight), BooleanClause.Occur.SHOULD);
    builder.add(new BoostQuery(orderedWindowBuilder.build(), orderWindowWeight), BooleanClause.Occur.SHOULD);
    builder.add(new BoostQuery(unorderedWindowBuilder.build(), unorderWindowWeight), BooleanClause.Occur.SHOULD);

    return builder.build();
  }
}

package io.anserini.analysis.vectors.lexlsh;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;

import java.io.IOException;

/**
 * {@link TokenFilter} which truncates a number beyond {#length} decimals.
 */
class TruncateTokenFilter extends TokenFilter {

    private final CharTermAttribute termAttribute = addAttribute(CharTermAttribute.class);
    private final KeywordAttribute keywordAttr = addAttribute(KeywordAttribute.class);

    private final int decimals;

    TruncateTokenFilter(TokenStream input, int decimals) {
      super(input);
      if (decimals < 1) {
        throw new IllegalArgumentException("'decimals' parameter must be a positive number: " + decimals);
      }
      this.decimals = decimals;
    }

    @Override
    public final boolean incrementToken() throws IOException {
      if (input.incrementToken()) {
        String s = termAttribute.toString();
        int decimalPlaceSeparatorIndex = s.indexOf(".");
        int threshold = decimalPlaceSeparatorIndex + 1 + decimals;
        if (!keywordAttr.isKeyword() && termAttribute.length() > threshold) {
            termAttribute.setLength(threshold);
        }
        return true;
      } else {
        return false;
      }
    }
  }
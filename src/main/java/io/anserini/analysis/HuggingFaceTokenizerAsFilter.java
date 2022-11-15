package io.anserini.analysis;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Arrays;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

public final class HuggingFaceTokenizerAsFilter extends TokenFilter {
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);
    private final HuggingFaceTokenizer tokenizer;
    private LinkedList<String> tokenizedString = new LinkedList<String>();
    private State current;
    
    public HuggingFaceTokenizerAsFilter(TokenStream input, HuggingFaceTokenizer tokenizer) {
        super(input);
        this.tokenizer = tokenizer;
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (!tokenizedString.isEmpty()) {
            String s = tokenizedString.pop();
            restoreState(current);
            termAtt.setEmpty().append(s);
            posIncrAtt.setPositionIncrement(0);
            return true;
        }

        if (!input.incrementToken()) {
            return false;
        }
        
        // Only insert tokens when tokenizer returns more than one token
        String[] tokens = tokenizer.encode(termAtt.toString()).getTokens();
        if (tokens.length > 1) {
            tokenizedString = new LinkedList<String>(Arrays.asList(tokens));
            current = captureState();
            return true;
        }

        return true;
    }
}

package io.anserini.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;

import java.util.Map;

public class TweetLowerCaseEntityPreservingFilterFactory extends TokenFilterFactory {

    public TweetLowerCaseEntityPreservingFilterFactory(Map<String, String> args) {
        super(args);
    }

    @Override
    public TokenStream create(TokenStream input) {
        return new TweetLowerCaseEntityPreservingFilter(input);
    }

}
package io.anserini.analysis;

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

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public final class CompositeAnalyzer extends Analyzer {
    private final HuggingFaceTokenizer tokenizer;
    private final Analyzer analyzer;

    public CompositeAnalyzer(HuggingFaceTokenizer tokenizer, Analyzer analyzer) {
        this.tokenizer = tokenizer;
        this.analyzer = analyzer;
    }

    public CompositeAnalyzer(String tokenizerNameOrPath, Analyzer analyzer) throws IOException {
        this(makeTokenizer(tokenizerNameOrPath), analyzer);
    }

    private static HuggingFaceTokenizer makeTokenizer(String tokenizerNameOrPath) throws IOException{
        Map<String, String> options = new ConcurrentHashMap<>();
        Path path = Paths.get(tokenizerNameOrPath);
        
        if (Files.exists(path) == true) {
            return HuggingFaceTokenizer.newInstance(path, options);
        } else {
            return HuggingFaceTokenizer.newInstance(tokenizerNameOrPath, options);
        }
    }

    @Override
    protected Reader initReader(String fieldName, Reader reader) {
        // Apply analyzer if exists
        if (analyzer != null) {
            try { return applyAnalyzer(reader);} 
            catch (IOException e) {
                System.err.println("Error applying analyzer: " + e);
            }
        }
        return reader;
    }

    private Reader applyAnalyzer(Reader reader) throws IOException {
        TokenStream tokenStream = analyzer.tokenStream(null, reader);
        CharTermAttribute cattr = tokenStream.addAttribute(CharTermAttribute.class);

        List<String> analyzerOutput = new ArrayList<>();

        tokenStream.reset();

        while (tokenStream.incrementToken()) {
            if (cattr.toString().length() == 0) { continue; }
            analyzerOutput.add(cattr.toString());
        }

        tokenStream.end();
        tokenStream.close();

        String tokenizedString = String.join(" ", analyzerOutput);
        return new StringReader(tokenizedString);
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer source = new WhitespaceTokenizer();
        TokenStream filter = new HuggingFaceTokenizerAsFilter(source, tokenizer);
        return new TokenStreamComponents(source, filter);
    }
}

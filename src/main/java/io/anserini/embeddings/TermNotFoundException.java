package io.anserini.embeddings;

/**
 * Throw TermNotFoundException if the term doesn't
 * exist the word embedding dictionary
 */
public class TermNotFoundException extends Exception {
    public TermNotFoundException(String term) {
        super(term);
    }
}

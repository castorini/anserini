package io.anserini.search.queryencoder;

import ai.onnxruntime.OrtException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public abstract class QueryEncoder {
    protected int weightRange;
    protected int quantRange;
    static private final String CACHE_DIR = "~/.cache/anserini";

    public static String getCacheDir() {
        File cacheDir = new File(CACHE_DIR);
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
        return cacheDir.getPath();
    }

    public QueryEncoder(int weightRange, int quantRange) {
        this.weightRange = weightRange;
        this.quantRange = quantRange;
    }

    public abstract String encode(String query) throws IOException, OrtException;

    public abstract Path getModelPath() throws IOException;

    public abstract Path getVocabPath() throws IOException;
}

package io.anserini.search.queryencoder;

import ai.onnxruntime.OrtException;
import java.io.File;

public abstract class QueryEncoder {
    static private final String CACHE_DIR = "~/.cache/anserini/main";

    protected int weightRange;

    protected int quantRange;

    static protected String getCacheDir() {
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

    public abstract String encode(String query) throws OrtException;
}

package io.anserini.encoder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

public abstract class EncoderInferenceTest {
    protected String modelName;
    protected String modelUrl;
    protected Object[][] examples;

    protected String getCacheDir() {
        File cacheDir = new File(System.getProperty("user.home") + "/.cache/anserini/test");
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
        return cacheDir.getPath();
    }

    protected Path getEncoderModelPath() throws IOException {
        File modelFile = new File(getCacheDir(), modelName);
        FileUtils.copyURLToFile(new URL(modelUrl), modelFile);
        return modelFile.toPath();
    }

    public EncoderInferenceTest(String modelName, String modelUrl, Object[][] examples) {
        this.modelName = modelName;
        this.modelUrl = modelUrl;
        this.examples = examples;
    }
}

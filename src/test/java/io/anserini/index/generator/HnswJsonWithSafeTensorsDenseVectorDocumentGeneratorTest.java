package io.anserini.index.generator;

import io.anserini.index.IndexCollection;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import static org.junit.Assert.assertEquals;



public class HnswJsonWithSafeTensorsDenseVectorDocumentGeneratorTest {
    @Test
    public void testGenerateFilePaths() throws Exception {
        IndexCollection.Args args = new IndexCollection.Args();
        args.input = "collections/beir-v1.0.0/bge-base-en-v1.5/nfcorpus";
        HnswJsonWithSafeTensorsDenseVectorDocumentGenerator<?> generator = new HnswJsonWithSafeTensorsDenseVectorDocumentGenerator<>(args);

        Path inputFolder = Paths.get(args.input);
        HnswJsonWithSafeTensorsDenseVectorDocumentGenerator.FilePaths filePaths = generator.generateFilePaths(inputFolder);

        assertEquals("collections/beir-v1.0.0/bge-base-en-v1.5.safetensors/nfcorpus/vectors.part00_vectors.safetensors", filePaths.vectorsFilePath);
        assertEquals("collections/beir-v1.0.0/bge-base-en-v1.5.safetensors/nfcorpus/vectors.part00_docids.safetensors", filePaths.docidsFilePath);
        assertEquals("collections/beir-v1.0.0/bge-base-en-v1.5.safetensors/nfcorpus/vectors.part00_docid_to_idx.json", filePaths.docidToIdxFilePath);
    }
}

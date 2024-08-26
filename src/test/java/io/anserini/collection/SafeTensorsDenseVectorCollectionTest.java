package io.anserini.collection;

import io.anserini.collection.SafeTensorsDenseVectorCollection;
import io.anserini.collection.DocumentCollection;
import io.anserini.collection.SourceDocument;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SafeTensorsDenseVectorCollectionTest {

    private static final Logger LOG = LogManager.getLogger(SafeTensorsDenseVectorCollectionTest.class);

    private SafeTensorsDenseVectorCollection collection;
    private Path testDirPath;

    @Before
    public void setUp() throws IOException {
        // Set up the directory path containing the safetensors files
        testDirPath = Paths.get("collections/beir-v1.0.0/bge-base-en-v1.5.safetensors/nfcorpus");
        LOG.info("Setting up the test with directory path: " + testDirPath.toString());
        collection = new SafeTensorsDenseVectorCollection(testDirPath);
    }

    @Test
    public void testDocumentProcessing() throws IOException {
        // Creating the file segment from the directory path
        LOG.info("Creating file segment for testing document processing.");
        FileSegment<SafeTensorsDenseVectorCollection.Document> segment = collection.createFileSegment(testDirPath);

        int documentCount = 0;
        Set<String> uniqueDocIds = new HashSet<>();  // To check for duplicate docids

        // Using for-each loop to iterate over the segment
        for (SafeTensorsDenseVectorCollection.Document document : segment) {
            documentCount++;
            LOG.debug("Processing document ID: " + document.id());

            // Check that the document ID is unique
            assertTrue("Duplicate docid found: " + document.id(), uniqueDocIds.add(document.id()));

            // Check that the document contents are not null or empty
            assertTrue("Document contents should not be null or empty for docid: " + document.id(),
                    document.contents() != null && !document.contents().isEmpty());

            // Additional content checks, e.g., checking vector size
            assertTrue("Document vector should not be empty for docid: " + document.id(),
                    document.contents().contains("[") && document.contents().contains("]"));
        }

        // Compare with the expected number of documents
        LOG.info("Total documents processed: " + documentCount);
        assertEquals("Expected number of documents", 3633, documentCount);
    }
}



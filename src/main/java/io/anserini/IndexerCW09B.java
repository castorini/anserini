package io.anserini;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.ConcurrentMergeScheduler;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.NoDeletionPolicy;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

/**
 * Indexer for ClueWeb09 Category B Corpus.
 */
public final class IndexerCW09B {

    static final String FIELD_BODY = "contents";
    static final String FIELD_ID = "id";
    private static final String RESPONSE = "response";

    private final class IndexerThread extends Thread {

        final private Path inputWarcFile;

        final private IndexWriter writer;

        volatile int addCount;

        public IndexerThread(IndexWriter writer, Path inputWarcFile) throws IOException {
            this.writer = writer;
            this.inputWarcFile = inputWarcFile;
            setName(inputWarcFile.getFileName().toString());
        }

        private int indexWarcFile() throws IOException {

            int i = 0;

            try (DataInputStream inStream = new DataInputStream(new GZIPInputStream(Files.newInputStream(inputWarcFile, StandardOpenOption.READ)))) {

                // iterate through our stream
                ClueWeb09WarcRecord wDoc;
                while ((wDoc = ClueWeb09WarcRecord.readNextWarcRecord(inStream)) != null) {
                    // see if it's a response record
                    if (RESPONSE.equals(wDoc.getHeaderRecordType())) {

                        String id = wDoc.getDocid();

                        org.jsoup.nodes.Document jDoc = Jsoup.parse(wDoc.getContent());

                        String contents = jDoc.text();
                        // don't index empty documents
                        if (contents.trim().length() == 0) {
                            System.err.println(id);
                            continue;
                        }

                        // make a new, empty document
                        Document document = new Document();

                        // document ID
                        document.add(new StringField(FIELD_ID, id, Field.Store.YES));

                        // entire document
                        document.add(new TextField(FIELD_BODY, contents, Field.Store.NO));

                        writer.addDocument(document);
                        i++;
                    }
                }
            }
            return i;
        }

        @Override
        public void run() {
            try {
                addCount = indexWarcFile();
                System.out.println("*./" + inputWarcFile.getParent().getFileName().toString() + File.separator + inputWarcFile.getFileName().toString() + "  " + addCount);
            } catch (IOException ioe) {
                System.out.println(Thread.currentThread().getName() + ": ERROR: unexpected IOException:");
                ioe.printStackTrace(System.out);
            }
        }
    }

    private final Path indexPath;
    private final Path docDir;


    public IndexerCW09B(String docsPath, String indexPath) throws IOException {

        this.indexPath = Paths.get(indexPath);
        if (!Files.exists(this.indexPath))
            Files.createDirectories(this.indexPath);

        docDir = Paths.get(docsPath);
        if (!Files.exists(docDir) || !Files.isReadable(docDir) || !Files.isDirectory(docDir)) {
            System.out.println("Document directory '" + docDir.toString() + "' does not exist or is not readable, please check the path");
            System.exit(1);
        }
    }


    private final static PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.warc.gz");


    static List<Path> discoverWarcFiles(Path p) {

        final List<Path> warcFiles = new ArrayList<>();

        FileVisitor<Path> fv = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                Path name = file.getFileName();
                if (name != null && matcher.matches(name))
                    warcFiles.add(file);
                return FileVisitResult.CONTINUE;
            }
        };

        try {
            Files.walkFileTree(p, fv);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return warcFiles;
    }

    /**
     * KStemAnalyzer: Filters {@link ClassicTokenizer} with {@link org.apache.lucene.analysis.standard.ClassicFilter},
     * {@link org.apache.lucene.analysis.core.LowerCaseFilter} and {@link org.apache.lucene.analysis.en.KStemFilter}.
     *
     * @return KStemAnalyzer
     * @throws IOException
     */
    static Analyzer analyzer() throws IOException {
        return CustomAnalyzer.builder()
                .withTokenizer("classic")
                .addTokenFilter("classic")
                .addTokenFilter("lowercase")
                .addTokenFilter("kstem")
                .build();
    }

    public int indexWithThreads(int numThreads) throws IOException, InterruptedException {

        System.out.println("Indexing with " + numThreads + " threads to directory '" + indexPath.toAbsolutePath() + "'...");

        final Directory dir = FSDirectory.open(indexPath);

        final IndexWriterConfig iwc = new IndexWriterConfig(analyzer());

        iwc.setSimilarity(new BM25Similarity());
        iwc.setIndexDeletionPolicy(NoDeletionPolicy.INSTANCE);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        iwc.setRAMBufferSizeMB(256.0);
        iwc.setUseCompoundFile(false);
        iwc.setMergeScheduler(new ConcurrentMergeScheduler());

        final IndexWriter writer = new IndexWriter(dir, iwc);

        final ExecutorService executor = Executors.newFixedThreadPool(numThreads);


        for (Path f : discoverWarcFiles(docDir))
            executor.execute(new IndexerThread(writer, f));


        //add some delay to let some threads spawn by scheduler
        Thread.sleep(30000);
        executor.shutdown(); // Disable new tasks from being submitted

        try {
            // Wait for existing tasks to terminate
            while (!executor.awaitTermination(5, TimeUnit.MINUTES)) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            executor.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }

        int numIndexed = writer.maxDoc();

        try {
            writer.commit();
        } finally {
            writer.close();
        }

        return numIndexed;
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        Args clArgs = new Args(args);

        final String dataDir = clArgs.getString("-dataDir");
        final String indexPath = clArgs.getString("-indexPath");
        final int numThreads = clArgs.getInt("-threadCount");

        clArgs.check();

        final long start = System.nanoTime();
        IndexerCW09B indexer = new IndexerCW09B(dataDir, indexPath);
        int numIndexed = indexer.indexWithThreads(numThreads);
        final long durationMillis = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
        System.out.println("Total " + numIndexed + " documents indexed in " + DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss"));
    }
}

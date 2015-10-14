package io.anserini.index;

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

import io.anserini.document.ClueWeb09WarcRecord;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

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
public final class IndexClueWeb09b {

    private static final Logger LOG = LogManager.getLogger(IndexClueWeb09b.class);

    public static final String FIELD_BODY = "contents";
    public static final String FIELD_ID = "id";
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
                        if (positions)
                            document.add(new TextField(FIELD_BODY, contents, Field.Store.NO));
                        else
                            document.add(new NoPositionsTextField(FIELD_BODY, contents));

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

    private boolean positions = false;

    public void setPositions(boolean positions) {
        this.positions = positions;
    }

    private boolean optimize = false;

    public void setOptimize(boolean optimize) {
        this.optimize = optimize;
    }

    private int doclimit = -1;

    public void setDocLimit(int doclimit) {
        this.doclimit = doclimit;
    }

    public IndexClueWeb09b(String docsPath, String indexPath) throws IOException {

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
    public static Analyzer analyzer() throws IOException {
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
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        iwc.setRAMBufferSizeMB(256.0);
        iwc.setUseCompoundFile(false);
        iwc.setMergeScheduler(new ConcurrentMergeScheduler());

        final IndexWriter writer = new IndexWriter(dir, iwc);

        final ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        List<Path> warcFiles = discoverWarcFiles(docDir);
        if (doclimit > 0 && warcFiles.size() < doclimit)
            warcFiles = warcFiles.subList(0, doclimit);

        for (Path f : warcFiles)
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
            if (optimize)
                writer.forceMerge(1);
        } finally {
            writer.close();
        }

        return numIndexed;
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        IndexArgs indexArgs = new IndexArgs();

        CmdLineParser parser = new CmdLineParser(indexArgs, ParserProperties.defaults().withUsageWidth(90));

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.err.println("Example: IndexClueWeb09b" + parser.printExample(OptionHandlerFilter.REQUIRED));
            return;
        }

        final long start = System.nanoTime();
        IndexClueWeb09b indexer = new IndexClueWeb09b(indexArgs.input, indexArgs.index);

        indexer.setPositions(indexArgs.positions);
        indexer.setOptimize(indexArgs.optimize);
        indexer.setDocLimit(indexArgs.doclimit);

        LOG.info("Index path: " + indexArgs.index);
        LOG.info("Threads: " + indexArgs.threads);
        LOG.info("Positions: " + indexArgs.positions);
        LOG.info("Optimize (merge segments): " + indexArgs.optimize);
        LOG.info("Doc limit: " + (indexArgs.doclimit == -1 ? "all docs" : "" + indexArgs.doclimit));

        LOG.info("Indexer: start");

        int numIndexed = indexer.indexWithThreads(indexArgs.threads);
        final long durationMillis = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
        LOG.info("Total " + numIndexed + " documents indexed in " + DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss"));
    }
}

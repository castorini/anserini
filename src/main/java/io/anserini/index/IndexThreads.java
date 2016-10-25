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

import io.anserini.document.*;
import org.apache.commons.compress.compressors.z.ZCompressorInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.benchmark.byTask.feeds.DemoHTMLParser;
import org.apache.lucene.benchmark.byTask.feeds.DocData;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.ConcurrentMergeScheduler;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;


public final class IndexThreads {

    private static final Logger LOG = LogManager.getLogger(IndexThreads.class);

    public static final String FIELD_BODY = "contents";
    public static final String FIELD_ID = "id";
    public static final String RESPONSE = "response";

    private final class IndexerThread extends Thread {

        final private Path inputFile;
        final private IndexWriter writer;

        public IndexerThread(IndexWriter writer, Path inputFile) throws IOException {
            this.writer = writer;
            this.inputFile = inputFile;
            setName(inputFile.getFileName().toString());
        }

        private int indexTextRecord(String id, String contents) throws IOException {
            // don't index empty documents but count them
            if (contents.trim().length() == 0) {
                System.err.println(id);
                return 1;
            }

            // make a new, empty document
            Document document = new Document();

            // document id
            document.add(new StringField(FIELD_ID, id, Field.Store.YES));

            FieldType fieldType = new FieldType();

            // Are we storing document vectors?
            if (docVectors) {
                fieldType.setStored(false);
                fieldType.setStoreTermVectors(true);
                fieldType.setStoreTermVectorPositions(true);
            }

            // Are we building a "positional" or "count" index?
            if (positions) {
                fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
            } else {
                fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
            }

            document.add(new Field(FIELD_BODY, contents, fieldType));

            writer.addDocument(document);
            return 1;
        }

        private String extractTextFromHTML(String raw, boolean useLucene) throws IOException, java.lang.IllegalArgumentException {
            String contents = "";
            if (useLucene) {
                DemoHTMLParser dhp = new DemoHTMLParser();
                DocData dd = new DocData();
                dd = dhp.parse(dd, "", null, new StringReader(raw), null);
                contents = dd.getTitle() + "\n" + dd.getBody();
            } else {
                org.jsoup.nodes.Document jDoc = Jsoup.parse(raw);
                contents = jDoc.text();
            }
            return contents;
        }

        private int indexSimpleRecord(ISimpleRecord ISimpleRecord) throws IOException {
            String id = ISimpleRecord.id();
            String contents;
            try {
                contents = extractTextFromHTML(ISimpleRecord.content(), false);
            } catch (IOException e) {
                LOG.error("Parsing document with Lucene HTML parser failed, skipping document : " + id, e);
                System.err.println(id);
                return 1;
            } catch (java.lang.IllegalArgumentException iae) {
                LOG.error("Parsing document with JSoup failed, skipping document : " + id, iae);
                System.err.println(id);
                return 1;
            }

            return indexTextRecord(id, contents);
        }

        private int indexWarcRecord(IWarcRecord warcRecord) throws IOException {
            // see if it's a response record
            if (!RESPONSE.equals(warcRecord.type()))
                return 0;

            String id = warcRecord.id();
            String contents;
            try {
                contents = extractTextFromHTML(warcRecord.content(), false);
            } catch (IOException e) {
                LOG.error("Parsing document with Lucene HTML parser failed, skipping document : " + id, e);
                System.err.println(id);
                return 1;
            } catch (java.lang.IllegalArgumentException iae) {
                LOG.error("Parsing document with JSoup failed, skipping document : " + id, iae);
                System.err.println(id);
                return 1;
            }

            return indexTextRecord(id, contents);
        }

        private int indexClueWeb12WarcFile() throws IOException {
            int i = 0;

            try (DataInputStream inStream = new DataInputStream(new GZIPInputStream(Files.newInputStream(inputFile, StandardOpenOption.READ)))) {
                // iterate through our stream
                ClueWeb12WarcRecord wDoc;
                while ((wDoc = ClueWeb12WarcRecord.readNextWarcRecord(inStream, ClueWeb12WarcRecord.WARC_VERSION)) != null) {
                    i += indexWarcRecord(wDoc);
                }
            }
            return i;
        }

        private int indexClueWeb09WarcFile() throws IOException {
            int i = 0;

            try (DataInputStream inStream = new DataInputStream(new GZIPInputStream(Files.newInputStream(inputFile, StandardOpenOption.READ)))) {
                // iterate through our stream
                ClueWeb09WarcRecord wDoc;
                while ((wDoc = ClueWeb09WarcRecord.readNextWarcRecord(inStream, ClueWeb09WarcRecord.WARC_VERSION)) != null) {
                    i += indexWarcRecord(wDoc);
                }
            }
            return i;
        }

        private int indexGov2File() throws IOException {
            int i = 0;

            try (InputStream stream = new GZIPInputStream(Files.newInputStream(inputFile, StandardOpenOption.READ), Gov2Record.BUFFER_SIZE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                ISimpleRecord doc;
                while ((doc = Gov2Record.readNextRecord(reader)) != null) {
                    i += indexSimpleRecord(doc);
                }
            }
            return i;
        }

        private int indexTrecFile() throws IOException {
            int i = 0;

            BufferedReader reader = null;
            try {
                String fileName = inputFile.toString();
                if (fileName.matches(".*?\\.\\d*z$")) { // .z .0z .1z .2z
                    FileInputStream fin = new FileInputStream(fileName);
                    BufferedInputStream in = new BufferedInputStream(fin);
                    ZCompressorInputStream zIn = new ZCompressorInputStream(in);
                    reader = new BufferedReader(new InputStreamReader(zIn, StandardCharsets.UTF_8));
                } else if (fileName.endsWith(".gz")) { //.gz
                    InputStream stream = new GZIPInputStream(Files.newInputStream(inputFile, StandardOpenOption.READ), TrecRecord.BUFFER_SIZE);
                    reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                } else { // plain text file
                    reader = new BufferedReader(new FileReader(fileName));
                }

                ISimpleRecord doc;
                while ((doc = TrecRecord.readNextRecord(reader)) != null) {
                    i += indexSimpleRecord(doc);
                }
            } finally {
                if (reader != null)
                    reader.close();
            }

            return i;
        }

        @Override
        public void run() {
            {
                try {
                    int addCount = 0;
                    if (Collection.CW09.equals(collection)) {
                        addCount = indexClueWeb09WarcFile();
                    } else if (Collection.CW12.equals(collection)) {
                        addCount = indexClueWeb12WarcFile();
                    } else if (Collection.GOV2.equals(collection)) {
                        addCount = indexGov2File();
                    } else if (Collection.TrecText.equals(collection)) {
                        addCount = indexTrecFile();
                    }
                    System.out.println("./" + inputFile.getParent().getFileName().toString() + File.separator + inputFile.getFileName().toString() + "\t" + addCount);
                } catch (IOException ioe) {
                    LOG.error(Thread.currentThread().getName() + ": ERROR: unexpected IOException:", ioe);
                }
            }
        }
    }

    private final Path indexPath;
    private final Path docDir;
    private final Collection collection;

    private boolean keepstopwords = false;

    public void setKeepstopwords(boolean keepstopwords) {
        this.keepstopwords = keepstopwords;
    }

    private boolean positions = false;

    public void setPositions(boolean positions) {
        this.positions = positions;
    }

    private boolean docVectors = false;

    public void setDocVectors(boolean docVectors) {
        this.docVectors = docVectors;
    }

    private boolean optimize = false;

    public void setOptimize(boolean optimize) {
        this.optimize = optimize;
    }

    private int doclimit = -1;

    public void setDocLimit(int doclimit) {
        this.doclimit = doclimit;
    }

    public IndexThreads(String docsPath, String indexPath, Collection collection) throws IOException {

        this.indexPath = Paths.get(indexPath);
        if (!Files.exists(this.indexPath))
            Files.createDirectories(this.indexPath);

        docDir = Paths.get(docsPath);
        if (!Files.exists(docDir) || !Files.isReadable(docDir) || !Files.isDirectory(docDir)) {
            System.out.println("Document directory '" + docDir.toString() + "' does not exist or is not readable, please check the path");
            System.exit(1);
        }

        this.collection = collection;
    }

    public int indexWithThreads(int numThreads) throws IOException, InterruptedException {

        LOG.info("Indexing with " + numThreads + " threads to directory '" + indexPath.toAbsolutePath() + "'...");

        final Directory dir = FSDirectory.open(indexPath);

        final EnglishAnalyzer ea = keepstopwords ? new EnglishAnalyzer(CharArraySet.EMPTY_SET) : new EnglishAnalyzer();
        final IndexWriterConfig iwc = new IndexWriterConfig(ea);

        iwc.setSimilarity(new BM25Similarity());
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        iwc.setRAMBufferSizeMB(512);
        iwc.setUseCompoundFile(false);
        iwc.setMergeScheduler(new ConcurrentMergeScheduler());

        final IndexWriter writer = new IndexWriter(dir, iwc);

        final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
        Set<String> skippedFilePrefix = new HashSet<>();
        Set<String> allowedFilePrefix = new HashSet<>();
        Set<String> skippedFileSuffix = new HashSet<>();
        Set<String> allowedFileSuffix = new HashSet<>();
        Set<String> skippedDirs = new HashSet<>();
        if (Collection.TrecText.equals(collection)) {
            skippedFilePrefix = TrecRecord.skippedFilePrefix;
            skippedDirs = TrecRecord.skippedDirs;
        } else if (Collection.GOV2.equals(collection)) {
            allowedFileSuffix = Gov2Record.allowedFileSuffix;
            skippedDirs = Gov2Record.skippedDirs;
        } else if (Collection.CW09.equals(collection) || Collection.CW12.equals(collection)) {
            allowedFileSuffix.add(".warc.gz");
            skippedDirs = Gov2Record.skippedDirs;
        }
        final Deque<Path> indexFiles = DiscoverFiles.discover(docDir, skippedFilePrefix, allowedFilePrefix,
                skippedFileSuffix, allowedFileSuffix, skippedDirs);

        if (doclimit > 0 && indexFiles.size() < doclimit)
            for (int i = doclimit; i < indexFiles.size(); i++)
                indexFiles.removeFirst();

        long totalFiles = indexFiles.size();
        LOG.info(totalFiles + " many files found under the docs path : " + docDir.toString());


        for (int i = 0; i < 2000; i++) {
            if (!indexFiles.isEmpty())
                executor.execute(new IndexerThread(writer, indexFiles.removeFirst()));
            else {
                if (!executor.isShutdown()) {
                    Thread.sleep(30000);
                    executor.shutdown();
                }
                break;
            }
        }

        long first = 0;
        //add some delay to let some threads spawn by scheduler
        Thread.sleep(30000);

        try {
            // Wait for existing tasks to terminate
            while (!executor.awaitTermination(1, TimeUnit.MINUTES)) {

                final long completedTaskCount = executor.getCompletedTaskCount();

                LOG.info(String.format("%.2f percentage completed", (double) completedTaskCount / totalFiles * 100.0d));

                if (!indexFiles.isEmpty())
                    for (long i = first; i < completedTaskCount; i++) {
                        if (!indexFiles.isEmpty())
                            executor.execute(new IndexerThread(writer, indexFiles.removeFirst()));
                        else {
                            if (!executor.isShutdown())
                                executor.shutdown();
                        }
                    }

                first = completedTaskCount;
                Thread.sleep(1000);
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            executor.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }

        if (totalFiles != executor.getCompletedTaskCount())
            throw new RuntimeException("totalFiles = " + totalFiles + " is not equal to completedTaskCount =  " + executor.getCompletedTaskCount());


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
}

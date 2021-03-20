/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.anserini.collection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

/**
 * <p>A classic TREC <i>ad hoc</i> document collection.</p>
 *
 * <p>This class handles a collection comprising files containing documents of the form:</p>
 *
 * <pre>
 * &lt;DOC&gt;
 * &lt;DOCNO&gt;doc1&lt;/DOCNO&gt;
 * &lt;TEXT&gt;
 * ...
 * &lt;/TEXT&gt;
 * &lt;/DOC&gt;
 * </pre>
 *
 * <p>This class also handles the following alternative format (e.g., for NTCIR-8 ACLIA):</p>
 * <pre>
 * &lt;DOC id="doc1"&gt;
 * &lt;TEXT&gt;
 * ...
 * &lt;/TEXT&gt;
 * &lt;/DOC&gt;
 * </pre>
 *
 * <p>In both cases, compressed files are transparently handled.</p>
 *
 * <p>This collection calls the {@link JsoupStringTransform} to remove tags in the document content.</p>
 */
public class C4Collection extends DocumentCollection<C4Collection.Document> {
    public C4Collection(Path path) {
        this.path = path;
    }

    @Override
    public FileSegment<C4Collection.Document> createFileSegment(Path p) throws IOException {
        return new Segment(p);
    }

    public static class Segment extends FileSegment<C4Collection.Document>{
        private MappingIterator<JsonNode> iterator; // iterator for JSON line objects
        private JsonNode node = null;
        private String fileName;
        private int count = 0;

        public Segment(Path path) throws IOException {
            super(path);
            fileName = path.toString();
            if (fileName.endsWith(".gz")) { //.gz
                InputStream stream = new GZIPInputStream(
                        Files.newInputStream(path, StandardOpenOption.READ), BUFFER_SIZE);
                bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            } else { // plain text file
                bufferedReader = new BufferedReader(new FileReader(fileName));
            }
            // reading as a json file
            ObjectMapper mapper = new ObjectMapper();
            // removes ctrl characters
            String filtered = bufferedReader.lines().map(line -> line.replaceAll("[\\p{C}]","")).collect(Collectors.joining());
            iterator = mapper.readerFor(JsonNode.class).readValues(filtered);
            if(iterator.hasNext()){
                node = iterator.next();
            }
        }

        @Override
        public void readNext() throws NoSuchElementException {
            if (node == null) {
                throw new NoSuchElementException("JsonNode is empty");
            } else {
                bufferedRecord = new C4Collection.Document(node, fileName, count);
                if (iterator.hasNext()) { // if bufferedReader contains JSON line objects, we parse the next JSON into node
                    node = iterator.next();
                    count++;
                } else {
                    atEOF = true; // there is no more JSON object in the bufferedReader
                }
            }
        }
    }

    public static class Document implements SourceDocument {
        private String id;
        private String contents;
        private String raw;
        private String url;
        private String timestamp;

        public Document(JsonNode json, String filename, int jsonLoc) {
            this.raw = json.toPrettyString();
            this.contents = json.get("text").asText();
            this.id = filename +  '-' + jsonLoc;
            this.url = json.get("url").asText();
            this.timestamp = json.get("timestamp").asText();
        }

        public String getUrl() {
            return url;
        }

        public String getTimestamp() {
            return timestamp;
        }

        @Override
        public String id() {
            if (id == null) {
                throw new RuntimeException("JSON document has no \"id\" field");
            }
            return id;
        }

        @Override
        public String contents() {
            if (contents == null) {
                throw new RuntimeException("JSON document has no \"contents\" field");
            }
            return contents;
        }

        @Override
        public String raw() {
            return raw;
        }

        @Override
        public boolean indexable() {
            return true;
        }
    }
}
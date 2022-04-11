/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AfribertaCollection extends DocumentCollection<AfribertaCollection.Document> {
    private static final Logger LOG = LogManager.getLogger(AfribertaCollection.class);

    public AfribertaCollection(Path path){
        this.path = path;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FileSegment<AfribertaCollection.Document> createFileSegment(Path p) throws IOException {
        return new Segment(p);
    }

//    public static void main(String[] args) throws IOException {
//
//        Path segment1 = Paths.get("collections/afriberta/06061756970746d77188461866c859523170731395217978372791bf0ccc22f5.zip");
//        Segment seg = new Segment(segment1);
//        seg.readNext();
//        seg.readNext();
//
//    }

    public static class Segment<T extends Document> extends FileSegment<T> {
        private JsonNode node = null;
        private List<JsonNode> jsonNodeArray = null;
        private Iterator<JsonNode> iter = null; // iterator for JSON document array
        private Iterator<JsonNode> iterator; // iterator for JSON line objects

        public Segment(Path path) throws IOException{
            super(path);

            ZipFile zip = new ZipFile(String.valueOf(path));
            for (Enumeration e = zip.entries(); e.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) e.nextElement();
                if (!entry.isDirectory()) {
                    if (FilenameUtils.getExtension(entry.getName()).equals("txt")) {
                        jsonNodeArray = getTxtFiles(zip.getInputStream(entry));
                    }
                }
            }

            iterator = jsonNodeArray.iterator();
            if (iterator.hasNext()) {
                node = iterator.next();
            }
        }

        private List<JsonNode> getTxtFiles(InputStream inputStream) {
            List<JsonNode> jsonNodeArray = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            try {
                int i=0;
                while ((line = reader.readLine()) != null) {
                    String json = "{ \"id\" : \"doc_"+i+"\", \"contents\" : \""+line.replaceAll("[^a-zA-Z0-9,’?()\\-: ]","")+"\" }";
                    JsonNode jsonNode = objectMapper.readTree(json);
                    jsonNodeArray.add(jsonNode);
                    i++;
                }
            } catch (IOException e) {
                LOG.error("Error: this is not a text file");
                e.printStackTrace();
            }
            return jsonNodeArray;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void readNext() throws NoSuchElementException {
            if (node == null) {
                throw new NoSuchElementException("JsonNode is empty");
            } else if (node.isObject()) {
                bufferedRecord = (T) createNewDocument(node);
                if (iterator.hasNext()) { // if bufferedReader contains JSON line objects, we parse the next JSON into node
                    node = iterator.next();
                } else {
                    atEOF = true; // there is no more JSON object in the bufferedReader
                }
            } else {
                LOG.error("Error: invalid JsonNode type");
                throw new NoSuchElementException("Invalid JsonNode type");
            }
        }

        protected AfribertaCollection.Document createNewDocument(JsonNode node) {
            return new AfribertaCollection.Document(node);
        }
    }

    /**
     * A document in a language corpus for AfriBERTa.
     */
    public static class Document implements SourceDocument{
        private String id;
        private String raw;
        private String contents;
        private Map<String, String> fields;


        public Document(JsonNode json) {
            this.raw = json.toPrettyString();
            this.fields = new HashMap<>();

            json.fields().forEachRemaining( e -> {
                if ("id".equals(e.getKey())) {
                    this.id = json.get("id").asText();
                } else if ("contents".equals(e.getKey())) {
                    this.contents = json.get("contents").asText();
                } else {
                    this.fields.put(e.getKey(), e.getValue().asText());
                }
            });
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public String contents() {
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
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

package io.anserini.doc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;

import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

import io.anserini.index.IndexInfo;
import io.anserini.index.IndexInfo.IndexType;

public class GeneratePrebuiltIndexesDocTest {
  @Test
  public void generateDocs() throws IOException {
    // Map from IndexType to list of IndexInfo entries
    Map<IndexType, Map<String, List<IndexInfo>>> grouped = new TreeMap<IndexType, Map<String, List<IndexInfo>>>(Comparator.comparing(IndexType::name));
    for (IndexInfo info : IndexInfo.values()) {
      String dataset = "Other";
      if (info.indexName.contains("msmarco")) {
        dataset = "MS MARCO";
      } else if (info.indexName.contains("beir")) {
        dataset = "BEIR";
      } else if (info.indexName.contains("bright")) {
        dataset = "BRIGHT";
      }
      IndexType type = info.indexType;
      if (info.indexType == IndexType.DENSE_HNSW_INT8) {
        type = IndexType.DENSE_HNSW; 
      } else if (info.indexType == IndexType.DENSE_FLAT_INT8) {
        type = IndexType.DENSE_FLAT; 
      }
      grouped.computeIfAbsent(type, k -> new HashMap<>());
      grouped.get(type).computeIfAbsent(dataset, k -> new ArrayList<>());
      grouped.get(type).get(dataset).add(info);
    }

    StringBuilder md = new StringBuilder();
    md.append("""
      # Anserini: Prebuilt Indexes

      Anserini ships with a number of prebuilt indexes.
      This means that various indexes (inverted indexes, HNSW indexes, etc.) for common collections used in NLP and IR research have already been built and just needs to be downloaded (from UWaterloo/Hugging Face servers), which Anserini will handle automatically for you.

      Bindings for the available prebuilt indexes are in [`io.anserini.index.IndexInfo`](https://github.com/castorini/anserini/blob/master/src/main/java/io/anserini/index/IndexInfo.java) and below.
      For example, if you specify `-index msmarco-v1-passage`, Anserini will know that you mean the Lucene index of the MS MARCO V1 passage corpus.
      It will then download the index from the servers and cache locally.
      All of this happens automagically!

      ## Getting Started

      To download a prebuilt index and view its statistics, you can use the following command:

      ```bash
      bin/run.sh io.anserini.index.IndexReaderUtils -index cacm -stats
      ```

      The output of the command will be:

      ```
      Index statistics
      ----------------
      documents:             3204
      documents (non-empty): 3204
      unique terms:          14363
      total terms:           320968
      ```

      Note that unless the underlying index was built with the `-optimize` option (i.e., merging all index segments into a single segment), `unique_terms` will show -1.
      Nope, that's not a bug.

      ## Managing Indexes

      The downloaded index will by default be in `~/.cache/pyserini/indexes/`.
      (Yes, `pyserini`; this is so prebuilt indexes from both Pyserini and Anserini can live in the same location.)
      You can specify a custom cache directory by setting the environment variable `$ANSERINI_INDEX_CACHE` or the system property `anserini.index.cache`.

      Another helpful tip is to download and manage the indexes by hand.
      All relevant information is stored in [`IndexInfo`](https://github.com/castorini/anserini/blob/master/src/main/java/io/anserini/index/IndexInfo.java).
      For example, `msmarco-v1-passage` can be downloaded from:

      ```
      https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/resolve/main/passage/original/lucene-inverted/tf/lucene-inverted.msmarco-v1-passage.20221004.252b5e.tar.gz
      ```

      and has an MD5 checksum of `678876e8c99a89933d553609a0fd8793`.
      You can download, verify, and put anywhere you want.
      With `-index /path/to/index/` you'll get exactly the same output as `-index msmarco-v1-passage`, except now you've got fine-grained control over managing the index.

      By manually managing the indexes, you can share indexes between multiple users to conserve space.
      The schema of the index location in `~/.cache/pyserini/indexes/` is the tarball name (after unpacking), followed by a dot and the checksum, so `msmarco-v1-passage` lives in following location:

      ```
      ~/.cache/pyserini/indexes/lucene-inverted.msmarco-v1-passage.20221004.252b5e.678876e8c99a89933d553609a0fd8793
      ```

      You can download the index once, put in a common location, and have each user symlink to the actual index location.
      Source would conform to the schema above, target would be where your index actually resides.

      ## Recovering from Partial Downloads

      A common issue is recovering from partial downloads, for example, if you abort the downloading of a large index tarball.
      In the standard flow, Anserini downloads the tarball from the servers, verifies the checksum, and then unpacks the tarball.
      If this process is interrupted, you'll end up in an inconsistent state.

      To recover, go to `~/.cache/pyserini/indexes/` or your custom cache directory and remove any tarballs (i.e., `.tar.gz` files).
      If there are any partially unpacked indexes, remove those also.
      Then start over (e.g., rerun the command you were running before).

      ## Available Prebuilt Indexes

      Below is a summary of the prebuilt indexes that are currently available.

      Note that this page is automatically generated from [this script](../src/test/java/io/anserini/doc/GeneratePrebuiltIndexesDocTest.java), so do not modify this page directly; modify the script instead.

    """);

    for (IndexType type : grouped.keySet()) {
      String typeHeading = "";
      if (type == IndexType.SPARSE_INVERTED) {
        typeHeading = "Lucene Standard Inverted Indexes";
      } else if (type == IndexType.SPARSE_IMPACT) {
        typeHeading = "Lucene Impact Indexes";
      } else if (type == IndexType.DENSE_HNSW) {
        typeHeading = "Lucene HNSW Indexes";
      } else if (type == IndexType.DENSE_FLAT) {
        typeHeading = "Lucene Flat Indexes";
      }

      md.append("### ").append(typeHeading).append("\n");
      for (String dataset : grouped.get(type).keySet()) {
        md.append("<details>\n");
        md.append("<summary>").append(dataset).append("</summary>\n").append("<dl>\n");
        for (IndexInfo info : grouped.get(type).get(dataset)) {
          md.append("<dt></dt><b><code>").append(info.indexName).append("</code></b>\n");
          md.append("[<a href=\"https://github.com/castorini/pyserini/blob/master/pyserini/resources/index-metadata/").append(info.readme).append("\">readme</a>]\n");   
          md.append("<dd>").append(info.description).append("\n</dd>\n");
        }
        md.append("</dl>\n</details>\n");
      }
      md.append("\n");
    }

    try (FileWriter writer = new FileWriter("docs/prebuilt-indexes.md")) {
      writer.write(md.toString());
    }
  }
}

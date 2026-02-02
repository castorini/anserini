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

import static org.junit.Assert.assertEquals;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

import io.anserini.index.prebuilt.PrebuiltFlatIndex;
import io.anserini.index.prebuilt.PrebuiltHnswIndex;
import io.anserini.index.prebuilt.PrebuiltImpactIndex;
import io.anserini.index.prebuilt.PrebuiltIndex;
import io.anserini.index.prebuilt.PrebuiltInvertedIndex;

public class GeneratePrebuiltIndexesDocTest {
  @Test
  public void generateDocs() throws IOException {
    md.append(renderIndexType("Standard Inverted Indexes", (List<? extends PrebuiltIndex.Entry>) PrebuiltInvertedIndex.entries()));
    md.append(renderIndexType("Impact Indexes", (List<? extends PrebuiltIndex.Entry>) PrebuiltImpactIndex.entries()));
    md.append(renderIndexType("Flat Vector Indexes", (List<? extends PrebuiltIndex.Entry>) PrebuiltFlatIndex.entries()));
    md.append(renderIndexType("HNSW Vector Indexes", (List<? extends PrebuiltIndex.Entry>) PrebuiltHnswIndex.entries()));

    try (FileWriter writer = new FileWriter("docs/prebuilt-indexes.md")) {
      writer.write(md.toString());
    }
  }

  private static int orderBucket(String name) {
    if (name.startsWith("msmarco-v1") && name.contains("passage")) {
      return 0;
    }
    if (name.startsWith("msmarco-v1") && name.contains("doc") && !name.contains("segmented")) {
      return 1;
    }
    if (name.startsWith("msmarco-v1") && name.contains("doc") && name.contains("segmented")) {
      return 2;
    }
    if (name.startsWith("msmarco-v2") && !name.contains("v2.1") && name.contains("passage")) {
      return 3;
    }
    if (name.startsWith("msmarco-v2") && !name.contains("v2.1") && !name.contains("segmented")) {
      return 4;
    }
    if (name.startsWith("msmarco-v2") && !name.contains("v2.1") && name.contains("segmented")) {
      return 5;
    }
    if (name.startsWith("msmarco-v2.1") && !name.contains("segmented")) {
      return 6;
    }
    if (name.startsWith("msmarco-v2.1") && name.contains("segmented")) {
      return 7;
    }
    if (name.startsWith("beir")) {
      return 8;
    }
    if (name.startsWith("bright")) {
      return 9;
    }
    return 10;
  }

  private String renderIndexType(String type, List<? extends PrebuiltIndex.Entry> entries) {
    List<PrebuiltIndex.Entry> order = new ArrayList<>(entries);
    order.sort(Comparator
        .comparingInt((PrebuiltIndex.Entry entry) -> orderBucket(entry.name))
        .thenComparing(entry -> entry.name));

    // Make sure we haven't left out any entries.
    assertEquals(entries.size(), order.size());

    StringBuilder sb = new StringBuilder();
    sb.append("### " + type + "\n\n");
    sb.append("<dl>\n");
    for (PrebuiltIndex.Entry entry : order) {
      sb.append(renderEntry(entry));
    }
    sb.append("</dl>\n\n");

    return sb.toString();
  }

  public String renderEntry(PrebuiltIndex.Entry entry) {
    StringBuffer sb = new StringBuffer();
    sb.append("<dt></dt><b><code>").append(entry.name).append("</code></b>\n");
    if (entry.readme != "") {
      sb.append("[<a href=\"").append(entry.readme).append("\">README</a>]\n");
    }
    sb.append("<dd>").append(entry.description).append("\n</dd>\n");

    return sb.toString();
  }

  private final StringBuilder md = new StringBuilder("""
    # Anserini: Prebuilt Indexes

    Anserini ships with a number of prebuilt indexes.
    This means that various indexes (inverted indexes, HNSW indexes, etc.) for common collections used in NLP and IR research have already been built and just needs to be downloaded (from UWaterloo and Hugging Face servers), which Anserini will handle automatically for you.

    Bindings for the available prebuilt indexes are in the package [`io.anserini.index.prebuilt`](https://github.com/castorini/anserini/tree/master/src/main/java/io/anserini/index/prebuilt).
    For example, if you specify `-index msmarco-v1-passage`, Anserini will know that you mean the Lucene index of the MS MARCO V1 passage corpus.
    It will then download the index from the specified location(s) and cache locally.
    All of this happens auto-magically!

    ## Getting Started

    To download a prebuilt index and view its statistics, you can use the following command:

    ```bash
    bin/run.sh io.anserini.index.IndexReaderUtils -index cacm -stats
    ```

    The output of the above command will be:

    ```text
    Index statistics
    ----------------
    documents:             3204
    documents (non-empty): 3204
    unique terms:          14363
    total terms:           320968
    index_path:            /home/jimmylin/.cache/pyserini/indexes/lucene-index.cacm.20221005.252b5e.cfe14d543c6a27f4d742fb2d0099b8e0
    total_size:            2.9 MB
    ```

    Note that for inverted indexes, unless the underlying index was built with the `-optimize` option (i.e., merging all index segments into a single segment), `unique_terms` will show -1.
    Nope, that's not a bug.

    ## Managing Indexes

    Downloaded indexes are by default stored in `~/.cache/pyserini/indexes/`.
    (Yes, `pyserini`, that's not a bug &mdash; this is so prebuilt indexes can be shared between Pyserini and Anserini.)
    You can specify a custom cache directory by setting the environment variable `$ANSERINI_INDEX_CACHE` or the system property `anserini.index.cache`.

    Another helpful tip is to download and manage the indexes by hand.
    As an example, from the [metadata](https://github.com/castorini/anserini/blob/master/src/main/resources/prebuilt-indexes/) you can see that `msmarco-v1-passage` can be downloaded from:

    ```text
    https://huggingface.co/datasets/castorini/prebuilt-indexes-msmarco-v1/resolve/main/passage/original/lucene-inverted/tf/lucene-inverted.msmarco-v1-passage.20221004.252b5e.tar.gz
    ```

    The tarball has an MD5 checksum of `678876e8c99a89933d553609a0fd8793`.
    
    You can download, verify, unpack, and put the index anywhere you want.
    With `-index /path/to/index/` you'll get exactly the same output as `-index msmarco-v1-passage`, except now you've got fine-grained control over managing the index.

    By manually managing indexes, you can share indexes between multiple users to conserve space.
    The schema of the index location in `~/.cache/pyserini/indexes/` is the tarball name (after unpacking), followed by a dot and the checksum, so `msmarco-v1-passage` lives in following location:

    ```text
    ~/.cache/pyserini/indexes/lucene-inverted.msmarco-v1-passage.20221004.252b5e.678876e8c99a89933d553609a0fd8793
    ```

    You can download the index once, put in a common location, and have each user symlink to the actual index location.
    The source of the symlink would conform to the schema above, and the target of the symlink would be where your index actually resides.

    ## Recovering from Partial Downloads

    A common issue is recovering from partial downloads, for example, if you abort the downloading of a large index tarball.
    In the standard flow, Anserini downloads the tarball from the servers, verifies the checksum, and then unpacks the tarball.
    If this process is interrupted, you'll end up in an inconsistent state.

    To recover, go to `~/.cache/pyserini/indexes/` or your custom cache directory and remove any tarballs (i.e., `.tar.gz` files).
    If there are any partially unpacked indexes, remove those also.
    Then start over (e.g., rerun the command you were running before).

    ## Available Prebuilt Indexes

    Below is a summary of the prebuilt indexes that are currently available.

    Note that this page is automatically generated from [this test case](../src/test/java/io/anserini/doc/GeneratePrebuiltIndexesDocTest.java).
    This means that the page is updated with every (successful) build.
    Therefore, do not modify this page directly; modify the test case instead.

    """);
}

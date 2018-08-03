/**
 * Anserini: An information retrieval toolkit built on Lucene
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

package io.anserini.util.mapper;

import io.anserini.collection.SourceDocument;
import io.anserini.util.MapCollections;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class CountDocumentMapper extends DocumentMapper {

  private static final Logger LOG = LogManager.getLogger(CountDocumentMapper.class);

  public final static class Counters {
    /**
     * Counter for successfully indexed documents.
     */
    public AtomicLong indexed = new AtomicLong();

    /**
     * Counter for empty documents that are not indexed. Empty documents are not necessary errors;
     * it could be the case, for example, that a document is comprised solely of stopwords.
     */
    public AtomicLong empty = new AtomicLong();

    /**
     * Counter for unindexable documents. These are cases where {@link SourceDocument#indexable()}
     * returns false.
     */
    public AtomicLong unindexable = new AtomicLong();

    /**
     * Counter for skipped documents. These are cases documents are skipped as part of normal
     * processing logic, e.g., using a whitelist, not indexing retweets or deleted tweets.
     */
    public AtomicLong skipped = new AtomicLong();

    /**
     * Counter for unexpected errors.
     */
    public AtomicLong errors = new AtomicLong();
  }

  private final Counters counters;
  private final Set whitelistDocids;

  public CountDocumentMapper(MapCollections.Args args) throws Exception {
    super(args);
    this.counters = new CountDocumentMapper.Counters();

    if (args.whitelist != null) {
      List<String> lines = FileUtils.readLines(new File(args.whitelist), "utf-8");
      this.whitelistDocids = new HashSet<>(lines);
    } else {
      this.whitelistDocids = null;
    }
  }

  public void incrementErrors() {
    counters.errors.incrementAndGet();
  }

  public void incrementSkipped() {
    counters.skipped.incrementAndGet();
  }

  public void incrementUnindexable() {
    counters.unindexable.incrementAndGet();
  }

  public void incrementEmpty() {
    counters.empty.incrementAndGet();
  }

  public void incrementIndexed() {
    counters.indexed.incrementAndGet();
  }

  public void incrementIndexedBy(int numIndexed) {
    counters.indexed.addAndGet(numIndexed);
  }

  public boolean process(SourceDocument d) {
    if (!d.indexable()) {
      incrementUnindexable();
      return false;
    }

    if (whitelistDocids != null && !whitelistDocids.contains(d.id())) {
      incrementSkipped();
      return false;
    }

    incrementIndexed();
    return true;
  }

  public void printResult(long durationMillis) {
    LOG.info("# Final Counter Values");
    LOG.info(String.format("indexed:     %,12d", counters.indexed.get()));
    LOG.info(String.format("empty:       %,12d", counters.empty.get()));
    LOG.info(String.format("unindexable: %,12d", counters.unindexable.get()));
    LOG.info(String.format("skipped:     %,12d", counters.skipped.get()));
    LOG.info(String.format("errors:      %,12d", counters.errors.get()));
    LOG.info(String.format("Total %,d documents indexed in %s", counters.indexed.get(),
            DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss")));
  }
}

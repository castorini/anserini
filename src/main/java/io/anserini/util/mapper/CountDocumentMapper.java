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

public class CountDocumentMapper extends DocumentMapper {
  private static final Logger LOG = LogManager.getLogger(CountDocumentMapper.class);

  private CountDocumentMapperContext counters;
  private final Set whitelistDocids;

  public CountDocumentMapper(MapCollections.Args args) throws Exception {
    super(args);

    if (args.whitelist != null) {
      List<String> lines = FileUtils.readLines(new File(args.whitelist), "utf-8");
      this.whitelistDocids = new HashSet<>(lines);
    } else {
      this.whitelistDocids = null;
    }
  }

  @Override
  public void setContext(DocumentMapperContext context) {
    this.counters = (CountDocumentMapperContext) context;
  }

  @Override
  public void process(SourceDocument doc, DocumentMapperContext context) {
    if (!doc.indexable()) {
      ((CountDocumentMapperContext) context).unindexable.incrementAndGet();
      return;
    }

    if (whitelistDocids != null && !whitelistDocids.contains(doc.id())) {
      ((CountDocumentMapperContext) context).skipped.incrementAndGet();
      return;
    }

    ((CountDocumentMapperContext) context).processed.incrementAndGet();
  }

  public void printResult(long durationMillis) {
    LOG.info("# Final Counter Values");
    LOG.info(String.format("processed:   %,12d", counters.processed.get()));
    LOG.info(String.format("unindexable: %,12d", counters.unindexable.get()));
    LOG.info(String.format("skipped:     %,12d", counters.skipped.get()));
    LOG.info(String.format("Total %,d documents processed in %s", counters.processed.get(),
            DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss")));
  }
}

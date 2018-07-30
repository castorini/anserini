package io.anserini.util.mapper;

import io.anserini.util.MapCollections;

public class CountDocumentMapper extends DocumentMapper {

  protected MapCollections.Counters counters;

  public CountDocumentMapper(MapCollections.Counters counters) {
    this.counters = counters;
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
}

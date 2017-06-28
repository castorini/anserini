package io.anserini.collection;

import io.anserini.document.TrecCoreDocument;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class representing an instance of a TREC Core collection.
 */
public class TrecCoreCollection extends Collection<TrecCoreDocument> {
  public class FileSegment extends Collection.FileSegment {
    private String fileName;

    protected FileSegment(Path path) throws IOException {
      this.path = path;
      this.fileName = path.toString();
    }

    @Override
    public void close() throws IOException {
      atEOF = false;
    }

    @Override
    public boolean hasNext() {
      return !atEOF;
    }

    @Override
    public TrecCoreDocument next() {
      TrecCoreDocument doc = new TrecCoreDocument();
      atEOF = true;
      try {
        doc = (TrecCoreDocument) doc.readNextRecord(new File(fileName));
      } catch (IOException e) {
        doc = null;
      }
      return doc;
    }
  }

  @Override
  public List<Path> getFileSegmentPaths() {
    Set<String> allowedFileSuffix = new HashSet<>(Arrays.asList(".xml"));

    return discover(path, EMPTY_SET, EMPTY_SET, EMPTY_SET,
            allowedFileSuffix, EMPTY_SET);
  }

  @Override
  public FileSegment createFileSegment(Path p) throws IOException {
    return new FileSegment(p);
  }

}

package io.anserini.collection;

import io.anserini.document.TwitterDocument;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;

/**
 * Class representing an instance of a TREC Microblog collection.
 */
public class TwitterCollection extends Collection<TwitterDocument> {
  public class FileSegment extends Collection.FileSegment {
    protected BufferedReader bufferedReader;
    protected final int BUFFER_SIZE = 1 << 16; // 64K

    protected FileSegment(Path path) throws IOException {
      this.path = path;
      this.bufferedReader = null;
      String fileName = path.toString();
      if (fileName.endsWith(".gz")) {
        InputStream stream = new GZIPInputStream(
                Files.newInputStream(path, StandardOpenOption.READ), BUFFER_SIZE);
        bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
      } else {
        // in case user had already uncompressed the folder
        bufferedReader = new BufferedReader(new FileReader(fileName));
      }
    }


    @Override
    public void close() throws IOException {
      atEOF = false;
      if (bufferedReader != null) {
        bufferedReader.close();
      }
    }

    @Override
    public boolean hasNext() {
      return !atEOF;
    }

    @Override
    public TwitterDocument next() {

      TwitterDocument doc = new TwitterDocument();
      String raw = null;

      try {
        raw = bufferedReader.readLine();
      } catch (IOException e) {
        e.printStackTrace();
      }

      // check to see if we've reached end of file.
      if (raw == null) {
        atEOF = true;
        return null;
      }

      try {
        doc = (TwitterDocument) doc.readNextRecord(raw);

        if (doc == null) {
          return null;
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      return  doc;
    }
  }

  @Override
  public List<Path> getFileSegmentPaths() {
    Set<String> allowedFileSuffix = new HashSet<>(Arrays.asList(".gz"));

    return discover(path, EMPTY_SET, EMPTY_SET, EMPTY_SET,
            allowedFileSuffix, EMPTY_SET);
  }

  @Override
  public FileSegment createFileSegment(Path p) throws IOException {
    return new FileSegment(p);
  }

}

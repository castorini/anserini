package io.anserini.collection;

import io.anserini.document.SourceDocument;
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

import org.apache.commons.compress.compressors.z.ZCompressorInputStream;

/**
 * Class representing an instance of a Twitter collection.
 */
public class TwitterCollection<D extends TwitterDocument> extends Collection {
  protected boolean keepRetweets = false;

  public TwitterCollection(Boolean keepRetweets) {
    this.keepRetweets = keepRetweets;
  }

  public class FileSegment extends Collection.FileSegment {
    protected FileSegment(Path path) throws IOException {
      super();
      dType = new TwitterDocument(keepRetweets);

      this.path = path;
      this.bufferedReader = null;
      String fileName = path.toString();
      if (fileName.matches("(?i:.*?\\.\\d*z$)")) { // .z .0z .1z .2z
        FileInputStream fin = new FileInputStream(fileName);
        BufferedInputStream in = new BufferedInputStream(fin);
        ZCompressorInputStream zIn = new ZCompressorInputStream(in);
        bufferedReader = new BufferedReader(new InputStreamReader(zIn, StandardCharsets.UTF_8));
      } else if (fileName.endsWith(".gz")) { //.gz
        InputStream stream = new GZIPInputStream(
            Files.newInputStream(path, StandardOpenOption.READ), BUFFER_SIZE);
        bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
      } else { // plain text file
        bufferedReader = new BufferedReader(new FileReader(fileName));
      }
    }
  }

  @Override
  public List<Path> getFileSegmentPaths() {
    Set<String> allowedFileSuffix = new HashSet<>(Arrays.asList(".gz"));

    return discover(path, EMPTY_SET, EMPTY_SET, EMPTY_SET, allowedFileSuffix, EMPTY_SET);
  }

  @Override
  public FileSegment createFileSegment(Path p) throws IOException {
    return new FileSegment(p);
  }

}

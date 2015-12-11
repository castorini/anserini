package io.anserini.document.twitter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.zip.GZIPInputStream;

import com.google.common.base.Preconditions;

public class JsonTweetsBlock implements Iterable<Status> {
  private final BufferedReader br;

  public JsonTweetsBlock(File file) throws IOException {
    Preconditions.checkNotNull(file);

    if (!file.getName().endsWith(".gz")) {
      throw new IOException("Expecting .gz compressed file!");
    }

    br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file)), "UTF-8"));
  }

  public JsonTweetsBlock(InputStream stream) throws IOException {
    Preconditions.checkNotNull(stream);

    br = new BufferedReader(new InputStreamReader(stream));
  }

  public void close() throws IOException {
    br.close();
  }

  @Override
  public Iterator<Status> iterator() {
    return new Iterator<Status>() {
      private Status currentStatus = null;

      @Override
      public boolean hasNext() {
        if (currentStatus != null) {
          return true;
        }

        String raw = null;
        while (currentStatus == null) {
          try {
            raw = br.readLine();
          } catch (IOException e) {
            return false;
          }

          // Check to see if we've reached end of file.
          if (raw == null) {
            return false;
          }

          currentStatus = Status.fromJson(raw);
        }

        return true;
      }

      @Override
      public Status next() {
        if (currentStatus != null) {
          Status tmp = currentStatus;
          currentStatus = null;
          return tmp;
        }

        if (!hasNext()) {
          throw new NoSuchElementException();
        }

        Status tmp = currentStatus;
        currentStatus = null;
        return tmp;
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }
}

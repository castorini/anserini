package io.anserini.document.twitter;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Iterator;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import com.google.common.base.Preconditions;

public class JsonTweetsCollection implements Iterable<Status> {
  private JsonTweetsBlock curBlock = null;
  private Iterator<Status> curIter = null;
  private final File[] files;
  private int nextFile = 0;

  public JsonTweetsCollection(File file) throws IOException {
    Preconditions.checkNotNull(file);

    if (!file.isDirectory()) {
      throw new IOException("Expecting " + file + " to be a directory!");
    }

    files = file.listFiles(new FileFilter() {
      public boolean accept(File path) {
        return path.getName().endsWith(".gz") ? true : false;
      }
    });

    if (files.length == 0) {
      throw new IOException(file + " does not contain any .gz files!");
    }
  }

  @Override
  public Iterator<Status> iterator() {
    return new Iterator<Status>() {
      @Override
      public boolean hasNext() {
        if (curBlock == null) {
          try {
            curBlock = new JsonTweetsBlock(files[nextFile]);
            curIter = curBlock.iterator();
            nextFile++;
          } catch (IOException e) {
            return false;
          }
        }

        while (true) {
          if (curIter.hasNext())
            return true;

          if (nextFile >= files.length) {
            // We're out of files to read. Must be the end of the collection.
            return false;
          }

          try {
            curBlock.close();
            // Move to next file.
            curBlock = new JsonTweetsBlock(files[nextFile]);
            curIter = curBlock.iterator();

            nextFile++;
          } catch (IOException e) {
            return false;
          }

          return true;
        }
      }

      @Override
      public Status next() {
        return curIter.next();
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  public void close() throws IOException {
    curBlock.close();
  }

  public static class Args {
    @Option(name = "-input", metaVar = "[path]", required = true, usage = "input path")
    public String input;
  }

  public static void main(String[] argv) throws IOException {
    Args args = new Args();
    CmdLineParser parser = new CmdLineParser(args, ParserProperties.defaults().withUsageWidth(100));

    try {
      parser.parseArgument(argv);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.exit(-1);
    }

    long minId = Long.MAX_VALUE, maxId = Long.MIN_VALUE, cnt = 0;
    JsonTweetsCollection tweets = new JsonTweetsCollection(new File(args.input));
    for (Status tweet : tweets) {
      cnt++;
      long id = tweet.getId();

      System.out.println("id: " + id);

      if (id < minId) minId = id;
      if (id > maxId) maxId = id;

      if ( cnt % 100000 == 0) {
        System.out.println("Read " + cnt + " tweets");
      }
    }
    tweets.close();

    System.out.println("Read " + cnt + " in total.");
    System.out.println("MaxId = " + maxId);
    System.out.println("MinId = " + minId);
  }
}

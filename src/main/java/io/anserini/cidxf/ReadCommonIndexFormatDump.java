package io.anserini.cidxf;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import java.io.FileInputStream;

public class ReadCommonIndexFormatDump {
  public static class Args {
    @Option(name = "-postings", metaVar = "[file]", required = true, usage = "postings file")
    public String postings = "";

    @Option(name = "-max", metaVar = "[int]", usage = "maximum number of postings to write")
    public int max = 10;
  }

  public static void main(String[] argv) throws Exception {
    Args args = new Args();
    CmdLineParser parser = new CmdLineParser(args, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(argv);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: Eval " + parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    FileInputStream fileIn = new FileInputStream(args.postings);
    for (int i=0; i<args.max; i++ ) {
      CommonIndexFormat.PostingsList pl = CommonIndexFormat.PostingsList.parseDelimitedFrom(fileIn);
      System.out.print(String.format("term: '%s', df=%d, cf=%d", pl.getTerm(), pl.getDf(), pl.getCf()));

      if (pl.getDf() != pl.getPostingCount()) {
        throw new RuntimeException(String.format(
            "Unexpected number of postings! expected %d got %d", pl.getDf(), pl.getPostingCount()));
      }

      for (int j=0; j< (pl.getDf() > 10 ? 10 : pl.getDf()); j++) {
        System.out.print(String.format(" (%d, %d)", pl.getPosting(j).getDocid(), pl.getPosting(j).getTf()));
      }
      System.out.println("");
    }
    fileIn.close();
  }
}

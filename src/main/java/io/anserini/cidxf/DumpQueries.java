package io.anserini.cidxf;

import io.anserini.analysis.AnalyzerUtils;
import io.anserini.index.IndexCollection;
import io.anserini.search.topicreader.TopicReader;
import io.anserini.search.topicreader.Topics;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public class DumpQueries {
  public static class Args {
    @Option(name = "-queries", metaVar = "[file]", required = true, usage = "queries")
    public String queries = "";

    @Option(name = "-output", metaVar = "[file]", required = true, usage = "queries")
    public String output = "";
  }

  public static void main(String[] argv) throws IOException {
    Args args = new Args();
    CmdLineParser parser = new CmdLineParser(args, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(argv);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: DumpQueries " + parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    SortedMap<?, Map<String, String>> topics =
        TopicReader.getTopics(Enum.valueOf(Topics.class, args.queries));

    FileOutputStream out = new FileOutputStream(args.output);
    for (Map.Entry<?, Map<String, String>> entry : topics.entrySet()) {
      List<String> tokens = AnalyzerUtils.tokenize(IndexCollection.DEFAULT_ANALYZER, entry.getValue().get("title"));
      out.write((entry.getKey() + "\t" + StringUtils.join(tokens, " ") + "\n").getBytes());
    }
    out.close();
  }
}
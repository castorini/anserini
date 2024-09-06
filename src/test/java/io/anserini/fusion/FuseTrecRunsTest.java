package io.anserini.fusion;

import java.io.IOException;

import static org.junit.Assert.fail;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ParserProperties;

public class FuseTrecRunsTest {
  
  @Test
  public void testFuseTrecRunsRRF() throws IOException {
    String[] args = {
      "-runs", "runs/testlong/run.neuclir22-zh-en-splade.splade.topics.neuclir22-en.splade.original-desc_title.txt", "runs/testlong/run.neuclir22-zh-en-splade.splade.topics.neuclir22-en.splade.original-desc.txt",
        "runs/testlong/run.neuclir22-zh-en-splade.splade.topics.neuclir22-en.splade.original-title.txt",
      "-output", "runs/testsrc/test/resources/fused_output.txt",
      "-rrf_k", "60",
      "-k", "1000",
      "-depth", "1000",
      "-resort"
    };

    FuseTrecRuns.Args fuseArgs = new FuseTrecRuns.Args();
    CmdLineParser parser = new CmdLineParser(fuseArgs, ParserProperties.defaults().withUsageWidth(120));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      fail("Argument parsing failed: " + e.getMessage());
    }

    FuseTrecRuns fuseTrecRuns = new FuseTrecRuns(fuseArgs);
    fuseTrecRuns.run();

    // Assert the existence of the output file
    // assertTrue("Output file should exist", Paths.get("runs/testsrc/test/resources/fused_output.txt").toFile().exists());

    // Further assertions on the output can be made by reading and validating the contents.
  }
}

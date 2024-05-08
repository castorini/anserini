/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
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
package io.anserini.fusion;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.NotImplementedException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

class FusedRunOutputWriter implements Closeable {
  private final PrintWriter out;
  private final String format;
  private final String runtag;

  public FusedRunOutputWriter(String output, String format, String runtag) throws IOException {
    this.out = new PrintWriter(Files.newBufferedWriter(Paths.get(output), StandardCharsets.UTF_8));
    this.format = format;
    this.runtag = runtag;
  }

  private class Document implements Comparable<Document>{
    String docid;
    double score;

    public Document(String docid, double score)
    {
      this.docid = docid;
      this.score = score;
    }

    @Override public int compareTo(Document a)
    {
      return Double.compare(a.score,this.score);
    }

  }

  public void writeTopic(String qid, HashMap<String,Double> results) {
    int rank = 1;
    ArrayList<Document> documents = new ArrayList<>();

    for (Map.Entry<String, Double> entry : results.entrySet()) {
      documents.add(new Document(entry.getKey(), entry.getValue()));
    }
    Collections.sort(documents);   
    for (Document r : documents) {
      if ("msmarco".equals(format)) {
        // MS MARCO output format:
        out.append(String.format(Locale.US, "%s\t%s\t%d\n", qid, r.docid, rank));
      } else {
        // Standard TREC format:
        // + the first column is the topic number.
        // + the second column is currently unused and should always be "Q0".
        // + the third column is the official document identifier of the retrieved document.
        // + the fourth column is the rank the document is retrieved.
        // + the fifth column shows the score (integer or floating point) that generated the ranking.
        // + the sixth column is called the "run tag" and should be a unique identifier for your
        out.append(String.format(Locale.US, "%s Q0 %s %d %f %s\n", qid, r.docid, rank, r.score, runtag));
      }
      rank++;
    }
  }

  @Override
  public void close() {
    out.flush();
    out.close();
  }
}

// Used to hold the score and the rank of a document
class DocScore{
  public Double score;
  public int initialRank; 

  public DocScore(Double score, int initialRank) {
    this.score = score;
    this.initialRank = initialRank;
  }
}

public class FuseRuns {

  public static class Args {
    @Option(name = "-options", usage = "Print information about options.")
    public Boolean options = false;

    @Option(name = "-runs", handler = StringArrayOptionHandler.class, metaVar = "", required = true, 
        usage = "Path to both run files to fuse")
    public String[] runs = new String[]{};
    @Option(name = "-output", metaVar = "[output]", required = true, usage = "Path to save the output")
    public String output;

    @Option(name = "-runtag", metaVar = "[runtag]", usage = "Run tag for the fusion")
    public String runtag = "fused";

    @Option(name = "-method", metaVar = "[method]", required = false, usage = "Specify fusion method")
    public String method = "default";

    @Option(name = "-rrf_k", metaVar = "[rrf_k]", required = false, usage = "Parameter k needed for reciprocal rank fusion.")
    public double rrf_k = 60;

    @Option(name = "-alpha", required = false, usage = "Alpha value used for interpolation.")
    public double alpha = 0.5;

    @Option(name = "-k", required = false, usage = "number of documents to output for topic")
    public int k = 1000;

    @Option(name = "-depth", required = false, usage = "Pool depth per topic.")
    public int depth = 1000;

    @Option (name = "-resort", usage="We Resort the Trec run files or not")
    public boolean resort = false;


  }

  public static TreeMap<String, HashMap<String,DocScore>> createRunMap(String filename) throws FileNotFoundException, IOException {
    TreeMap<String, HashMap<String, DocScore>> twoTierHashMap = new TreeMap<>();
    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] data = line.split(" ");
        HashMap<String, DocScore> innerHashMap = new HashMap<String,DocScore>();
        HashMap<String, DocScore> innerHashMap_ = twoTierHashMap.putIfAbsent(data[0], innerHashMap);
        if (innerHashMap_ != null){
          innerHashMap = innerHashMap_;
        }
        innerHashMap.put(data[2], new DocScore(Double.valueOf(data[4]), Integer.parseInt(data[3])));
      }
    } catch (FileNotFoundException ex){
      throw ex;
    } catch (IOException ex){
      throw ex;
    }
    return twoTierHashMap;
  }

  public static void main(String[] args) {
    Args fuseArgs = new Args();
    CmdLineParser parser = new CmdLineParser(fuseArgs, ParserProperties.defaults().withUsageWidth(120));

    // parse argumens
    try {
      parser.parseArgument(args);
      // TODO: THESE CONSTRUCTORS ARE DEPRECATED
      if(fuseArgs.runs.length != 2) {
        throw new CmdLineException(parser, "Option run expects exactly 2 files"); 
      } else if (fuseArgs.depth <= 0) {
        throw new CmdLineException(parser, "Option depth must be greater than 0"); 
      } else if (fuseArgs.k <= 0) {
        throw new CmdLineException(parser, "Option k must be greater than 0"); 
      }
    } catch (CmdLineException e) {
      if (fuseArgs.options) {
        System.err.printf("Options for %s:\n\n", FuseRuns.class.getSimpleName());
        parser.printUsage(System.err);

        ArrayList<String> required = new ArrayList<String>();
        parser.getOptions().forEach((option) -> {
          if (option.option.required()) {
            required.add(option.option.toString());
          }
        });

        System.err.printf("\nRequired options are %s\n", required);
      } else {
        System.err.printf("Error: %s. For help, use \"-options\" to print out information about options.\n",
        e.getMessage());
      }
      return;
    }


    try {
      // TreeMap<docid, HashMap<topic,score>> 
      TreeMap<String,HashMap<String, DocScore>> runA = createRunMap(fuseArgs.runs[0]);
      TreeMap<String,HashMap<String, DocScore>> runB = createRunMap(fuseArgs.runs[1]);
      
      TreeMap<String, HashMap<String, Double>> finalRun;

      if (fuseArgs.method.equals(FusionMethods.AVERAGE)) {
        finalRun = FusionMethods.average(runA, runB, fuseArgs.depth, fuseArgs.k);
      } else if (fuseArgs.method.equals(FusionMethods.RRF)) {
        finalRun = FusionMethods.reciprocal_rank_fusion(runA, runB, fuseArgs.rrf_k, fuseArgs.depth, fuseArgs.k);
      } else if (fuseArgs.method.equals(FusionMethods.INTERPOLATION)) {
        finalRun = FusionMethods.interpolation(runA, runB, fuseArgs.alpha, fuseArgs.depth, fuseArgs.k);
      } else {
        throw new NotImplementedException("This method has not yet been implemented: " + fuseArgs.method);
      }

      FusedRunOutputWriter out = new FusedRunOutputWriter(fuseArgs.output, "trec", fuseArgs.runtag);
      for (String key : finalRun.keySet()) {
        out.writeTopic(key, finalRun.get(key));
      }
      out.close();
      System.out.println("File " + fuseArgs.output + " was succesfully created");

    } catch (IOException e) {
      System.err.println("Error occured: " + e.getMessage());
    } catch (NotImplementedException e) {
      System.err.println("Error occured: " + e.getMessage());
    }
  }
}


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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.LocaleUtils;
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

    // Currently useless, will eventually be needed to match pyserini's fusion implementation
    // @Option(name = "-method", metaVar = "[method]", required = false, usage = "Specify fusionm method")
    // public String method = "default";
    //
    // @Option(name = "-rrf_k", metaVar = "[rrf_k]", required = false, usage = "Parameter k needed for reciprocal rank fusion.")
    // public int rrf_k = 60;
    //
    // @Option(name = "-alpha", required = false, usage = "Alpha value used for interpolation.")
    // public double alpha = 0.5;
    //
    // @Option(name = "-k", required = false, usage = "Alpha value used for interpolation")
    // public int k = 1000;
    //
    // @Option (name = "-resort", usage="We Resort the Trec run files or not")
    // public boolean resort = false;
    //

  }

  public static TreeMap<String,HashMap<String,Double>> createRunMap(String filename){
    TreeMap<String, HashMap<String, Double>> twoTierHashMap = new TreeMap<>();
    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] data = line.split(" ");
        HashMap<String, Double> innerHashMap = new HashMap<String,Double>();
        HashMap<String, Double> innerHashMap_ = twoTierHashMap.putIfAbsent(data[0], innerHashMap);
        if (innerHashMap_ != null){
          innerHashMap = innerHashMap_;
        }
        innerHashMap.put(data[2], Double.valueOf(data[4]));
      }
    } catch (FileNotFoundException ex){
      System.out.println(ex);
    } catch (IOException ex){
      System.out.println(ex);
    }
    return twoTierHashMap;
  }

  public static void normalize_min_max(TreeMap<String, HashMap<String, Double>> hashMap) {
    for (String outerKey : hashMap.keySet()) {
      Map<String, Double> innerHashMap = hashMap.get(outerKey);
      Double min = Double.MAX_VALUE;
      Double max = -1.0;
      for (String innerKey : innerHashMap.keySet()) {
        Double innerValue = innerHashMap.get(innerKey);
        if (innerValue < min) {
          min = innerValue;
        } 
        if (innerValue > max) {
          max = innerValue;
        }
      }
      for (String innerKey : innerHashMap.keySet()) {
          Double innerValue = innerHashMap.get(innerKey);
          Double newValue = (innerValue - min) / (max-min);
          innerHashMap.replace(innerKey,innerValue,newValue);
      }
    }
  }    

  public static HashMap<String,Double> aggregateQuery(HashMap<String, Double> hashMap1, HashMap<String, Double> hashMap2) {
    HashMap<String,Double> mergedHashMap = new HashMap<String,Double>();
    for (String key : hashMap1.keySet()) {
      mergedHashMap.put(key, hashMap1.get(key));
    }
    for (String key : hashMap2.keySet()) {
      Double existingValue = mergedHashMap.getOrDefault(key,0.0);
      mergedHashMap.put(key, hashMap2.get(key) + existingValue);
    }
    return mergedHashMap;
  }    

  public static TreeMap<String,HashMap<String, Double>> aggregateHashMap(TreeMap<String,HashMap<String, Double>> hashMap1, TreeMap<String,HashMap<String, Double>> hashMap2) {
    Set<String> queries = new HashSet<String>();
    TreeMap<String,HashMap<String, Double>> finalHashMap = new TreeMap<String,HashMap<String, Double>>();
    for (String key : hashMap1.keySet()) {
      queries.add(key);
    }
    for (String key : hashMap2.keySet()) {
      queries.add(key);
    }
    Iterator<String> queryIterator = queries.iterator();
    while(queryIterator.hasNext()) {
      String query = queryIterator.next();
      HashMap<String,Double> aggregated = aggregateQuery(hashMap1.getOrDefault(query,new HashMap<String,Double>()), hashMap2.getOrDefault(query,new HashMap<String,Double>()));
      finalHashMap.put(query,aggregated);
    }
    return finalHashMap;
  }    

  public static void main(String[] args) {
    Args fuseArgs = new Args();
    CmdLineParser parser = new CmdLineParser(fuseArgs, ParserProperties.defaults().withUsageWidth(120));

    // parse argumens
    try {
      parser.parseArgument(args);
      if(fuseArgs.runs.length != 2) {
        // TODO: THIS CONSTRUCTOR IS DEPRECATED
        throw new CmdLineException(parser, "Expects exactly 2 run files"); 
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
      TreeMap<String,HashMap<String, Double>> runA = createRunMap(fuseArgs.runs[0]);
      TreeMap<String,HashMap<String, Double>> runB = createRunMap(fuseArgs.runs[1]);
      normalize_min_max(runA);
      normalize_min_max(runB);
      
      TreeMap<String, HashMap<String, Double>> finalRun = aggregateHashMap(runA, runB);

      FusedRunOutputWriter out = new FusedRunOutputWriter(fuseArgs.output, "trec", fuseArgs.runtag);
      for (String key : finalRun.keySet()) {
        out.writeTopic(key, finalRun.get(key));
      }
      out.close();
      System.out.println("File " + fuseArgs.output + " was succesfully created");

    } catch (IOException e) {
      System.out.println("Error occured: " + e.getMessage());
    }

  }
}


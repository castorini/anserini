package io.anserini.ltr;

import io.anserini.util.Qrels;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of RPB metric as described in Moffat and Zobel 08
 *
 */
public class RBPEvaluator {

  // Default:
  private double p = 0.5;
  // precision of the metric, we stop look at the other documents if
  // its score contribution doesn't exceed this value
  private double epsilon = 0.0001;

  private double totalResidual = 0.0d;
  private double totalScore = 0.0d;
  private int qidsProcessed = 0;
  private HashMap<Integer, Integer> unjudgedCounts = new HashMap<>(3);

  public RBPEvaluator(double p) {
    this.p = p;
  }
  // We will iterate through each to check if the document is relevant or not
  // This method will output both the rpb score, a count of unjudged at depths 5,10,15
  // and also the residual as computed by p^d d is the last judged document
  private void outputRpb(Qrels qrels, List<String> docIds, String qid, PrintStream out) {
    // Start with we haven't even encountered any judged
    out.println(String.format("Computing stat for qid: %s", qid));
    qidsProcessed++;
    int lastJudgedDoc = 0;
    int numUnjudged = 0;
    double score = 0.0;
    for (int i = 0; i < docIds.size(); i++) {
      String docId = docIds.get(i);

      if (qrels.isDocJudged(qid, docId)) {
        lastJudgedDoc = i;
      } else {
        numUnjudged ++;
      }

      if (i == 4 || i == 9 || i == 14) {
        out.println(String.format("Unjudged at %d is %d", i+1, numUnjudged));
        if (!this.unjudgedCounts.containsKey(i +1)) {
          this.unjudgedCounts.put(i+1, numUnjudged);
        } else {
          this.unjudgedCounts.put(i+1, this.unjudgedCounts.get(i+1)+ numUnjudged);
        }
      }

      int qrel = qrels.getRelevanceGrade(qid, docId);
      if (qrel > 0) {
        score += Math.pow(this.p, i);
      }
    }

    score = (1 - this.p) * score;
    out.println(String.format("RBP Score: %.3f", score));
    out.println(String.format("Residual: %.3f", Math.pow(this.p, lastJudgedDoc)));
    this.totalScore+= score;
    this.totalResidual += Math.pow(this.p, lastJudgedDoc);
  }

  public double averageRBP() {
    return this.totalScore/ (double) this.qidsProcessed;
  }

  public double averageResidual() {
    return this.totalResidual / (double) this.qidsProcessed;
  }

  public void printAverageUnjudged(PrintStream out) {
    out.println(String.format("Average unjudged at %d: %.3f",
            5, this.unjudgedCounts.get(5) / (double) this.qidsProcessed));
    out.println(String.format("Average unjudged at %d: %.3f",
            10, this.unjudgedCounts.get(10) / (double) this.qidsProcessed));
    out.println(String.format("Average unjudged at %d: %.3f",
            15, this.unjudgedCounts.get(15) / (double) this.qidsProcessed));
  }

  //*****************************************************
  //***************** Parse Arg *************************
  private static class ParserArgs {
    @Option(name = "-qrel", metaVar = "[Path]", required = true, usage = "Path to qrel file")
    String qrelFile;
    @Option(name = "-rank", metaVar = "[Path]", required = true, usage = "Path to ranked file")
    String rankFile;
    @Option(name = "-out", metaVar = "[Path]", required = false, usage = "Path to output file, if not supplied, will use stdout")
    String outputFile= null;
    @Option(name = "-sep", required = false, usage = "Separator for the fields in each reranked line, default is space")
    String separator = " ";
    @Option(name = "-p", required = false, usage = "p value for RBP, default 0.5")
    double p = 0.5;
  }
  //*****************************************************
  //****************** Main Method **********************


  public static void main(String[] args) throws IOException {
    ParserArgs parsedArgs = new ParserArgs();
    CmdLineParser parser = new CmdLineParser(parsedArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      return;
    }

    Qrels qrels = new Qrels(parsedArgs.qrelFile);
    BufferedReader rankFileReader = new BufferedReader(new InputStreamReader(new FileInputStream(parsedArgs.rankFile)));

    // Now let's construct the list of reranked documents
    // Map of qid to docId list
    Map<String, List<String>> rerankMap = new HashMap<>();
    String currentQid = null;
    List<String> docIds = new ArrayList<>();
    String line;
    // lines of the following form
    //701 Q0 GX250-26-0990860 1 4.165548142909957 LUCENE
    while ( (line = rankFileReader.readLine()) != null) {
      String[] pieces = line.split(parsedArgs.separator);
      String qid = pieces[0];
      String docId = pieces[2];

      if (currentQid == null) currentQid = qid;
      // We are processing a new qid
      if (currentQid!= null && !currentQid.equals(qid)) {
        rerankMap.put(currentQid, docIds);
        docIds = new ArrayList<>();
        currentQid = qid;
      }
      docIds.add(docId);
    }

    RBPEvaluator evaluator = new RBPEvaluator(parsedArgs.p);
    PrintStream out;
    if (parsedArgs.outputFile == null) {
      out = System.out;
    } else {
      // Now we have all the queries
      out = new PrintStream(new FileOutputStream(new File(parsedArgs.outputFile)));
    }
    for (String qid : rerankMap.keySet()) {
      evaluator.outputRpb(qrels, rerankMap.get(qid), qid, out);
    }

    evaluator.printAverageUnjudged(out);
    out.println(String.format("Average RBP: %.3f", evaluator.averageRBP()));
    out.println(String.format("Average Residual: %.3f", evaluator.averageResidual()));
  }

}

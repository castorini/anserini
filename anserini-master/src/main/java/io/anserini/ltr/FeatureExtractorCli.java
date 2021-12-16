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

package io.anserini.ltr;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.anserini.ltr.feature.DocSize;
import io.anserini.ltr.feature.IcTfStat;
import io.anserini.ltr.feature.IdfStat;
import io.anserini.ltr.feature.MatchingTermCount;
import io.anserini.ltr.feature.NormalizedTfStat;
import io.anserini.ltr.feature.OrderedQueryPairs;
import io.anserini.ltr.feature.OrderedSequentialPairs;
import io.anserini.ltr.feature.QueryCoverageRatio;
import io.anserini.ltr.feature.QueryLength;
import io.anserini.ltr.feature.SCS;
import io.anserini.ltr.feature.TfIdfStat;
import io.anserini.ltr.feature.TfStat;
import io.anserini.ltr.feature.TpDist;
import io.anserini.ltr.feature.UniqueTermCount;
import io.anserini.ltr.feature.UnorderedQueryPairs;
import io.anserini.ltr.feature.UnorderedSequentialPairs;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FeatureExtractorCli {
  static class DebugArgs {
    @Option(name = "-index", metaVar = "[path]", required = true, usage = "Lucene index directory")
    public String indexDir;

    @Option(name = "-json", metaVar = "[path]", required = true, usage = "Input File")
    public String jsonFile;

    @Option(name = "-threads", metaVar = "[num]", usage = "Number of workers")
    public int threads = 1;

  }

  public static void addFeature(FeatureExtractorUtils utils, String queryField, String docField) throws IOException {
    /**
     * utils.add(new BM25Stat(new SumPooler(), 2.0, 0.75, docField, queryField));
     * utils.add(new BM25Stat(new AvgPooler(), 2.0, 0.75, docField, queryField));
     * utils.add(new BM25Stat(new MedianPooler(), 2.0, 0.75, docField, queryField));
     * utils.add(new BM25Stat(new MaxPooler(), 2.0, 0.75, docField, queryField));
     * utils.add(new BM25Stat(new MinPooler(), 2.0, 0.75, docField, queryField));
     * utils.add(new BM25Stat(new MaxMinRatioPooler(), 2.0, 0.75, docField,
     * queryField));
     * 
     * utils.add(new LMDirStat(new SumPooler(), 1000, docField, queryField));
     * utils.add(new LMDirStat(new AvgPooler(), 1000, docField, queryField));
     * utils.add(new LMDirStat(new MedianPooler(), 1000, docField, queryField));
     * utils.add(new LMDirStat(new MaxPooler(), 1000, docField, queryField));
     * utils.add(new LMDirStat(new MinPooler(), 1000, docField, queryField));
     * utils.add(new LMDirStat(new MaxMinRatioPooler(), 1000, docField,
     * queryField));
     * 
     * utils.add(new NTFIDF(docField, queryField)); utils.add(new
     * ProbalitySum(docField, queryField));
     * 
     * utils.add(new DFR_GL2Stat(new SumPooler(), docField, queryField));
     * utils.add(new DFR_GL2Stat(new AvgPooler(), docField, queryField));
     * utils.add(new DFR_GL2Stat(new MedianPooler(), docField, queryField));
     * utils.add(new DFR_GL2Stat(new MaxPooler(), docField, queryField));
     * utils.add(new DFR_GL2Stat(new MinPooler(), docField, queryField));
     * utils.add(new DFR_GL2Stat(new MaxMinRatioPooler(), docField, queryField));
     * 
     * utils.add(new DFR_In_expB2Stat(new SumPooler(), docField, queryField));
     * utils.add(new DFR_In_expB2Stat(new AvgPooler(), docField, queryField));
     * utils.add(new DFR_In_expB2Stat(new MedianPooler(), docField, queryField));
     * utils.add(new DFR_In_expB2Stat(new MaxMinRatioPooler(), docField,
     * queryField)); utils.add(new DFR_In_expB2Stat(new MinPooler(), docField,
     * queryField)); utils.add(new DFR_In_expB2Stat(new MaxPooler(), docField,
     * queryField));
     * 
     * utils.add(new DPHStat(new SumPooler(), docField, queryField)); utils.add(new
     * DPHStat(new AvgPooler(), docField, queryField)); utils.add(new DPHStat(new
     * MedianPooler(), docField, queryField)); utils.add(new DPHStat(new
     * MaxPooler(), docField, queryField)); utils.add(new DPHStat(new MinPooler(),
     * docField, queryField)); utils.add(new DPHStat(new MaxMinRatioPooler(),
     * docField, queryField));
     * 
     * utils.add(new Proximity(docField, queryField)); utils.add(new
     * TPscore(docField, queryField));
     */
    utils.add(new TpDist(docField, queryField));

    utils.add(new DocSize(docField));
    if (queryField == "analyzed" && docField == "contents"){
       utils.add(new QueryLength(queryField));
       utils.add(new QueryCoverageRatio(docField, queryField));
       utils.add(new UniqueTermCount(queryField)); }

    utils.add(new MatchingTermCount(docField, queryField));
    utils.add(new SCS(docField, queryField));

    utils.add(new TfStat(new AvgPooler(), docField, queryField));
    utils.add(new TfStat(new MedianPooler(), docField, queryField));
    utils.add(new TfStat(new SumPooler(), docField, queryField));
    utils.add(new TfStat(new MinPooler(), docField, queryField));
    utils.add(new TfStat(new MaxPooler(), docField, queryField));
    utils.add(new TfStat(new MaxMinRatioPooler(), docField, queryField));

    utils.add(new TfIdfStat(true, new AvgPooler(), docField, queryField));
    utils.add(new TfIdfStat(true, new MedianPooler(), docField, queryField));
    utils.add(new TfIdfStat(true, new SumPooler(), docField, queryField));
    utils.add(new TfIdfStat(true, new MinPooler(), docField, queryField));
    utils.add(new TfIdfStat(true, new MaxPooler(), docField, queryField));
    utils.add(new TfIdfStat(true, new MaxMinRatioPooler(), docField, queryField));

    utils.add(new NormalizedTfStat(new AvgPooler(), docField, queryField));
    utils.add(new NormalizedTfStat(new MedianPooler(), docField, queryField));
    utils.add(new NormalizedTfStat(new SumPooler(), docField, queryField));
    utils.add(new NormalizedTfStat(new MinPooler(), docField, queryField));
    utils.add(new NormalizedTfStat(new MaxPooler(), docField, queryField));
    utils.add(new NormalizedTfStat(new MaxMinRatioPooler(), docField, queryField));

    utils.add(new IdfStat(new AvgPooler(), docField, queryField));
    utils.add(new IdfStat(new MedianPooler(), docField, queryField));
    utils.add(new IdfStat(new SumPooler(), docField, queryField));
    utils.add(new IdfStat(new MinPooler(), docField, queryField));
    utils.add(new IdfStat(new MaxPooler(), docField, queryField));
    utils.add(new IdfStat(new MaxMinRatioPooler(), docField, queryField));

    utils.add(new IcTfStat(new AvgPooler(), docField, queryField));
    utils.add(new IcTfStat(new MedianPooler(), docField, queryField));
    utils.add(new IcTfStat(new SumPooler(), docField, queryField));
    utils.add(new IcTfStat(new MinPooler(), docField, queryField));
    utils.add(new IcTfStat(new MaxPooler(), docField, queryField));
    utils.add(new IcTfStat(new MaxMinRatioPooler(), docField, queryField));

    utils.add(new UnorderedSequentialPairs(3, docField, queryField));
    utils.add(new UnorderedSequentialPairs(8, docField, queryField));
    utils.add(new UnorderedSequentialPairs(15, docField, queryField));
    utils.add(new OrderedSequentialPairs(3, docField, queryField));
    utils.add(new OrderedSequentialPairs(8, docField, queryField));
    utils.add(new OrderedSequentialPairs(15, docField, queryField));
    utils.add(new UnorderedQueryPairs(3, docField, queryField));
    utils.add(new UnorderedQueryPairs(8, docField, queryField));
    utils.add(new UnorderedQueryPairs(15, docField, queryField));
    utils.add(new OrderedQueryPairs(3, docField, queryField));
    utils.add(new OrderedQueryPairs(8, docField, queryField));
    utils.add(new OrderedQueryPairs(15, docField, queryField));

  }

  public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
    long start = System.nanoTime();
    DebugArgs cmdArgs = new DebugArgs();
    CmdLineParser parser = new CmdLineParser(cmdArgs);

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      return;
    }

    FeatureExtractorUtils utils = new FeatureExtractorUtils(cmdArgs.indexDir, cmdArgs.threads);
    addFeature(utils, "analyzed", "contents");
    addFeature(utils, "analyzed", "predict");
    addFeature(utils, "text_unlemm", "text_unlemm");
    addFeature(utils, "text_bert_tok", "text_bert_tok");

    addFeature(utils,"text","text");
    addFeature(utils,"text_unlemm","text_unlemm");
    addFeature(utils,"text_bert_tok","text_bert_tok");
    //System.out.println("Load IBM Models");
    //utils.add(new
    // IBMModel1("../FlexNeuART/collections/msmarco_doc/derived_data/giza/title_unlemm",
    // "text_unlemm",
    // "title_unlemm", "text_unlemm"));
    // utils.add(new
    // IBMModel1("../FlexNeuART/collections/msmarco_doc/derived_data/giza/url_unlemm",
    // "text_unlemm",
    // "url_unlemm", "text_unlemm"));
    // utils.add(new
    // IBMModel1("../FlexNeuART/collections/msmarco_doc/derived_data/giza/body",
    // "text_unlemm", "body",
    // "text_unlemm"));
    // utils.add(new
    // IBMModel1("../FlexNeuART/collections/msmarco_doc/derived_data/giza/text_bert_tok",
    // "text_bert_tok",
    // "text_bert_tok", "text_unlemm"));
    // System.out.println("done load IBM");

    File file = new File(cmdArgs.jsonFile);
    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
    String line;
    List<String> qids = new ArrayList<>();
    int lineNum = 0;
    int offset = 0;
    String lastQid = null;
    ObjectMapper mapper = new ObjectMapper();
    List<String> names = utils.list();
    long[] time = new long[names.size()];
    for (int i = 0; i < names.size(); i++) {
      time[i] = 0;
    }
    long executionStart = System.nanoTime();
    while ((line = reader.readLine()) != null && offset < 10000) {
      lineNum++;
      // if(lineNum<=760) continue;
      qids.add(utils.debugExtract(line));
      if (qids.size() >= 10) {
        try {
          while (qids.size() > 0) {
            lastQid = qids.remove(0);
            List<debugOutput> outputArray = utils.getDebugResult(lastQid);
            for (debugOutput res : outputArray) {
              for (int i = 0; i < names.size(); i++) {
                time[i] += res.time.get(i);
              }
            }
            offset++;
          }
        } catch (Exception e) {
          System.out.println("the offset is:" + offset + " at qid:" + lastQid);
          throw e;
        }
      }

    }
    if (qids.size() >= 0) {
      try {
        while (qids.size() > 0) {
          lastQid = qids.remove(0);
          List<debugOutput> outputArray = utils.getDebugResult(lastQid);
          for (debugOutput res : outputArray) {
            for (int i = 0; i < names.size(); i++) {
              time[i] += res.time.get(i);
            }
          }
          offset++;
        }
      } catch (Exception e) {
        System.out.println("the offset is:" + offset + "at qid:" + lastQid);
        throw e;
      }
    }
    long executionEnd = System.nanoTime();
    long sumtime = 0;
    for(int i = 0; i < names.size(); i++){
      sumtime += time[i];
    }
    for(int i = 0; i < names.size(); i++){
      System.out.println(names.get(i)+" takes "+String.format("%.2f",time[i]/1000000000.0) + "s, accounts for "+
      String.format("%.2f", time[i]*100.0/sumtime) + "%");
    }
    utils.close();
    reader.close();
    long end = System.nanoTime();
    long overallTime = end - start;
    long overhead = overallTime-(executionEnd - executionStart);
    System.out.println("The program takes "+String.format("%.2f",overallTime/1000000000.0) + "s, where the overhead takes " + String.format("%.2f",overhead/1000000000.0) +"s");
  }
}
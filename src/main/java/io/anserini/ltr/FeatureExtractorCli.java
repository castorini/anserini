/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
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
import io.anserini.ltr.feature.base.*;
import io.anserini.ltr.feature.*;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
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
    utils.add(new BM25(0.9,0.4, docField, queryField));
    utils.add(new BM25(1.2,0.75, docField, queryField));
    utils.add(new BM25(2.0,0.75, docField, queryField));

    utils.add(new LMDir(1000, docField, queryField));
    utils.add(new LMDir(1500, docField, queryField));
    utils.add(new LMDir(2500, docField, queryField));

    utils.add(new LMJM(0.1, docField, queryField));
    utils.add(new LMJM(0.4, docField, queryField));
    utils.add(new LMJM(0.7, docField, queryField));

    utils.add(new NTFIDF(docField, queryField));
    utils.add(new ProbalitySum(docField, queryField));

    utils.add(new DFR_GL2(docField, queryField));
    utils.add(new DFR_In_expB2(docField, queryField));
    utils.add(new DPH(docField, queryField));

    utils.add(new Proximity(docField, queryField));
    utils.add(new TPscore(docField, queryField));
    utils.add(new tpDist(docField, queryField));

    utils.add(new DocSize(docField));
    utils.add(new Entropy(docField));

    utils.add(new QueryLength(queryField));
    utils.add(new QueryCoverageRatio(docField, queryField));

    utils.add(new UniqueTermCount(queryField));
    utils.add(new MatchingTermCount(docField, queryField));
    utils.add(new SCS(docField, queryField));

    utils.add(new tfStat(new AvgPooler(), docField, queryField));
    utils.add(new tfStat(new MedianPooler(), docField, queryField));
    utils.add(new tfStat(new SumPooler(), docField, queryField));
    utils.add(new tfStat(new MinPooler(), docField, queryField));
    utils.add(new tfStat(new MaxPooler(), docField, queryField));
    utils.add(new tfStat(new VarPooler(), docField, queryField));
    utils.add(new tfStat(new MaxMinRatioPooler(), docField, queryField));
    utils.add(new tfStat(new ConfidencePooler(), docField, queryField));
    utils.add(new tfIdfStat(new AvgPooler(), docField, queryField));
    utils.add(new tfIdfStat(new MedianPooler(), docField, queryField));
    utils.add(new tfIdfStat(new SumPooler(), docField, queryField));
    utils.add(new tfIdfStat(new MinPooler(), docField, queryField));
    utils.add(new tfIdfStat(new MaxPooler(), docField, queryField));
    utils.add(new tfIdfStat(new VarPooler(), docField, queryField));
    utils.add(new tfIdfStat(new MaxMinRatioPooler(), docField, queryField));
    utils.add(new tfIdfStat(new ConfidencePooler(), docField, queryField));
    utils.add(new scqStat(new AvgPooler(), docField, queryField));
    utils.add(new scqStat(new MedianPooler(), docField, queryField));
    utils.add(new scqStat(new SumPooler(), docField, queryField));
    utils.add(new scqStat(new MinPooler(), docField, queryField));
    utils.add(new scqStat(new MaxPooler(), docField, queryField));
    utils.add(new scqStat(new VarPooler(), docField, queryField));
    utils.add(new scqStat(new MaxMinRatioPooler(), docField, queryField));
    utils.add(new scqStat(new ConfidencePooler(), docField, queryField));
    utils.add(new normalizedTfStat(new AvgPooler(), docField, queryField));
    utils.add(new normalizedTfStat(new MedianPooler(), docField, queryField));
    utils.add(new normalizedTfStat(new SumPooler(), docField, queryField));
    utils.add(new normalizedTfStat(new MinPooler(), docField, queryField));
    utils.add(new normalizedTfStat(new MaxPooler(), docField, queryField));
    utils.add(new normalizedTfStat(new VarPooler(), docField, queryField));
    utils.add(new normalizedTfStat(new MaxMinRatioPooler(), docField, queryField));
    utils.add(new normalizedTfStat(new ConfidencePooler(), docField, queryField));

    utils.add(new idfStat(new AvgPooler(), docField, queryField));
    utils.add(new idfStat(new MedianPooler(), docField, queryField));
    utils.add(new idfStat(new SumPooler(), docField, queryField));
    utils.add(new idfStat(new MinPooler(), docField, queryField));
    utils.add(new idfStat(new MaxPooler(), docField, queryField));
    utils.add(new idfStat(new VarPooler(), docField, queryField));
    utils.add(new idfStat(new MaxMinRatioPooler(), docField, queryField));
    utils.add(new idfStat(new ConfidencePooler(), docField, queryField));
    utils.add(new ictfStat(new AvgPooler(), docField, queryField));
    utils.add(new ictfStat(new MedianPooler(), docField, queryField));
    utils.add(new ictfStat(new SumPooler(), docField, queryField));
    utils.add(new ictfStat(new MinPooler(), docField, queryField));
    utils.add(new ictfStat(new MaxPooler(), docField, queryField));
    utils.add(new ictfStat(new VarPooler(), docField, queryField));
    utils.add(new ictfStat(new MaxMinRatioPooler(), docField, queryField));
    utils.add(new ictfStat(new ConfidencePooler(), docField, queryField));

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
    addFeature(utils,"analyzed","contents");
    //addFeature(utils,"text","text");
    //addFeature(utils,"text_unlemm","text_unlemm");
    //addFeature(utils,"text_bert_tok","text_bert_tok");
//    utils.add(new IBMModel1("../pyserini/collections/msmarco-passage/text_bert_tok","Bert","BERT","text_bert_tok"));

    utils.add(new EntityHowMany());
    utils.add(new EntityHowMuch());
    utils.add(new EntityHowLong());

    utils.add(new EntityWho());
    utils.add(new EntityWhen());
    utils.add(new EntityWhere());

    utils.add(new EntityWhoMatch());
    utils.add(new EntityWhereMatch());

    utils.add(new EntityQueryCount("PERSON"));
    utils.add(new EntityDocCount("PERSON"));

    utils.add(new QueryRegex("^[0-9.+_ ]*what.*$"));


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
    for(int i = 0; i < names.size(); i++){
      time[i] = 0;
    }
    long executionStart = System.nanoTime();
    while((line=reader.readLine())!=null&&offset<10000){
      lineNum++;
//      if(lineNum<=760) continue;
      qids.add(utils.debugExtract(line));
      if(qids.size()>=10){
        try{
          while(qids.size()>0) {
            lastQid = qids.remove(0);
            List<debugOutput> outputArray = utils.getDebugResult(lastQid);
//            System.out.println(String.format("Qid:%s\tLine:%d",lastQid,offset));
            for(debugOutput res:outputArray){
              for(int i = 0; i < names.size(); i++){
                time[i] += res.time.get(i);
              }
            }
            offset++;
          }
        } catch (Exception e) {
          System.out.println("the offset is:"+offset+" at qid:"+lastQid);
          throw e;
        }
      }

    }
    if(qids.size()>=0){
      try{
        while(qids.size()>0) {
          lastQid = qids.remove(0);
          List<debugOutput> outputArray = utils.getDebugResult(lastQid);
//          System.out.println(String.format("Qid:%s\tLine:%d",lastQid,offset));
          for(debugOutput res:outputArray){
            for(int i = 0; i < names.size(); i++){
              time[i] += res.time.get(i);
            }
          }
          offset++;
        }
      } catch (Exception e) {
        System.out.println("the offset is:"+offset+"at qid:"+lastQid);
        throw e;
      }
    }
//    long executionEnd = System.nanoTime();
//    long sumtime = 0;
//    for(int i = 0; i < names.size(); i++){
//      sumtime += time[i];
//    }
//    for(int i = 0; i < names.size(); i++){
//      System.out.println(names.get(i)+" takes "+String.format("%.2f",time[i]/1000000000.0) + "s, accounts for "+ String.format("%.2f", time[i]*100.0/sumtime) + "%");
//    }
    utils.close();
    reader.close();
//
//    long end = System.nanoTime();
//    long overallTime = end - start;
//    long overhead = overallTime-(executionEnd - executionStart);
//    System.out.println("The program takes "+String.format("%.2f",overallTime/1000000000.0) + "s, where the overhead takes " + String.format("%.2f",overhead/1000000000.0) +"s");
  }
}
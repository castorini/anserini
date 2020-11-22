package io.anserini.ltr;

import com.fasterxml.jackson.core.type.TypeReference;
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
    
    utils.add(new BM25(0.9,0.4));
    utils.add(new BM25(1.2,0.75));
    utils.add(new BM25(2.0,0.75));

    utils.add(new LMDir(1000));
    utils.add(new LMDir(1500));
    utils.add(new LMDir(2500));

    utils.add(new LMJM(0.1));
    utils.add(new LMJM(0.4));
    utils.add(new LMJM(0.7));

    utils.add(new NTFIDF());
    utils.add(new ProbalitySum());

    utils.add(new DFR_GL2());
    utils.add(new DFR_In_expB2());
    utils.add(new DPH());

    utils.add(new ContextDFR_GL2(new AvgPooler()));
    utils.add(new ContextDFR_GL2(new VarPooler()));
    utils.add(new ContextDFR_In_expB2(new AvgPooler()));
    utils.add(new ContextDFR_In_expB2(new VarPooler()));
    utils.add(new ContextDPH(new AvgPooler()));
    utils.add(new ContextDPH(new VarPooler()));

    utils.add(new Proximity());
    utils.add(new TPscore());
    utils.add(new tpDist());
//    utils.add(new SDM());

    utils.add(new DocSize());
    utils.add(new Entropy());
    utils.add(new StopCover());
    utils.add(new StopRatio());

    utils.add(new QueryLength());
    utils.add(new QueryLengthNonStopWords());
    utils.add(new QueryCoverageRatio());
    utils.add(new UniqueTermCount());
    utils.add(new MatchingTermCount());
    utils.add(new SCS());

    utils.add(new tfStat(new AvgPooler()));
    utils.add(new tfStat(new SumPooler()));
    utils.add(new tfStat(new MinPooler()));
    utils.add(new tfStat(new MaxPooler()));
    utils.add(new tfStat(new VarPooler()));
    utils.add(new tfIdfStat(new AvgPooler()));
    utils.add(new tfIdfStat(new SumPooler()));
    utils.add(new tfIdfStat(new MinPooler()));
    utils.add(new tfIdfStat(new MaxPooler()));
    utils.add(new tfIdfStat(new VarPooler()));
    utils.add(new scqStat(new AvgPooler()));
    utils.add(new scqStat(new SumPooler()));
    utils.add(new scqStat(new MinPooler()));
    utils.add(new scqStat(new MaxPooler()));
    utils.add(new scqStat(new VarPooler()));
    utils.add(new normalizedTfStat(new AvgPooler()));
    utils.add(new normalizedTfStat(new SumPooler()));
    utils.add(new normalizedTfStat(new MinPooler()));
    utils.add(new normalizedTfStat(new MaxPooler()));
    utils.add(new normalizedTfStat(new VarPooler()));
    utils.add(new normalizedDocSizeStat(new AvgPooler()));
    utils.add(new normalizedDocSizeStat(new SumPooler()));
    utils.add(new normalizedDocSizeStat(new MinPooler()));
    utils.add(new normalizedDocSizeStat(new MaxPooler()));
    utils.add(new normalizedDocSizeStat(new VarPooler()));

    utils.add(new idfStat(new AvgPooler()));
    utils.add(new idfStat(new SumPooler()));
    utils.add(new idfStat(new MinPooler()));
    utils.add(new idfStat(new MaxPooler()));
    utils.add(new idfStat(new VarPooler()));
    utils.add(new idfStat(new MaxMinRatioPooler()));
    utils.add(new idfStat(new ConfidencePooler()));
    utils.add(new ictfStat(new AvgPooler()));
    utils.add(new ictfStat(new SumPooler()));
    utils.add(new ictfStat(new MinPooler()));
    utils.add(new ictfStat(new MaxPooler()));
    utils.add(new ictfStat(new VarPooler()));
    utils.add(new ictfStat(new MaxMinRatioPooler()));
    utils.add(new ictfStat(new ConfidencePooler()));

    utils.add(new UnorderedSequentialPairs(3));
    utils.add(new UnorderedSequentialPairs(8));
    utils.add(new UnorderedSequentialPairs(15));
    utils.add(new OrderedSequentialPairs(3));
    utils.add(new OrderedSequentialPairs(8));
    utils.add(new OrderedSequentialPairs(15));
    utils.add(new UnorderedQueryPairs(3));
    utils.add(new UnorderedQueryPairs(8));
    utils.add(new UnorderedQueryPairs(15));
    utils.add(new OrderedQueryPairs(3));
    utils.add(new OrderedQueryPairs(8));
    utils.add(new OrderedQueryPairs(15));

    File file = new File(cmdArgs.jsonFile);
    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
    String line;
    List<String> qids = new ArrayList<>();
    int offset = 0;
    String lastQid = null;
    ObjectMapper mapper = new ObjectMapper();
    List<String> names = utils.list();
    long[] time = new long[names.size()];
    for(int i = 0; i < names.size(); i++){
      time[i] = 0;
    }
    long executionStart = System.nanoTime();
    while((line=reader.readLine())!=null&&offset<1000){
      qids.add(utils.debugExtract(line));
      if(qids.size()>=100){
        try{
          while(qids.size()>0) {
            lastQid = qids.remove(0);
            String allResult = utils.getResult(lastQid);
            TypeReference<ArrayList<debugOutput>> typeref = new TypeReference<>() {};
            List<debugOutput> outputArray = mapper.readValue(allResult, typeref);
            for(debugOutput res:outputArray){
              for(int i = 0; i < names.size(); i++){
                time[i] += res.time.get(i);
              }
            }
            offset++;
            System.out.println(offset);

          }
        } catch (Exception e) {
          System.out.println("the offset is:"+offset+"at qid:"+lastQid);
          throw e;
        }
      }

    }
    long executionEnd = System.nanoTime();
    long sumtime = 0;
    for(int i = 0; i < names.size(); i++){
      sumtime += time[i];
    }
    for(int i = 0; i < names.size(); i++){
      System.out.println(names.get(i)+" takes "+String.format("%.2f",time[i]/1000000000.0) + "s, accounts for "+ String.format("%.2f", time[i]*100.0/sumtime) + "%");
    }
    utils.close();
    reader.close();

    long end = System.nanoTime();
    long overallTime = end - start;
    long overhead = overallTime-(executionEnd - executionStart);
    System.out.println("The program takes "+String.format("%.2f",overallTime/1000000000.0) + "s, where the overhead takes " + String.format("%.2f",overhead/1000000000.0) +"s");
  }
}
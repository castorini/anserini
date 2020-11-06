package io.anserini.ltr;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.anserini.ltr.feature.OrderedSequentialPairsFeatureExtractor;
import io.anserini.ltr.feature.UnorderedSequentialPairsFeatureExtractor;
import io.anserini.ltr.feature.base.*;
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

    utils.add(new AvgICTFFeatureExtractor());
    utils.add(new AvgIDFFeatureExtractor());
    utils.add(new BM25FeatureExtractor());
    utils.add(new DocSizeFeatureExtractor());
    utils.add(new MatchingTermCount());
    utils.add(new PMIFeatureExtractor());
    utils.add(new QueryLength());
    utils.add(new SCQFeatureExtractor());
    utils.add(new SCSFeatureExtractor());
    utils.add(new SumMatchingTF());
    utils.add(new TFIDFFeatureExtractor());
    utils.add(new UniqueTermCount());
    utils.add(new UnorderedSequentialPairsFeatureExtractor(3));
    utils.add(new UnorderedSequentialPairsFeatureExtractor(5));
    utils.add(new UnorderedSequentialPairsFeatureExtractor(8));
    utils.add(new OrderedSequentialPairsFeatureExtractor(3));
    utils.add(new OrderedSequentialPairsFeatureExtractor(5));
    utils.add(new OrderedSequentialPairsFeatureExtractor(8));

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
    while((line=reader.readLine())!=null){
      qids.add(utils.lazyExtract(line));
      if(qids.size()>=100){
        try{
          while(qids.size()>0) {
            lastQid = qids.remove(0);
            String allResult = utils.getResult(lastQid);
            TypeReference<ArrayList<output>> typeref = new TypeReference<>() {};
            List<output> outputArray = mapper.readValue(allResult, typeref);
            for(output res:outputArray){
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
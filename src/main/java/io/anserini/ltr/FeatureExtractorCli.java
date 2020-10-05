package io.anserini.ltr;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.anserini.ltr.feature.OrderedSequentialPairsFeatureExtractor;
import io.anserini.ltr.feature.UnorderedSequentialPairsFeatureExtractor;
import io.anserini.ltr.feature.base.*;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.*;
import java.util.ArrayList;
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
        utils.add(new QueryLength());
        utils.add(new SCQFeatureExtractor());
        utils.add(new SCSFeatureExtractor());
        utils.add(new SumMatchingTF());
        utils.add(new TFIDFFeatureExtractor());
        utils.add(new UniqueTermCount());


        File file = new File(cmdArgs.jsonFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String line;
        ArrayList<String> qids = new ArrayList<>();
        int offset = 0;
        String lastQid = null;
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<String> names = utils.list();
        long[] time = new long[names.size()];
        for(int i = 0; i < names.size(); i++){
            time[i] = 0;
        }
        while((line=reader.readLine())!=null){
            qids.add(utils.lazyExtract(line));
            if(qids.size()>=1000){
                try{
                    while(qids.size()>0) {
                        lastQid = qids.remove(0);
                        String allResult = utils.getResult(lastQid);
                        TypeReference<ArrayList<output>> typeref = new TypeReference<>() {};
                        ArrayList<output> outputArray = mapper.readValue(allResult, typeref);
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
        long sumtime = 0;
        for(int i = 0; i < names.size(); i++){
            sumtime += time[i];
        }
        for(int i = 0; i < names.size(); i++){
            System.out.println(names.get(i)+" takes "+time[i]/1000000000.0 + "account for "+ time[i]/sumtime);
        }
    }
}

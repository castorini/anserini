package io.anserini.ltr;

import io.anserini.ltr.feature.OrderedSequentialPairsFeatureExtractor;
import io.anserini.ltr.feature.UnorderedSequentialPairsFeatureExtractor;
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

        utils.add(new OrderedSequentialPairsFeatureExtractor(3));
        utils.add(new UnorderedSequentialPairsFeatureExtractor(3));

        File file = new File(cmdArgs.jsonFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String line;
        ArrayList<String> qids = new ArrayList<>();
        int offset = 0;
        String lastQid = null;
        while((line=reader.readLine())!=null){
            qids.add(utils.lazyExtract(line));
            if(qids.size()>=1000){
                try{
                    while(qids.size()>0) {
                        lastQid = qids.remove(0);
                        utils.getResult(lastQid);
                        offset++;
                    }
                } catch (Exception e) {
                    System.out.println("the offset is:"+offset+"at qid:"+lastQid);
                    throw e;
                }
            }
        }
    }
}

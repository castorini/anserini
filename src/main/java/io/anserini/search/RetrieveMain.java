package io.anserini.search;

import org.apache.commons.io.FileUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import java.io.File;
import java.util.List;

/*
 * Java rewrite of retrieve.py
 */
public class RetrieveMain {
    public static void main(String[] args) throws Exception {
        SearchArgs searchArgs = new SearchArgs();
        CmdLineParser parser = new CmdLineParser(searchArgs, ParserProperties.defaults().withUsageWidth(90));

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.err.println("Example: Eval " + parser.printExample(OptionHandlerFilter.REQUIRED));
            return;
        }

        SimpleSearcher searcher = new SimpleSearcher(searchArgs.index);
        float k1 = Float.parseFloat(searchArgs.k1[0]);
        float b = Float.parseFloat(searchArgs.b[0]);
        searcher.setBM25Similarity(k1, b);
        System.out.println("Initializing BM25, setting k1=" + k1 + " and b=" + b + "");

        if (searchArgs.rm3) {
            int fbTerms = Integer.parseInt(searchArgs.rm3_fbTerms[0]);
            int fbDocs = Integer.parseInt(searchArgs.rm3_fbDocs[0]);
            float originalQueryWeight = Float.parseFloat(searchArgs.rm3_originalQueryWeight[0]);
            searcher.setRM3Reranker(fbTerms, fbDocs, originalQueryWeight);
            System.out.println("Initializing RM3, setting fbTerms=" + fbTerms + ", fbDocs=" + fbDocs +
                    " and originalQueryWeight=" + originalQueryWeight);
        }

        File fout = new File(searchArgs.output);
        FileUtils.writeStringToFile(fout, "", "utf-8"); // clear the file

        long startTime = System.nanoTime();
        List<String> lines = FileUtils.readLines(new File(searchArgs.qid_queries), "utf-8");

        for (int lineNumber = 0; lineNumber < lines.size(); ++lineNumber) {
            String line = lines.get(lineNumber);
            String[] split = line.trim().split("\t");
            String qid = split[0];
            String query = split[1];

            SimpleSearcher.Result[] hits = searcher.search(query, searchArgs.hits);

            if (lineNumber % 10 == 0) {
                double timePerQuery = (double) (System.nanoTime() - startTime) / (lineNumber + 1) / 10e9;
                System.out.format("Retrieving query " + lineNumber + " (%.3f s/query)\n", timePerQuery);
            }

            for (int rank = 0; rank < hits.length; ++rank) {
                String docno = hits[rank].docid;
                FileUtils.writeStringToFile(fout, qid + "\t" + docno + "\t" + (rank + 1) + "\n", "utf-8", true);
            }
        }

        System.out.println("Done!");
    }
}

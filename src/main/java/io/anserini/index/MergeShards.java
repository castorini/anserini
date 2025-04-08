package io.anserini.index;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.nio.file.Paths;

public class MergeShards {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: MergeShards <output_dir> <shard_dir1> [<shard_dir2> ...]");
            System.exit(1);
        }

        String outputDir = args[0];
        FSDirectory mergedDir = FSDirectory.open(Paths.get(outputDir));
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        IndexWriter writer = new IndexWriter(mergedDir, config);

        for (int i = 1; i < args.length; i++) {
            System.out.println("Adding index: " + args[i]);
            FSDirectory shardDir = FSDirectory.open(Paths.get(args[i]));
            writer.addIndexes(shardDir);
        }

        System.out.println("Merging...");
        writer.forceMerge(1);
        writer.close();
        System.out.println("Done. Merged index at: " + outputDir);
    }
}


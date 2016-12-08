package io.anserini.embeddings.train;

import org.kohsuke.args4j.Option;

public class TrainArgs {
    @Option(name = "-input", metaVar = "[file]", required = true, usage = "raw file")
    public String input;

    @Option(name = "-output", metaVar = "[file]", required = true, usage = "path of the output file")
    public String output;

    //optional arguments
    @Option(name = "-window", metaVar = "[Number]",  usage = "window size")
    public int window = 5;

    @Option(name = "-iter", metaVar = "[Number]",  usage = "number of iterations")
    public int iter = 1;

    @Option(name = "-dimension", metaVar = "[Number]",  usage = "dimension size")
    public int dimension = 100;

    @Option(name = "-minfreq", metaVar = "[Number]", usage = "minimum term frequency")
    public int min_word_freq = 5;

    @Option(name = "-seed", metaVar = "[Number]", usage = "seed size")
    public int seed = 42;

}

package io.anserini.embeddings.search;

import org.kohsuke.args4j.Option;

public class SearchW2V_Args {
    @Option(name = "-model", metaVar = "[file]", required = true, usage = "path to model file")
    public String model;

    //optional arguments
    @Option(name = "-nearest", metaVar = "[Number]",  usage = "number of nearest words")
    public int nearest = 10;

    @Option(name = "-term", metaVar = "[String]",  usage = "input word")
    public String term = "";

    @Option(name = "-input", metaVar = "[file]", usage = "input file with one word per line")
    public  String input_file = "";

    @Option(name = "-gmodel", usage = "specify if the model was trained in Google format")
    public Boolean gModel = false;

    @Option(name = "-output", metaVar = "[file]", usage = "path of the output file")
    public String output_file = "";


}

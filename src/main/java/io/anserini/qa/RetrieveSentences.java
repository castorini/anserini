<<<<<<< HEAD
/**
 * Anserini: An information retrieval toolkit built on Lucene
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
=======
>>>>>>> 0b9b484e9f5f9d8568abcb91aa6b305c3bb17b17
package io.anserini.qa;

import edu.stanford.nlp.simple.Sentence;
import io.anserini.index.IndexUtils;
import io.anserini.qa.passage.PassageScorer;
import io.anserini.qa.passage.ScoredPassage;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.search.query.QaTopicReader;
import io.anserini.util.AnalyzerUtils;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static io.anserini.index.generator.LuceneDocumentGenerator.FIELD_BODY;
import static io.anserini.index.generator.LuceneDocumentGenerator.FIELD_ID;

public class RetrieveSentences {

  public static class Args {
    // required arguments
    @Option(name = "-index", metaVar = "[path]", required = true, usage = "Lucene index")
    public String index;

    @Option(name = "-output", metaVar = "[file]", required = true, usage = "output file")
    public String output;

    // optional arguments
    @Option(name = "-topics", metaVar = "[file]", usage = "topics file")
    public String topics = "";

    @Option(name = "-query", metaVar = "[string]", usage = "a single query")
    public String query = "";

    @Option(name = "-hits", metaVar = "[number]", usage = "max number of hits to return")
    public int hits = 100;

    //Todo: add more passage scorer
<<<<<<< HEAD
    @Option(name = "-scorer", metaVar = "[Idf|Wmd]", usage = "passage scores")
=======
    @Option(name = "-scorer", metaVar = "[Idf]", usage = "passage scores")
>>>>>>> 0b9b484e9f5f9d8568abcb91aa6b305c3bb17b17
    public String scorer;

    @Option(name = "-k", metaVar = "[number]", usage = "top-k passages to be retrieved")
    public int k = 1;
  }

  private final IndexReader reader;
  private final PassageScorer scorer;

  public RetrieveSentences(RetrieveSentences.Args args) throws Exception {
    Path indexPath = Paths.get(args.index);

    if (!Files.exists(indexPath) || !Files.isDirectory(indexPath) || !Files.isReadable(indexPath)) {
      throw new IllegalArgumentException(args.index + " does not exist or is not a directory.");
    }

    this.reader = DirectoryReader.open(FSDirectory.open(indexPath));
    Constructor passageClass = Class.forName("io.anserini.qa.passage." + args.scorer + "PassageScorer")
            .getConstructor(String.class, Integer.class);
    scorer = (PassageScorer) passageClass.newInstance(args.index, args.k);
  }

  public void search(SortedMap<Integer, String> topics, String submissionFile, int numHits)
          throws IOException, ParseException {
    IndexSearcher searcher = new IndexSearcher(reader);

    //using BM25 scoring model
    Similarity similarity = new BM25Similarity(0.9f, 0.4f);
    searcher.setSimilarity(similarity);

    PrintWriter out = new PrintWriter(Files.newBufferedWriter(Paths.get(submissionFile), StandardCharsets.US_ASCII));

    EnglishAnalyzer ea = new EnglishAnalyzer();
    QueryParser queryParser = new QueryParser(FIELD_BODY, ea);
    queryParser.setDefaultOperator(QueryParser.Operator.OR);

    for (Map.Entry<Integer, String> entry : topics.entrySet()) {
      int qID = entry.getKey();
      String queryString = entry.getValue();
      Query query = AnalyzerUtils.buildBagOfWordsQuery(FIELD_BODY, ea, queryString);

      TopDocs rs = searcher.search(query, numHits);
      ScoreDoc[] hits = rs.scoreDocs;
      ScoredDocuments docs = ScoredDocuments.fromTopDocs(rs, searcher);

      for (int i = 0; i < docs.documents.length; i++) {
        out.println(String.format("%d %s %d %f", qID,
                docs.documents[i].getField(FIELD_ID).stringValue(), (i + 1), docs.scores[i]));
      }
    }
    out.flush();
    out.close();
  }

  public void getRankedPassages(Args args) throws Exception {
    IndexUtils util = new IndexUtils(args.index);
    List<String> sentencesList = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(args.output))) {
      String line;

      while ((line = br.readLine()) != null) {
        String docid = line.trim().split(" ")[1];
        List<Sentence> sentences = util.getSentDocument(docid);
        for (Sentence sent : sentences) {
          sentencesList.add(sent.text());
        }
      }
    }
    scorer.score(sentencesList, args.output);

    List<ScoredPassage> topPassages = scorer.extractTopPassages();
    for (ScoredPassage s: topPassages) {
      System.out.println(s.getSentence() + " " + s.getScore());
    }
  }

  public void retrieveDocuments(RetrieveSentences.Args args) throws Exception {
    SortedMap<Integer, String> topics = new TreeMap<>();
    if (!args.topics.isEmpty()) {
      QaTopicReader tr = new QaTopicReader(Paths.get(args.topics));
      topics = tr.read();
    } else {
      topics.put(1, args.query);
    }

    search(topics, args.output, args.hits);
  }

  public static void main(String[] args) throws Exception {
    Args qaArgs = new Args();
    CmdLineParser parser = new CmdLineParser(qaArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: RetrieveSentences" + parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    if (qaArgs.topics.isEmpty() && qaArgs.query.isEmpty()){
      System.err.println("Pass either a query or a topic. Both can't be empty.");
      return;
    }

    RetrieveSentences rs = new RetrieveSentences(qaArgs);
    rs.retrieveDocuments(qaArgs);
    rs.getRankedPassages(qaArgs);
  }
}

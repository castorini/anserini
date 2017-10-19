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
package io.anserini.qa;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
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

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static io.anserini.index.generator.LuceneDocumentGenerator.FIELD_BODY;
import static io.anserini.index.generator.LuceneDocumentGenerator.FIELD_ID;

public class RetrieveSentences {

  public static class Args {
    // required arguments
    @Option(name = "-index", metaVar = "[path]", required = true, usage = "Lucene index")
    public String index;

    // optional arguments
    @Option(name = "-embeddings", metaVar = "[path]", usage = "Path of the word2vec index")
    public String embeddings = "";

    @Option(name = "-topics", metaVar = "[file]", usage = "topics file")
    public String topics = "";

    @Option(name = "-query", metaVar = "[string]", usage = "a single query")
    public String query = "";

    @Option(name = "-hits", metaVar = "[number]", usage = "max number of hits to return")
    public int hits = 100;

    @Option(name = "-scorer", metaVar = "[Idf|Wmd]", usage = "passage scores")
    public String scorer = "Idf";

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
    if (args.scorer.equals("Idf")) {
      scorer = (PassageScorer) passageClass.newInstance(args.index, args.k);
    } else if (args.scorer.equals("Wmd")) {
      scorer = (PassageScorer) passageClass.newInstance(args.embeddings, args.k);
    } else {
      throw new IllegalArgumentException("Scorer should either be Idf or Wmd");
    }
  }

  public Map<String, Float> search(SortedMap<Integer, String> topics, int numHits)
          throws IOException, ParseException {
    IndexSearcher searcher = new IndexSearcher(reader);

    //using BM25 scoring model
    Similarity similarity = new BM25Similarity(0.9f, 0.4f);
    searcher.setSimilarity(similarity);

    EnglishAnalyzer ea = new EnglishAnalyzer();
    QueryParser queryParser = new QueryParser(FIELD_BODY, ea);
    queryParser.setDefaultOperator(QueryParser.Operator.OR);
    Map<String, Float> scoredDocs = new LinkedHashMap<>();

    for (Map.Entry<Integer, String> entry : topics.entrySet()) {
      int qID = entry.getKey();
      String queryString = entry.getValue();
      Query query = AnalyzerUtils.buildBagOfWordsQuery(FIELD_BODY, ea, queryString);

      TopDocs rs = searcher.search(query, numHits);
      ScoreDoc[] hits = rs.scoreDocs;
      ScoredDocuments docs = ScoredDocuments.fromTopDocs(rs, searcher);

      for (int i = 0; i < docs.documents.length; i++) {
        scoredDocs.put(docs.documents[i].getField(FIELD_ID).stringValue(), docs.scores[i]);
      }
    }
    return scoredDocs;
  }

  public void getRankedPassages(Args args) throws Exception {
    Map<String, Float> scoredDocs  = retrieveDocuments(args);
    Map<String, Float> sentencesMap = new LinkedHashMap<>();

    IndexUtils util = new IndexUtils(args.index);

    TokenizerFactory<CoreLabel> tokenizerFactory =
            PTBTokenizer.factory(new CoreLabelTokenFactory(), "");

    for (Map.Entry<String, Float> doc : scoredDocs.entrySet()) {
        List<Sentence> sentences = util.getSentDocument(doc.getKey());

        for (Sentence sent : sentences) {
          List<CoreLabel> tokens = tokenizerFactory.getTokenizer(new StringReader(sent.text())).tokenize();
          String answerTokens = tokens.stream()
                  .map(CoreLabel::toString)
                  .collect(Collectors.joining(" "));
          sentencesMap.put(answerTokens, doc.getValue());
        }
    }

    String queryTokens = tokenizerFactory.getTokenizer(new StringReader(args.query)).tokenize().stream()
            .map(CoreLabel::toString)
            .collect(Collectors.joining(" "));
    scorer.score(queryTokens, sentencesMap);

    List<ScoredPassage> topPassages = scorer.extractTopPassages();
    for (ScoredPassage s: topPassages) {
      System.out.println(s.getSentence() + " " + s.getScore());
    }
  }

  public Map<String, Float> retrieveDocuments(RetrieveSentences.Args args) throws Exception {
    SortedMap<Integer, String> topics = new TreeMap<>();
    if (!args.topics.isEmpty()) {
      QaTopicReader tr = new QaTopicReader(Paths.get(args.topics));
      topics = tr.read();
    } else {
      topics.put(1, args.query);
    }

    Map<String, Float> scoredDocs = search(topics, args.hits);
    return scoredDocs;
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

    if (qaArgs.scorer.equalsIgnoreCase("Wmd") && qaArgs.embeddings.isEmpty()) {
      System.err.println("Wmd passage scorer requires word2vec index");
      parser.printUsage(System.err);
      return;
    }

    if (!qaArgs.scorer.equals("Idf") && !qaArgs.scorer.equals("Wmd")) {
      System.err.println("Scorer should either be Idf or Wmd");
      parser.printUsage(System.err);
      return;
    }

    RetrieveSentences rs = new RetrieveSentences(qaArgs);
    rs.getRankedPassages(qaArgs);
  }
}

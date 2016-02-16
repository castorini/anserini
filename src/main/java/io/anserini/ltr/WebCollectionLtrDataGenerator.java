package io.anserini.ltr;

import io.anserini.index.IndexWebCollection;
import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.rerank.Reranker;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.util.Qrels;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;

import java.io.IOException;
import java.io.PrintStream;

/**
 * A reranker that will be used to dump feature vectors
 * for documents retrieved from a search
 */
public class WebCollectionLtrDataGenerator implements Reranker{
  private static final Logger LOG = LogManager.getLogger(WebCollectionLtrDataGenerator.class);

  private PrintStream out;
  private Qrels qrels;
  private final FeatureExtractors extractorChain;

  /**
   * Constructor
   * @param out         The output stream to actually print it
   */
  public WebCollectionLtrDataGenerator(PrintStream out, Qrels qrels, FeatureExtractors extractors) {
    this.out = out;
    this.qrels = qrels;
    this.extractorChain = extractors == null ? WebFeatureExtractor.getDefaultExtractors() : extractors;
  }

  @Override
  public ScoredDocuments rerank(ScoredDocuments docs, RerankerContext context) {
    Document[] documents = docs.documents;
    IndexReader reader = context.getIndexSearcher().getIndexReader();
    String qid = context.getQueryId();
    LOG.info("Beginning rerank");
    for (int i =0; i < docs.documents.length; i++ ) {
      try {
        Terms terms = reader.getTermVector(docs.ids[i], IndexWebCollection.FIELD_BODY);
        float[] features = this.extractorChain.extractAll(documents[i], terms, context);
        String docId = documents[i].get(IndexWebCollection.FIELD_ID);
        // QREL 0 in this case, will be assigned if needed later
        //qid
        BaseFeatureExtractor.writeFeatureVector(out,qid, this.qrels.getRelevanceGrade(qid, docId), docId,  features);
        LOG.info("Finished writing vectors");
      } catch (IOException e) {
        LOG.error(String.format("IOExecption trying to retrieve feature vector for %d doc", docs.ids[i]));
        continue;
      }
    }
    // Does nothing to the actual docs, we just need to extract the feature vector
    return docs;
  }
}

/**
 * Anserini: A toolkit for reproducible information retrieval research built on Lucene
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

package io.anserini.kg.freebase;

import io.anserini.rerank.ScoredDocuments;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Lookups a Freebase object either by {@code mid} or by a free text search over the textual labels
 * of the nodes.
 */
public class LookupFreebase implements Closeable {
  private static final Logger LOG = LogManager.getLogger(LookupFreebase.class);

  private final IndexReader reader;

  static final class Args {
    @Option(name = "-index", metaVar = "[path]", required = true, usage = "index path")
    public Path index;

    @Option(name = "-mid", metaVar = "[mid]", usage = "Freebase machine id")
    public String mid = null;

    @Option(name = "-query", metaVar = "[query]", usage = "full-text query")
    public String query;

    @Option(name="-hits", metaVar = "[hits]", usage = "number of search hits")
    private int numHits = 20;
  }

  protected class Result {
    public String mid;
    public String name;
    public String wikiTitle;
    public String w3Label;
    public float score;

    public Result(String mid, String name, String wikiTitle, String w3Label, float score) {
      this.mid = mid;
      this.name = name;
      this.wikiTitle = wikiTitle;
      this.w3Label = w3Label;
      this.score = score;
    }
  }

  public LookupFreebase(Path indexPath) throws IOException {
    if (!Files.exists(indexPath) || !Files.isDirectory(indexPath) || !Files.isReadable(indexPath)) {
      throw new IllegalArgumentException(indexPath + " does not exist or is not a directory.");
    }

    this.reader = DirectoryReader.open(FSDirectory.open(indexPath));
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

  /**
   * Prints all known facts about a particular mid.
   * @param mid subject mid
   * @throws Exception on error
   */
  public Document lookupMid(String mid) throws IOException {
    IndexSearcher searcher = new IndexSearcher(reader);

    // Search for exact subject URI
    TermQuery query = new TermQuery(new Term(IndexFreebase.FIELD_ID, mid));

    TopDocs topDocs = searcher.search(query, 1);
    if (topDocs.totalHits == 0) {
      System.err.println("Error: mid not found!");
      return null;
    }
    if (topDocs.totalHits > 1) {
      System.err.println("Error: more than one matching mid found. This shouldn't happen!");
      return null;
    }

    Document doc = reader.document(topDocs.scoreDocs[0].doc);
    return doc;
  }

  /**
   * Full text search over textual labels of nodes.
   * @param q query string
   * @param numHits hits to return
   * @throws Exception on error
   */
  public Result[] search(String q, int numHits) throws Exception {
    // Initialize index searcher
    IndexSearcher searcher = new IndexSearcher(reader);

    // search for query in multiple fields
    MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
        new String[]{ IndexFreebase.FIELD_NAME, IndexFreebase.FIELD_LABEL, IndexFreebase.FIELD_ALIAS },
        new SimpleAnalyzer());
    queryParser.setDefaultOperator(QueryParser.Operator.OR);
    Query query = queryParser.parse(q);

    TopDocs rs = searcher.search(query, numHits);
    ScoredDocuments docs = ScoredDocuments.fromTopDocs(rs, searcher);

    Result[] results = new Result[docs.documents.length];
    for (int i = 0; i < docs.documents.length; i++) {
      float score = docs.scores[i];
      String mid = docs.documents[i].getField(IndexFreebase.FIELD_ID).stringValue();
      String name = docs.documents[i].getField(IndexFreebase.FIELD_NAME).stringValue();
      String wikiTitle = docs.documents[i].getField(IndexFreebase.FIELD_ALIAS).stringValue();
      String w3Label = docs.documents[i].getField(IndexFreebase.FIELD_LABEL).stringValue();
      results[i] = new Result(mid, name, wikiTitle, w3Label, score);
    }
    return results;
  }

  public static void main(String[] args) throws Exception {
    Args searchArgs = new Args();
    CmdLineParser parser = new CmdLineParser(searchArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: "+ LookupFreebase.class.getSimpleName() +
          parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    LookupFreebase lookup = new LookupFreebase(searchArgs.index);

    if (searchArgs.mid != null) {
      // Lookup by mid.
      Document doc = lookup.lookupMid(searchArgs.mid);
      if (doc == null)
        System.exit(-1);
      doc.forEach(field -> {
        System.out.println(field.name() + " = " + field.stringValue());
      });
    } else {
      // Full-text search over text labels.
      Result[] results = lookup.search(searchArgs.query, searchArgs.numHits);
      for (int i = 0; i < results.length; i++) {
        String resultDoc = String.format("%d - SCORE: %f\nTOPIC_MID: %s\nOBJECT_NAME: %s\nWIKI_TITLE: %s\nW3_LABEL: %s\n",
            (i + 1),
            results[i].score,
            results[i].mid,
            results[i].name,
            results[i].wikiTitle,
            results[i].w3Label);
        System.out.println(resultDoc);
      }
    }

    lookup.close();
  }
}


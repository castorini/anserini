/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
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

package io.anserini.kg;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import java.nio.file.Path;

/**
 * Uses a "triple as a document" index of Freebase to look up Freebase triples.
 */
public class LookupFreebaseTriples {
  static final class Args {
    @Option(name = "-index", metaVar = "[path]", required = true, usage = "index path")
    private Path index;

    @Option(name = "-s", metaVar = "[subject]", usage = "subject")
    private String s = null;

    @Option(name = "-p", metaVar = "[predicate]", usage = "predicate")
    private String p = null;

    @Option(name = "-o", metaVar = "[object]", usage = "object")
    private String o = null;

    @Option(name="-hits", metaVar = "[hits]", usage = "number of search hits")
    private int hits = 20;
  }

  public static void main(String[] args) throws Exception {
    LookupFreebaseTriples.Args searchArgs = new LookupFreebaseTriples.Args();
    CmdLineParser parser = new CmdLineParser(searchArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: "+ LookupFreebaseTriples.class.getSimpleName() +
          parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    IndexReader reader = DirectoryReader.open(FSDirectory.open(searchArgs.index));
    IndexSearcher searcher = new IndexSearcher(reader);

    BooleanQuery.Builder builder = new BooleanQuery.Builder();
    if (searchArgs.s != null) {
      builder.add(new TermQuery(new Term(IndexFreebase.FIELD_SUBJECT, searchArgs.s)), BooleanClause.Occur.MUST);
    }
    if (searchArgs.p != null) {
      builder.add(new TermQuery(new Term(IndexFreebase.FIELD_PREDICATE, searchArgs.p)), BooleanClause.Occur.MUST);
    }
    if (searchArgs.o != null) {
      builder.add(new TermQuery(new Term(IndexFreebase.FIELD_OBJECT, searchArgs.o)), BooleanClause.Occur.MUST);
    }

    TopDocs topDocs = searcher.search(builder.build(), searchArgs.hits);
    System.out.println("total hits: " + topDocs.totalHits);
    for (int i=0; i<topDocs.scoreDocs.length; i++) {
      Document doc = reader.document(topDocs.scoreDocs[i].doc);
      System.out.println(String.format("%s %s %s", doc.getField(IndexFreebase.FIELD_SUBJECT).stringValue(),
          doc.getField(IndexFreebase.FIELD_PREDICATE).stringValue(), doc.getField(IndexFreebase.FIELD_OBJECT).stringValue()));
    }
  }
}

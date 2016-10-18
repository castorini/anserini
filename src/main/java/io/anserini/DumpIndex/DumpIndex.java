package io.anserini.dumpindex;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import io.anserini.index.IndexWebCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ParserProperties;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DumpIndex {
  private static final Logger LOG = LogManager.getLogger(DumpIndex.class);

  class RawDocNotStoredException extends Exception {
    public RawDocNotStoredException(String message) {
      super(message);
    }
  }
  class DocVectorNotStoredException extends Exception {
    public DocVectorNotStoredException(String message) {
      super(message);
    }
  }

  String printRepositoryStats( DirectoryReader reader ) throws IOException {
    int docCount = reader.numDocs();
    int contentsCount = reader.getDocCount(IndexWebCollection.FIELD_BODY);
    long termCount = reader.getSumTotalTermFreq(IndexWebCollection.FIELD_BODY);

    StringBuilder sb = new StringBuilder();
    sb.append("Repository statistics:\n");
    sb.append("documents:\t" + docCount + "\n");
    sb.append("contentsCount(doc with contents):\t" + contentsCount + "\n");
    //sb.append("unique terms:\t" + uniqueTermCount);
    sb.append("total terms:\t" + termCount + "\n");
    sb.append("stored fields:\t\t");

    Document d = reader.document(1);
    List<IndexableField> fields = d.getFields();
    for (IndexableField f : fields) {
      sb.append(f.name()+" ");
    }
    sb.append("\n");
    return sb.toString();
  }

  String printTermCounts( DirectoryReader reader, String termStr )
          throws IOException, ParseException {
    StringBuilder sb = new StringBuilder();
    EnglishAnalyzer ea = new EnglishAnalyzer(CharArraySet.EMPTY_SET);
    QueryParser qp = new QueryParser(IndexWebCollection.FIELD_BODY, ea);
    TermQuery q = (TermQuery)qp.parse(termStr);
    long termFreq = reader.totalTermFreq(q.getTerm());
    long docCount = reader.docFreq(q.getTerm());
    sb.append(termStr+" ")
      .append(q.toString(IndexWebCollection.FIELD_BODY)+" ")
      .append(termFreq+" ")
      .append(docCount+" ")
      .append("\n");
    return sb.toString();
  }

  /*
  * print the internal id
  */
  String printDocumentIDs( DirectoryReader reader, List<String> externalIDs ) throws IOException {
    StringBuilder sb = new StringBuilder();
    Map<String, Integer> mapping = new HashMap<>();
    for (String s : externalIDs) {
      mapping.put(s, 0);
    }
    int total = externalIDs.size();
    for (int i = 1; i < reader.maxDoc()+1; i++) {
      Document d = reader.document(i);
      IndexableField id = d.getField(IndexWebCollection.FIELD_ID);
      String idStr = id.stringValue();
      if (mapping.containsKey(idStr)) {
        mapping.put(idStr, i);
        total--;
        if (total == 0) break;
      }
    }
    for (String s : externalIDs) {
      sb.append(mapping.get(s)+"\n");
    }
    return sb.toString();
  }

  String printDocumentName( DirectoryReader reader, int number ) throws IOException {
    StringBuilder sb = new StringBuilder();
    Document d = reader.document(number);
    IndexableField id = d.getField(IndexWebCollection.FIELD_ID);
    sb.append(id.stringValue())
      .append("\n");
    return sb.toString();
  }

  String printDocumentText( DirectoryReader reader, int number ) throws IOException, RawDocNotStoredException {
    StringBuilder sb = new StringBuilder();
    Document d = reader.document(number);
    IndexableField id = d.getField(IndexWebCollection.FIELD_BODY);
    if (id == null) {
      throw new RawDocNotStoredException("Raw Contents not Stored!");
    }
    sb.append(id.stringValue())
      .append("\n");
    return sb.toString();
  }

  String printDocumentVector( DirectoryReader reader, int number ) throws IOException, DocVectorNotStoredException {
    StringBuilder sb = new StringBuilder();
    Terms terms = reader.getTermVector(number, IndexWebCollection.FIELD_BODY);
    if (terms == null) {
      throw new DocVectorNotStoredException("Doc Vector not Stored!");
    }
    TermsEnum te = terms.iterator();
    if (te == null) {
      throw new DocVectorNotStoredException("Doc Vector not Stored!");
    }
    while((te.next()) != null){
      sb.append(te.term().utf8ToString()+" "+te.totalTermFreq()+"\n");
    }
    return sb.toString();
  }

  public static void main(String[] clArgs) throws IOException{
    DumpIndexArgs dumpIndexArgs = new DumpIndexArgs();
    CmdLineParser parser = new CmdLineParser(dumpIndexArgs, ParserProperties.defaults().withUsageWidth(90));
    try {
      parser.parseArgument(clArgs);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      return;
    }

    final DumpIndex ic = new DumpIndex();
    FSDirectory dir = FSDirectory.open(new File(dumpIndexArgs.index).toPath());
    DirectoryReader reader = DirectoryReader.open(dir);
    String dump = null;

    try {
      if (dumpIndexArgs.stats) {
        dump = ic.printRepositoryStats(reader);
      } else if (dumpIndexArgs.term != null) {
        dump = ic.printTermCounts(reader, dumpIndexArgs.term);
      } else if (dumpIndexArgs.di != null) {
        dump = ic.printDocumentIDs(reader, dumpIndexArgs.di);
      } else if (dumpIndexArgs.dn > 0) {
        dump = ic.printDocumentName(reader, dumpIndexArgs.dn);
      } else if (dumpIndexArgs.dt > 0) {
        dump = ic.printDocumentText(reader, dumpIndexArgs.dt);
      } else if (dumpIndexArgs.dv > 0) {
        dump = ic.printDocumentVector(reader, dumpIndexArgs.dv);
      } else {
        dump = "";
      }
      System.out.println(dump);
    } catch (Exception e) {
      LOG.error(e.getMessage());
    }
  }
}

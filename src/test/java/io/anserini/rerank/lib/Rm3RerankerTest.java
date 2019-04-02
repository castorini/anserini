package io.anserini.rerank.lib;

import io.anserini.index.IndexUtils;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.search.SearchArgs;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;

/**
 * Created by Ameer Albahem on 2/04/2019.
 */
public class Rm3RerankerTest {
    Rm3Reranker reranker = null;
    RerankerContext context = null;
    Directory directory = null;
    String queryText =  "run cat";
    private IndexReader directoryReader;
    private Analyzer analyzer;

    @Before
    public void setUp() throws Exception {

        directory = new RAMDirectory();
        analyzer = new EnglishAnalyzer();
        IndexWriterConfig config =  new IndexWriterConfig(analyzer);

        IndexWriter indexWriter = new IndexWriter(directory,config);
        FieldType fieldType = new FieldType();
        fieldType.setTokenized(true);
        fieldType.setStored(true);
        fieldType.setStoreTermVectors(true);
        fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        Document rD = new Document();

        rD.add(new TextField("id",new StringReader("1")));
        String text = "run run lion lion cat";


        Field field = new Field("text",text,fieldType);
        rD.add(field);
        indexWriter.addDocument(rD);
        indexWriter.close();

        directoryReader = DirectoryReader.open(directory);




    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void rerank() throws Exception {

        StandardQueryParser queryParser = new StandardQueryParser(analyzer);
        Query query = queryParser.parse(queryText,"text");
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);

        TopDocs topDocs = indexSearcher.search(query,10);
        SearchArgs searchArgs = new SearchArgs();
        searchArgs.arbitraryScoreTieBreak = true;
        context = new RerankerContext(indexSearcher,"1",query,"",queryText,null,null,searchArgs);
        reranker = new Rm3Reranker(analyzer,"text",10,10,0.5f,true);

        ScoredDocuments scoredDocuments = reranker.rerank(ScoredDocuments.fromTopDocs(topDocs,indexSearcher),context);

        /**
         *         run     cat   lion  program
         *   q     1       1     0     0
         *   rd    2       1     2     0
         *   -----------------------------
         *   m    2        1.5   2     -3
         */
    }

    @Test
    public void tag() throws Exception {

    }

}
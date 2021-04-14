package io.anserini.integration;

import io.anserini.collection.DocumentCollection;
import io.anserini.collection.JsonCollection;
import io.anserini.collection.TrecCollection;
import io.anserini.index.IndexArgs;
import io.anserini.index.IndexCollection;
import io.anserini.index.generator.DefaultLuceneDocumentGenerator;
import io.anserini.search.SearchArgs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class PretokenizedIndexEndToEndTest extends EndToEndTest {

    @Override
    IndexArgs getIndexArgs() {
        IndexArgs indexArgs = createDefaultIndexArgs();

        indexArgs.input = "src/test/resources/sample_docs/json/collection_tokenized";
        indexArgs.collectionClass = JsonCollection.class.getSimpleName();
        indexArgs.generatorClass = DefaultLuceneDocumentGenerator.class.getSimpleName();
        indexArgs.pretokenized = true;
        indexArgs.storeRaw = true;

        return indexArgs;
    }

    @Override
    protected void setCheckIndexGroundTruth() {
        docCount = 2;
        documents.put("2000000", Map.of(
                "contents", "this was ##a simple pretokenized test",
                "raw","{\n" +
                        "  \"id\" : \"2000000\",\n" +
                        "  \"contents\" : \"this was ##a simple pretokenized test\"\n" +
                        "}"));
        documents.put("2000001", Map.of(
                "contents", "some time extra ##vert ##ing and some time intro ##vert ##ing",
                "raw","{\n" +
                        "  \"id\" : \"2000001\",\n" +
                        "  \"contents\" : \"some time extra ##vert ##ing and some time intro ##vert ##ing\"\n" +
                        "}"
        ));
        tokens.put("2000000",Map.of("contents",
                Map.of("this",1L, "was",1L,"##a",1L,"simple",1L,"pretokenized",1L,"test",1L)));
        tokens.put("2000001",Map.of("contents",
                Map.of("some",2L, "time",2L,"extra",1L,"##vert",2L,"##ing",2L,"and",1L,"intro",1L)));

        fieldNormStatusTotalFields = 1;
        // whitespace analyzer keeps everything, includes docid
        // this is ##a simple pretokenized test some time extra ##vert ##ing and intro 2000000 2000001
        termIndexStatusTermCount = 15;
        termIndexStatusTotFreq = 15;
        storedFieldStatusTotalDocCounts = 2;
        termIndexStatusTotPos = 17 + storedFieldStatusTotalDocCounts;
        storedFieldStatusTotFields = 6; // 1 docs * (1 id + 1 contents + 1 raw) *2
    }

    @Override
    protected void setSearchGroundTruth() {
        topicReader = "TsvInt";
        topicFile = "src/test/resources/sample_topics/json_topics.tsv";
        SearchArgs searchArg = createDefaultSearchArgs().bm25();
        searchArg.pretokenized = true;
        testQueries.put("bm25", searchArg);
        queryTokens.put("1",new ArrayList<>());
        queryTokens.get("1").add("##ing");
        queryTokens.get("1").add("##vert");
        referenceRunOutput.put("bm25", new String[]{
                "1 Q0 2000001 1 0.922400 Anserini"});
    }

}

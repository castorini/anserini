package io.anserini.integration;

import io.anserini.collection.DocumentCollection;
import io.anserini.collection.JsonCollection;
import io.anserini.collection.TrecCollection;
import io.anserini.index.IndexArgs;
import io.anserini.index.IndexCollection;
import io.anserini.index.generator.DefaultLuceneDocumentGenerator;

import java.io.IOException;
import java.util.Map;


public class PretokenizedIndexEndToEndTest extends EndToEndTest {

    @Override
    IndexArgs getIndexArgs() {
        IndexArgs indexArgs = createDefaultIndexArgs();

        indexArgs.input = "src/test/resources/sample_docs/json/collection_tokenized";
        indexArgs.collectionClass = JsonCollection.class.getSimpleName();
        indexArgs.generatorClass = DefaultLuceneDocumentGenerator.class.getSimpleName();
        indexArgs.pretokenized = true;
        indexArgs.storeRaw = false;

        return indexArgs;
    }

    @Override
    protected void setCheckIndexGroundTruth() {
        docCount = 2;
        documents.put("2000000", Map.of(
                "contents", "this is ##a simple pretokeinzed test"));
        documents.put("2000001", Map.of(
                "contents", "some time extra ##vert ##ing and some time intro ##vert ##ing"));

        fieldNormStatusTotalFields = 1;
        // whitespace analyzer keeps everything, includes docid
        // this is ##a simple pretokenized test some time extra ##vert ##ing and intro 2000000 2000001
        termIndexStatusTermCount = 15;
        // this is ##a simple pretokenized test some|2 time|2 extra ##vert|2 ##ing|2 and intro 2000000 2000001
        termIndexStatusTotFreq = 15;
        storedFieldStatusTotalDocCounts = 2;
        termIndexStatusTotPos = 17 + storedFieldStatusTotalDocCounts;
        storedFieldStatusTotFields = 4; // 1 docs * (1 id + 1 contents)
    }

    @Override
    protected void setSearchGroundTruth() {

    }

}

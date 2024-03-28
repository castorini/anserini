package io.anserini.demo;

import io.anserini.search.ScoredDoc;
import io.anserini.search.SimpleSearcher;
import io.anserini.util.PrebuiltIndexHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SearchService {

    final private String indexDir;
    final private float k1 = 0.82f;
    final private float b = 0.68f;

    public SearchService(String prebuiltIndex) {
        PrebuiltIndexHandler handler = new PrebuiltIndexHandler(prebuiltIndex);
        handler.initialize();
        try {
            handler.download();
            indexDir = handler.decompressIndex();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<QueryResult> search(String query, int hits) {
        try {
            // index, k1, b, hits
            SimpleSearcher searcher = new SimpleSearcher(indexDir);
            searcher.set_bm25(k1, b);
            ScoredDoc[] results = searcher.search(query, hits);
            List<QueryResult> resultStrings = List.of(results).stream()
                    .map(result -> {
                        try {
                            String jsonString = searcher.doc_raw(result.lucene_docid);
                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode jsonNode = mapper.readTree(jsonString);
                            String content = jsonNode.get("contents").asText();
                            return new QueryResult(result.docid, content, result.score);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }).filter(Objects::nonNull).collect(Collectors.toList());
            searcher.close();
            return resultStrings;
        } catch (Exception e) {
            // Consume exception and return empty list
            e.printStackTrace();
            return List.of();
        }
    }
}
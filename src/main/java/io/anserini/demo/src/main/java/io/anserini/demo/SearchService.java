package io.anserini.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.anserini.search.ScoredDoc;
import io.anserini.search.SimpleSearcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Collectors;

public class SearchService {
    
    static final private String INDEX_DIR = "indexes/msmarco-passage/lucene-index-msmarco";
    static final private float k1 = 0.82f;
    static final private float b = 0.68f;

    public static List<QueryResult> search(String query, int hits) {
        try {
            // index, k1, b, hits
            SimpleSearcher searcher = new SimpleSearcher(INDEX_DIR);
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
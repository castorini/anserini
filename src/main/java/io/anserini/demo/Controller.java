package io.anserini.demo;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
public class Controller {
    @RequestMapping(method = RequestMethod.GET, path = "/query/{query}")
    public List<QueryResult> search(@PathVariable("query") String query) {
        SearchService searchService = new SearchService("msmarco-v1-passage");
        return searchService.search(query, 10);
    }
}
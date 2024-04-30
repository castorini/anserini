package io.anserini.server;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
public class Controller {

  private static final String DEFAULT_COLLECTION = "msmarco-v1-passage";

  @RequestMapping(method = RequestMethod.GET, path = {"/collection/{collection}/search", "/search"})
  public List<QueryResult> search(
      @PathVariable(value = "collection", required = false) String collection, 
      @RequestParam("query") String query) {

    if (collection == null) {
      collection = DEFAULT_COLLECTION;
    }

    SearchService searchService = new SearchService(collection);
    return searchService.search(query, 10);
  }

}
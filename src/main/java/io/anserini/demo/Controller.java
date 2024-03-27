package io.anserini.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
public class Controller {
    // consider RequestBody vs PathVariable
    @RequestMapping(method = RequestMethod.GET, path = "/query/{query}")
    public String search(@PathVariable("query") String query) {
        return query;
    }
}
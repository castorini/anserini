package io.anserini.ltr.feature;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;

import java.util.Collections;
import java.util.List;

public interface Pooler {

    float pool(List<Float> array);

    Pooler clone();

    String getName();
}


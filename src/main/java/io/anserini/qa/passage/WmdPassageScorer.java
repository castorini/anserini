/**
 * Anserini: An information retrieval toolkit built on Lucene
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.anserini.qa.passage;

import io.anserini.index.IndexUtils;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.FSDirectory;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.storage.StorageLevel;
import scala.Tuple2;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class WmdPassageScorer implements PassageScorer {

  public static class SentenceWMD implements Serializable {
    private String sentence;
    private double wmd;

    public String getSentence() {
      return sentence;
    }

    public double getWMD() {
      return wmd;
    }
  }

  private final SparkConf conf;
  private final JavaSparkContext sc;
  private final IndexUtils util;
  private final FSDirectory directory;
  private final DirectoryReader reader;
  private final Queue<ScoredPassage> scoredPassageHeap;
  private final int topPassages;

  public WmdPassageScorer(String index, Integer k) throws IOException {
    conf = new SparkConf().setAppName("WMD Passage Retriever");
    sc = new JavaSparkContext(conf);

    this.util = new IndexUtils(index);
    this.directory = FSDirectory.open(new File(index).toPath());
    this.reader = DirectoryReader.open(directory);
    this.scoredPassageHeap = new PriorityQueue<ScoredPassage>();
    this.topPassages = k;
  }

  public double distance(String leftVector, String rightVector) {
    double[] leftVec = Arrays.stream(leftVector.split("\\s+")).mapToDouble(java.lang.Double::parseDouble).toArray();
    double[] rightVec = Arrays.stream(rightVector.split("\\s+")).mapToDouble(java.lang.Double::parseDouble).toArray();
    double distance = 0.0;

    for (int i = 0; i <= leftVec.length; i++) {
      distance += Math.pow(leftVec[i] - rightVec[i], 2);
    }
    return  Math.sqrt(distance);
  }

  public List<Tuple2<Long, Tuple2<String, String>>> getWordPairs(Long id, String s1, String s2) {
    String[] w1s = s1.toLowerCase()
            .replaceAll("\\p{Punct}", "")
            .split(" ");
    String[] w2s = s2.toLowerCase()
            .replaceAll("\\p{Punct}", "")
            .split(" ");

    List<Tuple2<Long, Tuple2<String, String>>> wordPair = new ArrayList<>();
    for (String word1 : w1s) {
      for (String word2 : w2s) {
        wordPair.add(new Tuple2<>(id, new Tuple2<>(word1, word2)));
      }
    }
    return wordPair;
  }

  @Override
  public void score(List<String> sentences, String output) throws Exception {
    String question = sentences.get(0);
    JavaRDD<String> lines = sc.parallelize(sentences);

    JavaPairRDD<Tuple2<Object, Object>, Long> sentencePairs =
            lines.mapToPair(line -> new Tuple2(question, line))
            .zipWithIndex()
            .persist(StorageLevel.MEMORY_AND_DISK());

    JavaPairRDD<String, String> w2vs = sc.textFile("GoogleNews-vectors-negative300.tsv")
            .mapToPair(w -> {
                String[] wordVectorPair =  w.split("\t");
                return new Tuple2<>(wordVectorPair[0], wordVectorPair[1]);
            });

    JavaRDD<Tuple2<Long, Tuple2<String, String>>> wordPairs = sentencePairs.flatMap(ssi ->
            getWordPairs(ssi._2, ssi._1._1.toString(), ssi._1._2.toString()));

    JavaPairRDD<Tuple2<Long, String>, List<java.lang.Double>> wordVectors = wordPairs.mapToPair(l ->
            new Tuple2<>(l._2._2, new Tuple2<>(l._1, l._2._1))).join(w2vs)
    .mapToPair(l -> new Tuple2<>(l._2._1._2, new Tuple2<>(l._2._1._1, l._2._2))).join(w2vs)
    .mapToPair(l -> new Tuple2<>(new Tuple2<>(l._2._1._1, l._1), new Tuple2<>(l._2._2, l._2._1._2)))
    .mapToPair(l -> {
      List<java.lang.Double> dist = new ArrayList<>();
      dist.add(distance(l._2._1, l._2._2));
      return new Tuple2<>(new Tuple2<>(l._1._1, l._1._2), dist);
    })
    .persist(StorageLevel.MEMORY_AND_DISK());

    JavaPairRDD<Long, java.lang.Double> bestWMD = wordVectors
            .reduceByKey((m, n) -> {
              m.addAll(n);
              return m;
            })
            .mapValues(dist -> {
              Collections.sort(dist);
              return dist.get(0);
            })
            .mapToPair(l -> new Tuple2<>(l._1._1, l._2))
            .reduceByKey(java.lang.Double::sum);

    JavaRDD<Tuple2> results = sentencePairs.mapToPair(l -> l.swap()).join(bestWMD)
            .map(l -> new Tuple2<>(l._2._1._2, l._2._2));

    SQLContext sqlContext = new org.apache.spark.sql.SQLContext(sc);
    DataFrame resultDataFrame = sqlContext.createDataFrame(results, SentenceWMD.class).sort();
    resultDataFrame.show();
  }

  @Override
  public List<ScoredPassage> extractTopPassages() {
    return null;
  }
}

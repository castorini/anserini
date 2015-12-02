package io.anserini.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import com.google.common.collect.Maps;

public class Qrels {
  final private Map<String, Map<String, Integer>> qrels;

  public Qrels(String file) {
    qrels = Maps.newHashMap();

    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      String line;
      String[] arr;
      while ((line = br.readLine()) != null) {
        arr = line.split("[\\s\\t]+");
        String qid = arr[0];
        String docno = arr[2];
        int grade = Integer.parseInt(arr[3]);
        //System.out.println(qid + " " + docno + " " + grade);

        if (qrels.containsKey(qid)) {
          qrels.get(qid).put(docno, grade);
        } else {
          Map<String, Integer> t = Maps.newHashMap();
          t.put(docno, grade);
          qrels.put(qid, t);
        }
      }
    } catch (IOException e) {

    }
  }

  public int getRelevanceGrade(String qid, String docid) {
    if (!qrels.containsKey(qid)) {
      return 0;
    }

    if (!qrels.get(qid).containsKey(docid)) {
      return 0;
    }

    if ( qrels.get(qid).get(docid) <= 0 ) return 0;
    return qrels.get(qid).get(docid);
  }
}

/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
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

package io.anserini.util;

/**
 * An enumeration comprising standard sets of qrels from various evaluations.
 */
public enum Qrels {
  TREC1_ADHOC("topics-and-qrels/qrels.adhoc.51-100.txt"),
  TREC2_ADHOC("topics-and-qrels/qrels.adhoc.101-150.txt"),
  TREC3_ADHOC("topics-and-qrels/qrels.adhoc.151-200.txt"),
  ROBUST04("topics-and-qrels/qrels.robust04.txt"),
  ROBUST05("topics-and-qrels/qrels.robust05.txt"),
  CORE17("topics-and-qrels/qrels.core17.txt"),
  CORE18("topics-and-qrels/qrels.core18.txt"),
  WT10G("topics-and-qrels/qrels.adhoc.451-550.txt"),
  TREC2004_TERABYTE("topics-and-qrels/qrels.terabyte04.701-750.txt"),
  TREC2005_TERABYTE("topics-and-qrels/qrels.terabyte05.751-800.txt"),
  TREC2006_TERABYTE("topics-and-qrels/qrels.terabyte06.801-850.txt"),
  TREC2011_WEB("topics-and-qrels/qrels.web.101-150.txt"),
  TREC2012_WEB("topics-and-qrels/qrels.web.151-200.txt"),
  TREC2013_WEB("topics-and-qrels/qrels.web.201-250.txt"),
  TREC2014_WEB("topics-and-qrels/qrels.web.251-300.txt"),
  MB11("topics-and-qrels/qrels.microblog2011.txt"),
  MB12("topics-and-qrels/qrels.microblog2012.txt"),
  MB13("topics-and-qrels/qrels.microblog2013.txt"),
  MB14("topics-and-qrels/qrels.microblog2014.txt"),
  CAR17V15_BENCHMARK_Y1_TEST("topics-and-qrels/qrels.car17v1.5.benchmarkY1test.txt"),
  CAR17V20_BENCHMARK_Y1_TEST("topics-and-qrels/qrels.car17v2.0.benchmarkY1test.txt"),
  MSMARCO_DOC_DEV("topics-and-qrels/qrels.msmarco-doc.dev.txt"),
  MSMARCO_PASSAGE_DEV_SUBSET("topics-and-qrels/qrels.msmarco-passage.dev-subset.txt"),
  NTCIR8_ZH("topics-and-qrels/qrels.ntcir8zh.eval.txt"),
  CLEF2006_FR("topics-and-qrels/qrels.clef06fr.mono.fr.txt"),
  TREC2002_AR("topics-and-qrels/qrels.trec02ar-ar.txt"),
  FIRE2012_BN("topics-and-qrels/qrels.fire12bn.176-225.txt"),
  FIRE2012_HI("topics-and-qrels/qrels.fire12hi.176-225.txt"),
  FIRE2012_EN("topics-and-qrels/qrels.fire12en.176-225.txt"),
  TREC2018_BL("topics-and-qrels/qrels.backgroundlinking18.txt"),
  TREC2019_BL("topics-and-qrels/qrels.backgroundlinking19.txt");

  public final String path;
  
  Qrels(String path) {
    this.path = path;
  }
}

// public class Qrels {
//   final private Map<String, Map<String, Integer>> qrels;

//   public Qrels(String file) {
//     qrels = new HashMap<>();

//     try (BufferedReader br = new BufferedReader(new FileReader(file))) {
//       String line;
//       String[] arr;
//       while ((line = br.readLine()) != null) {
//         arr = line.split("[\\s\\t]+");
//         String qid = arr[0];
//         String docno = arr[2];
//         int grade = Integer.parseInt(arr[3]);
//         //System.out.println(qid + " " + docno + " " + grade);

//         if (qrels.containsKey(qid)) {
//           qrels.get(qid).put(docno, grade);
//         } else {
//           Map<String, Integer> t = new HashMap<>();
//           t.put(docno, grade);
//           qrels.put(qid, t);
//         }
//       }
//     } catch (IOException e) {

//     }
//   }

//   /**
//    * Method will return whether this docId for this qid is judged or not
//    * Note that if qid is invalid this will always return false
//    * @param qid     qid
//    * @param docid   docid
//    * @return true if docId is judged against qid false otherwise
//    */
//   public boolean isDocJudged(String qid, String docid) {
//     if (!qrels.containsKey(qid)) {
//       return false;
//     }

//     if (!qrels.get(qid).containsKey(docid)) {
//       return false;
//     } else {
//       return true;
//     }
//   }

//   public<K> int getRelevanceGrade(K qid, String docid) {
//     if (!qrels.containsKey(qid)) {
//       return 0;
//     }

//     if (!qrels.get(qid).containsKey(docid)) {
//       return 0;
//     }

//     if ( qrels.get(qid).get(docid) <= 0 ) return 0;
//     return qrels.get(qid).get(docid);
//   }

//   public Set<String> getQids() {
//       return this.qrels.keySet();
//   }

//   public Map<String, Integer> getDocMap(String qid) {
//     if (this.qrels.containsKey(qid)) {
//       return this.qrels.get(qid);
//     } else {
//       return null;
//     }
//   }
// }

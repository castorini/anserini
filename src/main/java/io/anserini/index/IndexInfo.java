/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
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

package io.anserini.index;

public enum IndexInfo {
  MSMARCO_V1_PASSAGE("msmarco-v1-passage",
      "Lucene index of the MS MARCO V1 passage corpus. (Lucene 9)",
      "lucene-index.msmarco-v1-passage.20221004.252b5e.tar.gz",
      new String[] {
          "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.msmarco-v1-passage.20221004.252b5e.tar.gz" },
      "c697b18c9a0686ca760583e615dbe450"),

  CACM("cacm",
      "Lucene index of the CACM corpus. (Lucene 9)",
      "lucene-index.cacm.tar.gz",
      new String[] {
          "https://github.com/castorini/anserini-data/raw/master/CACM/lucene-index.cacm.20221005.252b5e.tar.gz" },
      "cfe14d543c6a27f4d742fb2d0099b8e0"),

  MSMARCO_V1_PASSAGE_COS_DPR_DISTIL("msmarco-v1-passage-cos-dpr-distil",
      "Lucene index of the MS MARCO V1 passage corpus encoded by cos-DPR Distil. (Lucene 9)",
      "lucene-hnsw.msmarco-v1-passage-cos-dpr-distil.20231124.9d3427.tar.gz",
      new String[] {
          "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.msmarco-v1-passage-cos-dpr-distil.20231124.9d3427.tar.gz" },
      "7aa825e292a411abbe1585fb4d9f20ee"),

  MSMARCO_V1_PASSAGE_SPLADE_PP_ED("msmarco-v1-passage-splade-pp-ed",
      "Lucene impact index of the MS MARCO passage corpus encoded by SPLADE++ CoCondenser-EnsembleDistil. (Lucene 9)",
      "lucene-index.msmarco-v1-passage-splade-pp-ed.20230524.a59610.tar.gz",
      new String[] {
          "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.msmarco-v1-passage-splade-pp-ed.20230524.a59610.tar.gz" },
      "4b3c969033cbd017306df42ce134c395");

  public final String indexName;
  public final String description;
  public final String filename;
  public final String[] urls;
  public final String md5;

  // constructor with all 5 fields
  IndexInfo(String indexName, String description, String filename, String[] urls, String md5) {
    this.indexName = indexName;
    this.description = description;
    this.filename = filename;
    this.urls = urls;
    this.md5 = md5;
  }

  public static boolean contains(String indexName) {
    for (IndexInfo indexInfo : IndexInfo.values()) {
      if (indexInfo.indexName.equals(indexName)) {
        return true;
      }
    }
    return false;
  }

  public static IndexInfo get(String indexName) {
    for (IndexInfo indexInfo : IndexInfo.values()) {
      if (indexInfo.indexName.equals(indexName)) {
        return indexInfo;
      }
    }
    throw new IllegalArgumentException("Index name " + indexName + " not found!");
  }

}

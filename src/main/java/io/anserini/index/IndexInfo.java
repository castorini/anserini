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
  CACM("cacm",
      "Lucene index of the CACM corpus.",
      "lucene-index.cacm.tar.gz",
      new String[] {
          "https://github.com/castorini/anserini-data/raw/master/CACM/lucene-index.cacm.20221005.252b5e.tar.gz" },
      "cfe14d543c6a27f4d742fb2d0099b8e0"),

  MSMARCO_V1_PASSAGE("msmarco-v1-passage",
      "Lucene index of the MS MARCO V1 passage corpus.",
      "lucene-index.msmarco-v1-passage.20221004.252b5e.tar.gz",
      new String[] {
          "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.msmarco-v1-passage.20221004.252b5e.tar.gz" },
      "c697b18c9a0686ca760583e615dbe450"),

  MSMARCO_V1_PASSAGE_SPLADE_PP_ED("msmarco-v1-passage-splade-pp-ed",
      "Lucene impact index of the MS MARCO V1 passage corpus encoded by SPLADE++ CoCondenser-EnsembleDistil.",
      "lucene-index.msmarco-v1-passage-splade-pp-ed.20230524.a59610.tar.gz",
      new String[] {
          "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.msmarco-v1-passage-splade-pp-ed.20230524.a59610.tar.gz" },
      "4b3c969033cbd017306df42ce134c395"),

  MSMARCO_V1_PASSAGE_COS_DPR_DISTIL("msmarco-v1-passage-cos-dpr-distil",
      "Lucene HNSW index of the MS MARCO V1 passage corpus encoded by cos-DPR Distil.",
      "lucene-hnsw.msmarco-v1-passage-cos-dpr-distil.20240108.825148.tar.gz",
      new String[] {
          "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.msmarco-v1-passage-cos-dpr-distil.20240108.825148.tar.gz" },
      "4aa1b08067b9aa313d8aba8ca9d7d8a2"),

  MSMARCO_V1_PASSAGE_COS_DPR_DISTIL_QUANTIZED("msmarco-v1-passage-cos-dpr-distil-quantized",
      "Lucene quantized HNSW index of the MS MARCO V1 passage corpus encoded by cos-DPR Distil.",
      "lucene-hnsw.msmarco-v1-passage-cos-dpr-distil-int8.20240108.825148.tar.gz",
      new String[] {
          "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.msmarco-v1-passage-cos-dpr-distil-int8.20240108.825148.tar.gz" },
      "cc52b5cabe9886d42c58f9d87a5dfab1"),

  MSMARCO_V1_PASSAGE_BGE_BASE_EN15("msmarco-v1-passage-bge-base-en-v1.5",
      "Lucene HNSW index of the MS MARCO V1 passage corpus encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.msmarco-v1-passage-bge-base-en-v1.5.20240117.53514b.tar.gz",
      new String[] {
          "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.msmarco-v1-passage-bge-base-en-v1.5.20240117.53514b.tar.gz" },
      "29d41b7a3b6ffb23f09a54aea453cc4e"),

  MSMARCO_V1_PASSAGE_BGE_BASE_EN15_QUANTIZED("msmarco-v1-passage-bge-base-en-v1.5-quantized",
      "Lucene quantized HNSW index of the MS MARCO V1 passage corpus encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.msmarco-v1-passage-bge-base-en-v1.5-int8.20240117.53514b.tar.gz",
      new String[] {
          "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.msmarco-v1-passage-bge-base-en-v1.5-int8.20240117.53514b.tar.gz" },
      "51261598a7a108e88fa854971637b39c");

  public final String indexName;
  public final String description;
  public final String filename;
  public final String[] urls;
  public final String md5;

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

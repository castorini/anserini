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
  MSMARCO_V1_PASSAGE("msmarco-v1-passage", "Lucene index of the MS MARCO V1 passage corpus. (Lucene 9)", "lucene-index.msmarco-v1-passage.20221004.252b5e.tar.gz", "lucene-index.msmarco-v1-passage.20221004.252b5e.README.md", new String[] {"https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.msmarco-v1-passage.20221004.252b5e.tar.gz"}, "c697b18c9a0686ca760583e615dbe450", "2170758938", "352316036", "8841823", "2660824", false);

  public final String indexName;
  public final String description;
  public final String filename;
  public final String readme;
  public final String[] urls;
  public final String md5;
  public final String size;
  public final String totalTerms;
  public final String totalDocs;
  public final String totalUniqueTerms;
  public final boolean downloaded;

  // constructor with all 11 fields
  IndexInfo(String indexName, String description, String filename, String readme, String[] urls, String md5, String size, String totalTerms, String totalDocs, String totalUniqueTerms, boolean downloaded) {
    this.indexName = indexName;
    this.description = description;
    this.filename = filename;
    this.readme = readme;
    this.urls = urls;
    this.md5 = md5;
    this.size = size;
    this.totalTerms = totalTerms;
    this.totalDocs = totalDocs;
    this.totalUniqueTerms = totalUniqueTerms;
    this.downloaded = downloaded;
  }

  // // constructor with 10 fields
  // // dict_keys(['indexName', 'description', 'filename', 'urls', 'md5', 'size compressed (bytes)', 'total_terms', 'documents', 'unique_terms', 'downloaded'])
  // IndexInfo(String indexName, String description, String filename, String[] urls, String md5, String size, String totalTerms, String totalDocs, String totalUniqueTerms, boolean downloaded) {
  //   this.indexName = indexName;
  //   this.description = description;
  //   this.filename = filename;
  //   this.readme = "";
  //   this.urls = urls;
  //   this.md5 = md5;
  //   this.size = size;
  //   this.totalTerms = totalTerms;
  //   this.totalDocs = totalDocs;
  //   this.totalUniqueTerms = totalUniqueTerms;
  //   this.downloaded = downloaded;
  // }

  // // constructor with 10 fields - robust04
  // // dict_keys(['indexName', 'description', 'filename', 'readme', 'urls', 'md5', 'size compressed (bytes)', 'total_terms', 'documents', 'unique_terms'])
  // IndexInfo(String indexName, String description, String filename, String readme, String[] urls, String md5, String size, String totalTerms, String totalDocs, String totalUniqueTerms) {
  //   this.indexName = indexName;
  //   this.description = description;
  //   this.filename = filename;
  //   this.readme = readme;
  //   this.urls = urls;
  //   this.md5 = md5;
  //   this.size = size;
  //   this.totalTerms = totalTerms;
  //   this.totalDocs = totalDocs;
  //   this.totalUniqueTerms = totalUniqueTerms;
  //   this.downloaded = false;
  // }

  // // constructor with 9 fields
  // // dict_keys(['indexName', 'description', 'filename', 'urls', 'md5', 'size compressed (bytes)', 'total_terms', 'documents', 'unique_terms'])
  // IndexInfo(String indexName, String description, String filename, String[] urls, String md5, String size, String totalTerms, String totalDocs, String totalUniqueTerms) {
  //   this.indexName = indexName;
  //   this.description = description;
  //   this.filename = filename;
  //   this.readme = "";
  //   this.urls = urls;
  //   this.md5 = md5;
  //   this.size = size;
  //   this.totalTerms = totalTerms;
  //   this.totalDocs = totalDocs;
  //   this.totalUniqueTerms = totalUniqueTerms;
  //   this.downloaded = false;
  // }

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

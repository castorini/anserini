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

package io.anserini.integration;

import io.anserini.collection.JsonCollection;
import io.anserini.index.IndexCollection;
import io.anserini.index.generator.DefaultLuceneDocumentGenerator;
import io.anserini.search.SearchCollection;

import java.util.ArrayList;
import java.util.Map;

public class JsonEndToEndZhTest extends EndToEndTest {
  @Override
  IndexCollection.Args getIndexArgs() {
    IndexCollection.Args indexArgs = createDefaultIndexArgs();
    indexArgs.input = "src/test/resources/sample_docs/json/collection_zh";
    indexArgs.collectionClass = JsonCollection.class.getSimpleName();
    indexArgs.generatorClass = DefaultLuceneDocumentGenerator.class.getSimpleName();
    indexArgs.storeRaw = true;
    indexArgs.language = "zh";

    return indexArgs;
    }

  @Override
  protected void setCheckIndexGroundTruth() {
    docCount = 3;
    docFieldCount = 3; // id, raw, contents

    referenceDocs.put("doc1", Map.of(
      "contents", "滑铁卢大学（英语：University of Waterloo，常简称为UWaterloo、UW、滑大等）是加拿大安大略省滑铁卢的一所省立研究型大学，前身为教会学校，建校于1957年。因加拿大最早成立的计算机科学系而知名，工程系全科为建教合作制度，且拥有全球最大规模的独立数学院（Faculty of Mathematics）和加拿大最大的环境学院（Faculty of Environment）。",
      "raw","{\n" +
      "  \"id\" : \"doc1\",\n" +
      "  \"contents\" : \"滑铁卢大学（英语：University of Waterloo，常简称为UWaterloo、UW、滑大等）是加拿大安大略省滑铁卢的一所省立研究型大学，前身为教会学校，建校于1957年。因加拿大最早成立的计算机科学系而知名，工程系全科为建教合作制度，且拥有全球最大规模的独立数学院（Faculty of Mathematics）和加拿大最大的环境学院（Faculty of Environment）。\"\n" +
      "}"));
    referenceDocs.put("doc2", Map.of(
      "contents", "多伦多大学（英语：University of Toronto，UofT）位于加拿大安大略省多伦多市，是一所公立联邦制研究型大学，亦是加拿大乃至全球最顶尖的学府之一。它的主校区坐落在多伦多市中心，主要建筑散落于女王公园四周，与安大略省政府及议会相毗邻。",
      "raw","{\n" +
      "  \"id\" : \"doc2\",\n" +
      "  \"contents\" : \"多伦多大学（英语：University of Toronto，UofT）位于加拿大安大略省多伦多市，是一所公立联邦制研究型大学，亦是加拿大乃至全球最顶尖的学府之一。它的主校区坐落在多伦多市中心，主要建筑散落于女王公园四周，与安大略省政府及议会相毗邻。\"\n" +
      "}"
    ));
    referenceDocs.put("doc3", Map.of(
      "contents", "不列颠哥伦比亚大学（英语：University of British Columbia，法语：Université de la Colombie-Britannique，简称UBC），又或译为英属哥伦比亚大学等，简称卑诗大学或卑大，是一所位于加拿大卑斯省的公立大学，也是U15大学联盟、英联邦大学协会、环太平洋大学联盟、和Universitas 21成员之一。",
      "raw","{\n" +
      "  \"id\" : \"doc3\",\n" +
      "  \"contents\" : \"不列颠哥伦比亚大学（英语：University of British Columbia，法语：Université de la Colombie-Britannique，简称UBC），又或译为英属哥伦比亚大学等，简称卑诗大学或卑大，是一所位于加拿大卑斯省的公立大学，也是U15大学联盟、英联邦大学协会、环太平洋大学联盟、和Universitas 21成员之一。\"\n" +
      "}"
    ));

    fieldNormStatusTotalFields = 1;
    termIndexStatusTermCount = 203;
    termIndexStatusTotFreq = 232;
    storedFieldStatusTotalDocCounts = 3;
    termIndexStatusTotPos = 264 + storedFieldStatusTotalDocCounts;
    storedFieldStatusTotFields = 9;
  }

  @Override
  protected void setSearchGroundTruth() {
    topicReader = "TsvString";
    topicFile = "src/test/resources/sample_topics/zh_topics.tsv";
    SearchCollection.Args searchArg = createDefaultSearchArgs().bm25();
    searchArg.language = "zh";
    testQueries.put("bm25", searchArg);
    queryTokens.put("1", new ArrayList<>());
    queryTokens.get("1").add("滑铁");
    queryTokens.get("1").add("铁卢");
    referenceRunOutput.put("bm25", new String[]{
        "1 Q0 doc1 1 1.337800 Anserini"
    });
  }

}

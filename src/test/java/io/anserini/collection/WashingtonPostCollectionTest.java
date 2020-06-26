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

package io.anserini.collection;

import org.junit.Before;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class WashingtonPostCollectionTest extends DocumentCollectionTest<WashingtonPostCollection.Document> {

  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/washingtonpost/collection1/");
    collection = new WashingtonPostCollection(collectionPath);

    Path segment1 = Paths.get("src/test/resources/sample_docs/washingtonpost/collection1/articles.jl");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 2);

    totalSegments = 1;
    totalDocs = 2;

    expected.put("5f992bbc-4b9f-11e2-a6a6-aabac85e8036",
        Map.of("id", "5f992bbc-4b9f-11e2-a6a6-aabac85e8036",
            "title", "Controlled exposure to light can ease jet lag’s effects before and after a trip",
            "author", "Mike",
            "article_url", "https://www.washingtonpost.com/national/" +
                "controlled-exposure-to-light-can-ease-jet-lags-effects-before-and-after-a-trip/" +
                "2012/12/24/5f992bbc-4b9f-11e2-a6a6-aabac85e8036_story.html",
            "published_date", "1356999181000",
            // contents() returns the JSON that's been parsed into readable text for indexing.
            "contents", "Controlled exposure to light can ease jet lag’s effects before and after a trip\n" +
                "National\n" +
                "Using light to help reset your body clock\n" +
                "When traveling east:\n" +
                "A few days before you leave, start exposing yourself to bright light in the morning.\n" +
                "When traveling west:\n" +
                "When you arrive, expose yourself to light during the evening hours.\n",
            // raw() returns the entire JSON.
            "raw", "{\"id\": \"5f992bbc-4b9f-11e2-a6a6-aabac85e8036\", \"article_url\": \"https://www.washingtonpost.com/national/controlled-exposure-to-light-can-ease-jet-lags-effects-before-and-after-a-trip/2012/12/24/5f992bbc-4b9f-11e2-a6a6-aabac85e8036_story.html\", \"title\": \"Controlled exposure to light can ease jet lag’s effects before and after a trip\", \"author\": \"Mike\", \"published_date\": 1356999181000, \"contents\": [{\"content\": \"National\", \"mime\": \"text/plain\", \"type\": \"kicker\"}, {\"content\": \"Controlled exposure to light can ease jet lag’s effects before and after a trip\", \"mime\": \"text/plain\", \"type\": \"title\"}, {\"content\": \"Using light to help reset your body clock\", \"subtype\": \"subhead\", \"type\": \"sanitized_html\", \"mime\": \"text/plain\"}, {\"content\": \"When traveling east:\", \"subtype\": \"paragraph\", \"type\": \"sanitized_html\", \"mime\": \"text/plain\"}, {\"content\": \"A few days before you leave, start exposing yourself to bright light in the morning.\", \"subtype\": \"paragraph\", \"type\": \"sanitized_html\", \"mime\": \"text/plain\"}, {\"content\": \"When traveling west:\", \"subtype\": \"paragraph\", \"type\": \"sanitized_html\", \"mime\": \"text/plain\"}, {\"content\": \"When you arrive, expose yourself to light during the evening hours.\", \"subtype\": \"paragraph\", \"type\": \"tweet\", \"mime\": \"text/plain\"}], \"type\": \"article\", \"source\": \"The Washington Post\"}"));
    
    // Article for which publication date was retrieved from contents section and stored in published_date.
    expected.put("3BKCWWXFCAI6PJS5DLAP27YJPY",
        Map.of("id", "3BKCWWXFCAI6PJS5DLAP27YJPY",
            "title", "University of Maryland is bringing upscale hotels, restaurants to College Park",
            "author", "Katherine Shaver",
            "article_url", "/local/trafficandcommuting/university-of-maryland-is-bringing-upscale-hotels-" +
                "restaurants-to-college-park/2017/12/29/d8542b5a-e510-11e7-a65d-1ac0fd7f097e_story.html", 
            "published_date", "1514865008000",
            "contents", "University of Maryland is bringing upscale hotels, restaurants to College Park\n" +
                "Trafficandcommuting\n" +
                "The four-star hotel with sleek gas fireplaces and modern chandeliers where\n" +
                "But Killion and his group were thrilled — and a bit\n" +
                "\"One of the challenges of adult life at the University of Maryland is finding a place\n",
            "raw", "{\"id\": \"3BKCWWXFCAI6PJS5DLAP27YJPY\", \"article_url\": \"/local/trafficandcommuting/university-of-maryland-is-bringing-upscale-hotels-restaurants-to-college-park/2017/12/29/d8542b5a-e510-11e7-a65d-1ac0fd7f097e_story.html\", \"title\": \"University of Maryland is bringing upscale hotels, restaurants to College Park\", \"author\": \"Katherine Shaver\", \"contents\": [{\"content\": \"Trafficandcommuting\", \"mime\": \"text/plain\", \"storyType\": \"News\", \"type\": \"kicker\"}, {\"content\": \"University of Maryland is bringing upscale hotels, restaurants to College Park\", \"mime\": \"text/plain\", \"type\": \"title\"}, {\"content\": \"By Katherine Shaver\", \"mime\": \"text/plain\", \"type\": \"byline\"}, {\"content\": 1514865008000, \"mime\": \"text/plain\", \"type\": \"date\"}, {\"content\": \"The four-star hotel with sleek gas fireplaces and modern chandeliers where\", \"mime\": \"text/html\", \"type\": \"sanitized_html\", \"subtype\": \"paragraph\"}, {\"content\": \"But Killion and his group were thrilled \\u2014 and a bit\", \"mime\": \"text/html\", \"type\": \"sanitized_html\", \"subtype\": \"paragraph\"}, {\"content\": \"\\\"One of the challenges of adult life at the University of Maryland is finding a place\", \"mime\": \"text/html\", \"type\": \"sanitized_html\", \"subtype\": \"paragraph\"}], \"type\": \"article\", \"source\": \"The Washington Post\"}"));
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    assertTrue(doc.indexable());
    assertEquals(expected.get("id"), doc.id());
    assertEquals(expected.get("raw"), doc.raw());
    assertEquals(expected.get("contents"), doc.contents());

    assertEquals(expected.get("title"), ((WashingtonPostCollection.Document) doc).getTitle().get());
    assertEquals(expected.get("author"), ((WashingtonPostCollection.Document) doc).getAuthor().get());
    assertEquals(expected.get("article_url"), ((WashingtonPostCollection.Document) doc).getArticleUrl().get());
    assertEquals((long) Long.valueOf(expected.get("published_date")),
        ((WashingtonPostCollection.Document) doc).getPublishDate());
  }
}

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

package io.anserini.collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.NoSuchElementException;

public class WikipediaArticleTest extends DocumentTest<WikipediaCollection.Document> {
  private static final Logger LOG = LogManager.getLogger(WashingtonPostDocumentTest.class);

  @Before
  public void setUp() throws Exception {
    super.setUp();

    String doc = "<mediawiki xmlns=\"http://www.mediawiki.org/xml/export-0.10/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.mediawiki.org/xml/export-0.10/ http://www.mediawiki.org/xm\n" +
        "l/export-0.10.xsd\" version=\"0.10\" xml:lang=\"en\">\n" +
        "  <siteinfo>\n" +
        "    <sitename>Wiktionary</sitename>\n" +
        "    <dbname>enwiktionary</dbname>\n" +
        "    <base>https://en.wiktionary.org/wiki/Wiktionary:Main_Page</base>\n" +
        "    <generator>MediaWiki 1.31.0-wmf.26</generator>\n" +
        "    <case>case-sensitive</case>\n" +
        "    <namespaces>\n" +
        "      <namespace key=\"-2\" case=\"case-sensitive\">Media</namespace>\n" +
        "      <namespace key=\"-1\" case=\"first-letter\">Special</namespace>\n" +
        "      <namespace key=\"0\" case=\"case-sensitive\" />\n" +
        "      <namespace key=\"1\" case=\"case-sensitive\">Talk</namespace>\n" +
        " .... " +
        "    </namespaces>\n" +
        "  </siteinfo>\n" +
        "  <page>\n" +
        "    <title>Wiktionary:Welcome, newcomers</title>\n" +
        "    <ns>0</ns>\n" +
        "    <id>7</id>\n" +
        "<revision>\n" +
        "      <id>28863815</id>\n" +
        "      <parentid>18348012</parentid>\n" +
        "      <timestamp>2014-08-24T22:39:11Z</timestamp>\n" +
        "      <contributor>\n" +
        "        <username>Ready Steady Yeti</username>\n" +
        "        <id>1705268</id>\n" +
        "      </contributor>\n" +
        "      <comment>This link doesn't actually exist. If you don't believe me, check it yourself.</comment>\n" +
        "      <model>wikitext</model>\n" +
        "      <format>text/x-wiki</format>\n" +
        "      <text xml:space=\"preserve\">this is the \n" +
        " real content \n" +
        "      </text>\n" +
        "  </page>\n" +
        "</mediawiki>";

    rawFiles.add(createFile(doc));
  }

  @Test
  public void test() throws IOException {
    WikipediaCollection collection = new WikipediaCollection();
    for (int i = 0; i < rawFiles.size(); i++) {
      AbstractFileSegment<WikipediaCollection.Document> iter = collection.createFileSegment(rawFiles.get(i));
      while (true) {
        try {
          WikipediaCollection.Document parsed = iter.next();
          assertEquals(parsed.id(), "Wiktionary:Welcome, newcomers");
          assertEquals(parsed.content(), "Wiktionary:Welcome, newcomers.\nthis is the   real content");
        } catch (NoSuchElementException e) {
          break;
        }
      }
    }
  }
}

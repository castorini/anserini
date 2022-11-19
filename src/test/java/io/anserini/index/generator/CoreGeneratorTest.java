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

package io.anserini.index.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import io.anserini.analysis.DefaultEnglishAnalyzer;
import io.anserini.collection.CoreCollection;
import io.anserini.index.Constants;
import io.anserini.index.IndexCollection;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StringField;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class CoreGeneratorTest {
  private CoreCollection.Document coreDoc;
  private Document doc;

  @Before
  public void setUp() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode coreJsonObj = mapper.createObjectNode();
    coreJsonObj.set("coreId", TextNode.valueOf("id_text"));
    coreJsonObj.set("doi", TextNode.valueOf("doi_text"));
    coreJsonObj.set("oai", TextNode.valueOf("oai_text"));
    coreJsonObj.set("title", TextNode.valueOf("every startup ever"));
    coreJsonObj.set("abstract", TextNode.valueOf("machine learning blockchain quantum vr"));
    coreJsonObj.set("year", IntNode.valueOf(2020));
    coreJsonObj.set("authors", mapper.createArrayNode().add("Elon Musk").add("Mark Zuckerberg"));
    coreJsonObj.set("contributors", mapper.createArrayNode());
    coreJsonObj.set("publisher", NullNode.getInstance());
    coreJsonObj.set("datePublished", TextNode.valueOf("2020-01-01"));
    coreJsonObj.set("pdfHashValue", TextNode.valueOf("abc"));
    coreJsonObj.set("downloadUrl", NullNode.getInstance());
    coreJsonObj.set("topics", mapper.createArrayNode().add("Machine Learning").add("Blockchain"));
    coreJsonObj.set("subjects", mapper.createArrayNode().add("Quantum").add("VR"));
    coreJsonObj.set("journals", mapper.createArrayNode().add("journal"));
    coreJsonObj.set("identifiers", mapper.createArrayNode());
    coreJsonObj.set("language", mapper.createObjectNode());
    coreJsonObj.set("relations", mapper.createObjectNode().set("sample", TextNode.valueOf("text")));
    coreJsonObj.set("fullTextIdentifier", NullNode.getInstance());
    coreJsonObj.set("enrichments", ((ObjectNode) mapper.createObjectNode()
      .set("references", mapper.createArrayNode()))
      .set("documentType",
        ((ObjectNode) mapper.createObjectNode()
          .set("type", NullNode.getInstance()))
          .set("confidence", NullNode.getInstance())));

    coreDoc = new CoreCollection.Document(coreJsonObj);

    CoreGenerator generator = new CoreGenerator(new IndexCollection.Args());
    doc = generator.createDocument(coreDoc);
  }

  @Test
  public void testDocumentFields() {
    // test proper id and contents field generated from CoreCollection
    assertEquals("doi_text", doc.getField(Constants.ID).stringValue());
    assertEquals("every startup ever machine learning blockchain quantum vr",
      doc.getField(Constants.CONTENTS).stringValue());

    // integer field value
    assertEquals(2020, doc.getField(CoreGenerator.CoreField.YEAR.name).numericValue());

    // array field values
    assertEquals("", doc.getField(CoreGenerator.CoreField.IDENTIFIERS.name).stringValue());
    assertEquals("journal", doc.getField(CoreGenerator.CoreField.JOURNALS.name).stringValue());

    // null field value
    assertEquals("", doc.getField(CoreGenerator.CoreField.DOWNLOAD_URL.name).stringValue());

    // make sure specified fields are stored as single tokens
    CoreGenerator.STRING_FIELD_NAMES.forEach(field ->
      assertEquals(StringField.class, doc.getField(field).getClass())
    );

    // make sure specified fields are stored without stemming
    Analyzer nonStemmingAnalyzer = DefaultEnglishAnalyzer.newNonStemmingInstance(CharArraySet.EMPTY_SET);
    CoreGenerator.FIELDS_WITHOUT_STEMMING.forEach(field -> {
      String fieldString = coreDoc.jsonNode().get(field).toString();

      assertEquals(nonStemmingAnalyzer.tokenStream(null, new StringReader(fieldString)),
        doc.getField(field).tokenStream(null, null));
    });
    nonStemmingAnalyzer.close();

    // make sure year is stored as numeric
    assertEquals(IntPoint.class, doc.getField(CoreGenerator.CoreField.YEAR.name).getClass());
  }
}

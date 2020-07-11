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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.StringBuilder;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Iso19115Collection extends DocumentCollection<Iso19115Collection.Document>{
  public Iso19115Collection(Path path){
    this.path = path;
    this.allowedFileSuffix = new HashSet<>(Arrays.asList(".json", ".jsonl"));
  }

  @Override
  public FileSegment<Iso19115Collection.Document> createFileSegment(Path p) throws IOException{
    return new Segment(p);
  }

  public static class Segment extends FileSegment<Iso19115Collection.Document> {
    private JsonNode node = null;
    private Iterator<JsonNode> iter = null;
    private MappingIterator<JsonNode> iterator;

    public Segment(Path path) throws IOException {
      super(path);
      bufferedReader = new BufferedReader(new FileReader(path.toString()));
      ObjectMapper mapper = new ObjectMapper();
      iterator = mapper.readerFor(JsonNode.class).readValues(bufferedReader);
      if(iterator.hasNext()){
        node = iterator.next();
        if(node.isArray()) {
          iter = node.elements();
        }
      }
    }

    @Override
    public void readNext() throws NoSuchElementException {
      if (node == null){
        throw new NoSuchElementException("JsonNode is empty");
      } else if (node.isObject()) {
        bufferedRecord = new Iso19115Collection.Document(node);
        if(iterator.hasNext()) {
          node = iterator.next();
        } else {
          atEOF = true;
        }
      } else if (node.isArray()) {
        if (iter != null && iter.hasNext()) {
          JsonNode json = iter.next();
          bufferedRecord = new Iso19115Collection.Document(node);
        } else {
          throw new NoSuchElementException("Reached end of JsonNode iterator");
        }
      } else {
        throw new NoSuchElementException("Invalid JsonNode type");
      } 
    }
  }

  public static class Document implements SourceDocument{
    protected String id;
    protected String title;
    protected String abstractContent;
    protected String raw;
    protected String organisation;
    protected String[] responsibleParty;
    protected String catalogue;
    protected String publish_time;
    protected String url;
    protected double[] latitude;
    protected double[] longitude;
    protected String coordinates;
    // new entried added
    protected String purpose;
    protected String supplInfo;
    protected String topicCategory;
    protected String[] keywords;
    protected String recommendedCitation;
    protected String thesaurusName;

    public Document(JsonNode json) {
      // extracting the fields from the ISO19115 file
      this.raw = json.toString();
      String identifier = json.get("gmd:MD_Metadata").get("gmd:fileIdentifier").get("gco:CharacterString").asText();
      // extracting the id in the beginning of the text
      this.id = identifier.substring(0,identifier.length() - 8);
      this.title = json.get("gmd:MD_Metadata").get("gmd:identificationInfo").get("gmd:MD_DataIdentification").get("gmd:citation")
                   .get("gmd:CI_Citation").get("gmd:title").get("gco:CharacterString").asText();
      this.abstractContent = json.get("gmd:MD_Metadata").get("gmd:identificationInfo").get("gmd:MD_DataIdentification")
                             .get("gmd:abstract").get("gco:CharacterString").asText();
      this.organisation = json.get("gmd:MD_Metadata").get("gmd:contact").get("gmd:CI_ResponsibleParty").get("gmd:organisationName")
                    .get("gco:CharacterString").asText();
      this.catalogue = json.get("gmd:MD_Metadata").get("gmd:contact").get("gmd:CI_ResponsibleParty").get("gmd:individualName")
              .get("gco:CharacterString").asText();
      this.publish_time = json.get("gmd:MD_Metadata").get("gmd:dateStamp").get("gco:Date").asText();
      this.url = json.get("gmd:MD_Metadata").get("gmd:dataSetURI").get("gco:CharacterString").asText();
      this.purpose = json.get("gmd:MD_Metadata").get("gmd:identificationInfo").get("gmd:MD_DataIdentification").get("gmd:purpose")
                     .get("gco:CharacterString").asText();
      this.supplInfo = json.get("gmd:MD_Metadata").get("gmd:identificationInfo").get("gmd:MD_DataIdentification").get("gmd:supplementalInformation")
                       .get("gco:CharacterString").asText();
      this.topicCategory = json.get("gmd:MD_Metadata").get("gmd:identificationInfo").get("gmd:MD_DataIdentification").get("gmd:topicCategory")
                           .get("gmd:MD_TopicCategoryCode").asText();
      this.recommendedCitation = json.get("gmd:MD_Metadata").get("gmd:identificationInfo").get("gmd:MD_DataIdentification").get("gmd:citation")
                                 .get("gmd:CI_Citation").get("gmd:otherCitationDetails").get("gco:CharacterString").asText();
      this.thesaurusName = json.get("gmd:MD_Metadata").get("gmd:identificationInfo").get("gmd:MD_DataIdentification").get("gmd:descriptiveKeywords")
                           .get(0).get("gmd:MD_Keywords").get("gmd:thesaurusName").get("gmd:CI_Citation").get("gmd:title").get("gco:CharacterString").asText()
                           + " : " +
                           json.get("gmd:MD_Metadata").get("gmd:identificationInfo").get("gmd:MD_DataIdentification").get("gmd:descriptiveKeywords")
                           .get(0).get("gmd:MD_Keywords").get("gmd:thesaurusName").get("gmd:CI_Citation").get("gmd:otherCitationDetails").get("gco:CharacterString")
                           .asText();

      // extracting all the authors of the paper
      JsonNode parties_node = json.get("gmd:MD_Metadata").get("gmd:identificationInfo").get("gmd:MD_DataIdentification").get("gmd:citation")
                             .get("gmd:CI_Citation").get("gmd:citedResponsibleParty");
      // extracting individual authors from the ResponsibleParty field
      int number_of_parties = parties_node.size();
      responsibleParty = new String[number_of_parties];
      for(int i=0; i < number_of_parties; i++){
        responsibleParty[i] = parties_node.get(i).get("gmd:CI_ResponsibleParty").get("gmd:individualName").get("gco:CharacterString").asText();
      }

      // extracting all the keywords of the paper
      JsonNode keyword_node = json.get("gmd:MD_Metadata").get("gmd:identificationInfo").get("gmd:MD_DataIdentification").get("gmd:descriptiveKeywords")
                              .get(0).get("gmd:MD_Keywords").get("gmd:keyword");
      // extracting individual keyword from the keyword field
      int number_of_keywords = keyword_node.size();
      keywords = new String[number_of_keywords];
      for(int i=0; i < number_of_keywords; i++){
        keywords[i] = keyword_node.get(i).get("gco:CharacterString").asText();
      }


      // extracting the latitudes from the paper, 5 points as the polygon needs to be enclosed
      latitude = new double[4];
      latitude[0] = json.get("gmd:MD_Metadata").get("gmd:identificationInfo").get("gmd:MD_DataIdentification").get("gmd:extent").get("gmd:EX_Extent")
                    .get("gmd:geographicElement").get("gmd:EX_GeographicBoundingBox").get("gmd:northBoundLatitude").get("gco:Decimal").asDouble();
      latitude[2] = json.get("gmd:MD_Metadata").get("gmd:identificationInfo").get("gmd:MD_DataIdentification").get("gmd:extent").get("gmd:EX_Extent")
              .get("gmd:geographicElement").get("gmd:EX_GeographicBoundingBox").get("gmd:southBoundLatitude").get("gco:Decimal").asDouble();
      // ensuring that a single coordinate location will be drawn as a small rectangle
      if (latitude[0] == latitude[2]) {
        latitude[0] -= 0.01;
        latitude[2] += 0.01;
      }
      latitude[1] = latitude[0];
      latitude[3] = latitude[2];

      // extracting the longitudes from the paper, again 5 points are needed to enclose the polygon
      longitude = new double[4];
      longitude[0] = json.get("gmd:MD_Metadata").get("gmd:identificationInfo").get("gmd:MD_DataIdentification").get("gmd:extent").get("gmd:EX_Extent")
              .get("gmd:geographicElement").get("gmd:EX_GeographicBoundingBox").get("gmd:westBoundLongitude").get("gco:Decimal").asDouble();
      longitude[1] = json.get("gmd:MD_Metadata").get("gmd:identificationInfo").get("gmd:MD_DataIdentification").get("gmd:extent").get("gmd:EX_Extent")
              .get("gmd:geographicElement").get("gmd:EX_GeographicBoundingBox").get("gmd:eastBoundLongitude").get("gco:Decimal").asDouble();
      // ensuring that a single coordinate location will be drawn as a small rectangle
      if (longitude[0] == longitude[1]) {
        longitude[0] -= 0.01;
        longitude[1] += 0.01;
      }
      longitude[2] = longitude[1];
      longitude[3] = longitude[0];

      this.coordinates = getCoordinateString();
    }

    public String getTitle() {
      return title;
    }

    public String getAbstract() {
      return abstractContent;
    }

    public String getOrganisation() {
      return organisation;
    }

    public String[] getResponsibleParty() {
      return responsibleParty;
    }

    public String getCatalogue() {
      return catalogue;
    }

    public String getPublish_time() {
      return publish_time;
    }

    public String getUrl() {
      return url;
    }

    public String getCoordinates() {
      return coordinates;
    }

    public String getSupplInfo() {
      return supplInfo;
    }

    public String getTopicCategory() {
      return topicCategory;
    }

    public String[] getKeywords() {
      return keywords;
    }

    public String getRecommendedCitation() {
      return recommendedCitation;
    }

    public String getThesaurusName() {
      return thesaurusName;
    }

    public String getPurpose() {return purpose;}

    private String getCoordinateString() {
      StringBuilder coordinates = new StringBuilder("[");
      // generating it in this form for literal evaluation in javascript
      for(int i=0; i < 4; i++) {
        coordinates.append("[");
        coordinates.append(latitude[i]);
        coordinates.append(",");
        coordinates.append(longitude[i]);
        coordinates.append("]");
        if (i != 3) {
          coordinates.append(",");
        }
      }
      coordinates.append("]");
      return coordinates.toString();
    }

    // public double[] getLatitude() { return latitude; }

    // public double[] getLongitude() { return longitude; }

    @Override
    public String id() {
      return id;
    }

    @Override
    public String contents() {
      return title + "\n" + abstractContent;
    }

    @Override
    public String raw() {
      return raw;
    }

    @Override
    public boolean indexable() {
      return true;
    }

  }
}

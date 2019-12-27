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

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An instance of the <a href="https://catalog.ldc.upenn.edu/products/LDC2008T19">New York Times
 * Annotated Corpus</a>.
 * This class works for both compressed <code>tgz</code> files or uncompressed <code>xml</code>
 * files.
 */
public class NewYorkTimesCollection extends DocumentCollection<NewYorkTimesCollection.Document> {
  private static final Logger LOG = LogManager.getLogger(NewYorkTimesCollection.class);

  public NewYorkTimesCollection(){
    this.allowedFileSuffix = new HashSet<>(Arrays.asList(".xml", ".tgz"));
  }

  @Override
  public FileSegment<NewYorkTimesCollection.Document> createFileSegment(Path p) throws IOException {
    return new Segment(p);
  }

  /**
   * An individual file from the
   * <a href="https://catalog.ldc.upenn.edu/products/LDC2008T19">New York Times Annotated Corpus</a>.
   * This class works for both compressed <code>tgz</code> files or uncompressed <code>xml</code>
   * files.
   */
  public static class Segment extends FileSegment<NewYorkTimesCollection.Document>{
    private final NewYorkTimesCollection.Parser parser = new NewYorkTimesCollection.Parser();
    private TarArchiveInputStream tarInput = null;
    private ArchiveEntry nextEntry = null;

    protected Segment(Path path) throws IOException {
      super(path);
      if (this.path.toString().endsWith(".tgz")) {
        tarInput = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(path.toFile())));
      }
    }

    @Override
    protected void readNext() throws IOException, NoSuchElementException {
      try {
        if (path.toString().endsWith(".tgz")) {
          getNextEntry();
          bufferedReader = new BufferedReader(new InputStreamReader(tarInput, "UTF-8"));
          File file = new File(nextEntry.getName()); // this is actually not a real file, only to match the method in Parser
          bufferedRecord = parser.parseFile(bufferedReader, file);
        } else {
          bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path.toFile()), "UTF-8"));
          bufferedRecord = parser.parseFile(bufferedReader, path.toFile());
          atEOF = true; // if it is a xml file, the segment only has one file, boolean to keep track if it's been read.
        }
      } catch (IOException e1) {
        if (path.toString().endsWith(".xml")) {
          atEOF = true;
        }
        throw e1;
      }
    }

    private void getNextEntry() throws IOException {
      nextEntry = tarInput.getNextEntry();
      if (nextEntry == null) {
        throw new NoSuchElementException();
      }
      // an ArchiveEntry may be a directory, so we need to read a next one.
      //   this must be done after the null check.
      if (nextEntry.isDirectory()) {
        getNextEntry();
      }
    }
  }

  /**
   * A document from the <a href="https://catalog.ldc.upenn.edu/products/LDC2008T19">New York Times
   * Annotated Corpus</a>.
   */
  public static class Document implements SourceDocument {
    private final RawDocument raw;
    private String id;
    private String contents;

    // No public constructor; must use parser to create document.
    private Document(RawDocument raw) {
      this.raw = raw;
    }

    @Override
    public String id() {
      return id;
    }

    @Override
    public String content() {
      return contents;
    }

    @Override
    public boolean indexable() {
      return true;
    }

    public RawDocument getRawDocument() {
      return raw;
    }
  }

  // We intentionally segregate the Anserini NewYorkTimesDocument from the parsed document below.

  /**
   * Raw container class for a document from <a href="https://catalog.ldc.upenn.edu/products/LDC2008T19">New York Times Annotated Corpus</a>.
   * This was originally distributed as part of the corpus as a class called {@code NYTCorpusDocument}.
   *
   * @author Evan Sandhaus
   */
  public static class RawDocument {
    /**
     * This field specifies the location on nytimes.com of the article. When
     * present, this URL is preferred to the URL field on articles published on
     * or after April 02, 2006, as the linked page will have richer content.
     */
    protected URL alternateURL;

    /**
     * This field is a summary of the article written by the New York Times
     * Indexing Service.
     */
    protected String articleAbstract;

    /**
     * This field specifies the biography of the author of the article.
     * Generally, this field is specified for guest authors not for New York
     * Times reporters. When this field is specified for Times reporters, it is
     * usually used to provide the author's email address.
     */
    protected String authorBiography;

    /**
     * The banner field is used to indicate if there has been additional
     * information appended to the articles since its publication. Examples of
     * banners include ('Correction Appended' and 'Editor's Note Appended').
     */
    protected String banner;

    /**
     * When present, the biographical category field generally indicates that a
     * document focuses on a particular individual. The value of the field
     * indicates the area or category in which this individual is best known.
     * This field is most often defined for Obituaries and Book Reviews. These
     * tags are hand-assigned by a team of library scientists working for the
     * New York Times Indexing service.
     *
     * <ol>
     * <li>Politics and Government (U.S.) <li>Books and Magazines <li>Royalty
     * </ol>
     */
    protected List<String> biographicalCategories = new ArrayList<String>();

    /**
     * The body field is the text content of the article. Please note that this
     * value includes the lead paragraph.
     */
    protected String body;

    /**
     * This field specifies the byline of the article as it appeared in the
     * print edition of the New York Times. Please note that not every article
     * in this collection has a byline, as editorials and other types of
     * articles are generally unsigned.
     * <P>
     * Sample byline:
     * <ul>
     * <li>By James Reston
     * <li>By JAMES GLANZ; William J. Broad contributed reporting for this
     * article.
     * <li>By ADAM NAGOURNEY and JEFF ZELENY
     * </ul>
     */
    protected String byline;

    /**
     * If the article is part of a regular column, this field specifies the name
     * of that column.
     * <p>
     * Sample Column Names:
     * </p>
     * <ol>
     * <li>World News Briefs
     * <li>WEDDINGS
     * <li>The Accessories Channel
     * </ol>
     *
     */
    protected String columnName;

    /**
     * This field specifies the column in which the article starts in the print
     * paper. A typical printed page in the paper has six columns numbered from
     * right to left. As a consequence most, but not all, of the values for this
     * field fall in the range 1-6.
     */
    protected Integer columnNumber;

    /**
     * This field specifies the date on which a correction was made to the
     * article. Generally, if the correction date is specified, the correction
     * text will also be specified (and vice versa).
     */
    protected Date correctionDate;

    /**
     * For articles corrected following publication, this field specifies the
     * correction. Generally, if the correction text is specified, the
     * correction date will also be specified (and vice versa).
     */
    protected String correctionText;

    /**
     * This field indicates the entity that produced the editorial content of
     * this document. For this collection, the credit will always be set to 'The
     * New York Times'.
     */
    protected String credit;

    /**
     * The "dateline" field is the dateline of the article. Generally a dateline
     * is the name of the geographic location from which the article was filed
     * followed by a comma and the month and day of the filing.
     * <p>
     * Sample datelines:
     * <ul>
     * <li>WASHINGTON, April 30
     * <li>RIYADH, Saudi Arabia, March 29
     * <li>ONTARIO, N.Y., Jan. 26
     * </ul>
     * Please note:
     * <ol>
     * <li>The dateline location is the location from which the article was
     * filed. Often times this location is related to the content of the
     * article, but this is not guaranteed.
     * <li>The date specified for the dateline is often but not always the day
     * previous to the publication date.
     * <li>The date is usually but not always specified.
     * </ol>
     */
    protected String dateline;

    /**
     * This field specifies the day of week on which the article was published.
     * <ul>
     * <li>Monday <li>Tuesday <li>Wednesday <li>Thursday <li>Friday <li>Saturday
     * <li>Sunday
     * </ul>
     */
    protected String dayOfWeek;

    /**
     * The "descriptors" field specifies a list of descriptive terms drawn from
     * a normalized controlled vocabulary corresponding to subjects mentioned in
     * the article. These tags are hand-assigned by a team of library scientists
     * working in the New York Times Indexing service.
     * <p>
     * Examples Include:
     * <ol>
     * <li>ECONOMIC CONDITIONS AND TRENDS
     * <li>AIRPLANES
     * <li>VIOLINS
     * </ol>
     */
    protected List<String> descriptors = new ArrayList<String>();

    /**
     * The "feature page" field.
     */
    protected String featurePage;

    /**
     * The "general online descriptors" field specifies a list of descriptors
     * that are at a higher level of generality than the other tags associated
     * with the article. These tags are algorithmically assigned and manually
     * verified by nytimes.com production staff.
     * <p>
     * Examples Include:
     * <ol>
     * <li>Surfing
     * <li>Venice Biennale
     * <li>Ranches
     * </ol>
     */
    protected List<String> generalOnlineDescriptors = new ArrayList<String>();

    /**
     * The GUID field specifies a an integer that is guaranteed to be unique for
     * every document in the corpus.
     */
    protected int guid;

    /**
     * This field specifies the headline of the article as it appeared in the
     * print edition of the New York Times.
     */
    protected String headline;

    /**
     * The kicker is an additional piece of information printed as an
     * accompaniment to a news headline.
     */
    protected String kicker;

    /**
     * The "lead Paragraph" field is the lead paragraph of the article.
     * Generally this field is populated with the first two paragraphs from the
     * article.
     */
    protected String leadParagraph;

    /**
     * The "locations" field specifies a list of geographic descriptors drawn
     * from a normalized controlled vocabulary that correspond to places
     * mentioned in the article. These tags are hand-assigned by a team of
     * library scientists working for the New York Times Indexing service.
     * <p>
     * Examples Include:
     * <ol>
     * <li>Wellsboro (Pa)
     * <li>Kansas City (Kan)
     * <li>Park Slope (NYC)
     * </ol>
     */
    protected List<String> locations = new ArrayList<String>();

    /**
     * The "names" field specifies a list of names mentioned in the article.
     * These tags are hand-assigned by a team of library scientists working for
     * the New York Times Indexing service.
     * <p>
     * Examples Include:
     * <ol>
     * <li>Azza Fahmy
     * <li>George C. Izenour
     * <li>Chris Schenkel
     * </ol>
     */
    protected List<String> names = new ArrayList<String>();

    /**
     * This field specifies the desk in the New York Times newsroom that
     * produced the article. The desk is related to, but is not the same as the
     * section in which the article appears.
     */
    protected String newsDesk;

    /**
     * The Normalized Byline field is the byline normalized to the form (last
     * name, first name).
     */
    protected String normalizedByline;

    /**
     * This field specifies a list of descriptors from a normalized controlled
     * vocabulary that correspond to topics mentioned in the article. These tags
     * are algorithmically assigned and manually verified by nytimes.com
     * production staff.
     * <p>
     * Examples Include:
     * <ol>
     * <li>Marriages
     * <li>Parks and Other Recreation Areas
     * <li>Cooking and Cookbooks
     * </ol>
     */
    protected List<String> onlineDescriptors = new ArrayList<String>();

    /**
     * This field specifies the headline displayed with the article on
     * nytimes.com. Often this differs from the headline used in print.
     */
    protected String onlineHeadline;

    /**
     * This field specifies the lead paragraph as defined by the producers at
     * nytimes.com.
     */
    protected String onlineLeadParagraph;

    /**
     * This field specifies a list of place names that correspond to geographic
     * locations mentioned in the article. These tags are algorithmically
     * assigned and manually verified by nytimes.com production staff.
     * <p>
     * Examples Include:
     * <ol>
     * <li>Hollywood
     * <li>Los Angeles
     * <li>Arcadia
     * </ol>
     */
    protected List<String> onlineLocations = new ArrayList<String>();

    /**
     * This field specifies a list of organizations that correspond to
     * organizations mentioned in the article. These tags are algorithmically
     * assigned and manually verified by nytimes.com production staff.
     * <p>
     * Examples Include:
     * <ol>
     * <li>Nintendo Company Limited
     * <li>Yeshiva University
     * <li>Rose Center
     * </ol>
     */
    protected List<String> onlineOrganizations = new ArrayList<String>();

    /**
     * This field specifies a list of people that correspond to individuals
     * mentioned in the article. These tags are algorithmically assigned and
     * manually verified by nytimes.com production staff.
     * <p>
     * Examples Include:
     * <ol>
     * <li>Lopez, Jennifer
     * <li>Joyce, James
     * <li>Robinson, Jackie
     * </ol>
     */
    protected List<String> onlinePeople = new ArrayList<String>();

    /**
     * This field specifies the section(s) on nytimes.com in which the article
     * is placed. If the article is placed in multiple sections, this field will
     * be specified as a ";" delineated list.
     */
    protected String onlineSection;

    /**
     * This field specifies a list of authored works mentioned in the article.
     * These tags are algorithmically assigned and manually verified by
     * nytimes.com production staff.
     * <p>
     * Examples Include:
     * <ol>
     * <li>Matchstick Men (Movie)
     * <li>Blades of Glory (Movie)
     * <li>Bridge &amp; Tunnel (Play)
     * </ol>
     */
    protected List<String> onlineTitles = new ArrayList<String>();

    /**
     * This field specifies a list of organization names drawn from a normalized
     * controlled vocabulary that correspond to organizations mentioned in the
     * article. These tags are hand-assigned by a team of library scientists
     * working in the New York Times Indexing service.
     * <p>
     * Examples Include:
     * <ol>
     * <li>Circuit City Stores Inc
     * <li>Delaware County Community College (Pa)
     * <li>CONNECTICUT GRAND OPERA
     * </ol>
     */
    protected List<String> organizations = new ArrayList<String>();

    /**
     * This field specifies the page of the section in the paper in which the
     * article appears. This is not an absolute pagination. An article that
     * appears on page 3 in section A occurs in the physical paper before an
     * article that occurs on page 1 of section F.
     */
    protected Integer page;

    /**
     * This field specifies a list of people from a normalized controlled
     * vocabulary that correspond to individuals mentioned in the article. These
     * tags are hand-assigned by a team of library scientists working in the New
     * York Times Indexing service.
     * <p>
     * Examples Include:
     * <ol>
     * <li>REAGAN, RONALD WILSON (PRES)
     * <li>BEGIN, MENACHEM (PRIME MIN)
     * <li>COLLINS, GLENN
     * </ol>
     */
    protected List<String> people = new ArrayList<String>();

    /**
     * This field specifies the date of the article�s publication.
     */
    protected Date publicationDate;

    /**
     * This field specifies the day of the month on which the article was
     * published, always in the range 1-31.
     */
    protected Integer publicationDayOfMonth;

    /**
     * This field specifies the month on which the article was published in the
     * range 1-12 where 1 is January 2 is February etc.
     */
    protected Integer publicationMonth;

    /**
     * This field specifies the year in which the article was published. This
     * value is in the range 1987-2007 for this collection.
     */
    protected Integer publicationYear;

    /**
     * This field specifies the section of the paper in which the article
     * appears. This is not the name of the section, but rather a letter or
     * number that indicates the section.
     */
    protected String section;

    /**
     * If the article is part of a regular series, this field specifies the name
     * of that column.
     */
    protected String seriesName;

    /**
     * The slug is a short string that uniquely identifies an article from all
     * other articles published on the same day. Please note, however, that
     * different articles on different days may have the same slug.
     * <ul>
     * <li>30other <li>12reunion
     * </ul>
     */
    protected String slug;

    /** The file from which this object was read. */
    protected File sourceFile;

    /**
     * This field specifies a list of taxonomic classifiers that place this
     * article into a hierarchy of articles. The individual terms of each
     * taxonomic classifier are separated with the '/' character. These tags are
     * algorithmically assigned and manually verified by nytimes.com production
     * staff. These tags are algorithmically assigned and manually verified by
     * nytimes.com production staff.
     * <p>
     * Examples Include:
     * <ol>
     * <li>Top/Features/Travel/Guides/Destinations/North America/United
     * States/Arizona
     * <li>Top/News/U.S./Rockies
     * <li>Top/Opinion
     * </ol>
     */
    protected List<String> taxonomicClassifiers = new ArrayList<String>();

    /**
     * This field specifies a list of authored works that correspond to works
     * mentioned in the article. These tags are hand-assigned by a team of
     * library scientists working in the New York Times Indexing service.
     * <p>
     * Examples Include:
     * <ol>
     * <li>Greystoke: The Legend of Tarzan, Lord of the Apes (Movie)
     * <li>Law &amp; Order (TV Program)
     * <li>BATTLEFIELD EARTH (BOOK)
     * </ol>
     */
    protected List<String> titles = new ArrayList<String>();

    /**
     * This field specifies a normalized list of terms describing the general
     * editorial category of the article. These tags are algorithmically
     * assigned and manually verified by nytimes.com production staff.
     * <p>
     * Examples Include:
     * <ol>
     * <li>REVIEW
     * <li>OBITUARY
     * <li>ANALYSIS
     * </ol>
     */
    protected List<String> typesOfMaterial = new ArrayList<String>();

    /**
     * This field specifies the location on nytimes.com of the article. The
     * �Alternative Url� field is preferred to this field on articles published
     * on or after April 02, 2006, as the linked page will have richer content.
     */
    protected URL url;

    /**
     * This field specifies the number of words in the body of the article,
     * including the lead paragraph.
     */
    protected Integer wordCount;

    /**
     * Accessor for the alternateURL property.
     *
     * @return the alternateURL
     */
    public URL getAlternateURL() {
      return alternateURL;
    }

    /**
     * Accessor for the articleAbstract property.
     *
     * @return the articleAbstract
     */
    public String getArticleAbstract() {
      return articleAbstract;
    }

    /**
     * Accessor for the authorBiography property.
     *
     * @return the authorBiography
     */
    public String getAuthorBiography() {
      return authorBiography;
    }

    /**
     * Accessor for the banner property.
     *
     * @return the banner
     */
    public String getBanner() {
      return banner;
    }

    /**
     * Accessor for the biographicalCategories property.
     *
     * @return the biographicalCategories
     */
    public List<String> getBiographicalCategories() {
      return biographicalCategories;
    }

    /**
     * Accessor for the body property.
     *
     * @return the body
     */
    public String getBody() {
      return body;
    }

    /**
     * Accessor for the byline property.
     *
     * @return the byline
     */
    public String getByline() {
      return byline;
    }

    /**
     * Accessor for the columnName property.
     *
     * @return the columnName
     */
    public String getColumnName() {
      return columnName;
    }

    /**
     * Accessor for the columnNumber property.
     *
     * @return the columnNumber
     */
    public Integer getColumnNumber() {
      return columnNumber;
    }

    /**
     * Accessor for the correctionDate property.
     *
     * @return the correctionDate
     */
    public Date getCorrectionDate() {
      return correctionDate;
    }

    /**
     * Accessor for the correctionText property.
     *
     * @return the correctionText
     */
    public String getCorrectionText() {
      return correctionText;
    }

    /**
     * Accessor for the credit property.
     *
     * @return the credit
     */
    public String getCredit() {
      return credit;
    }

    /**
     * Accessor for the dateline property.
     *
     * @return the dateline
     */
    public String getDateline() {
      return dateline;
    }

    /**
     * Accessor for the dayOfWeek property.
     *
     * @return the dayOfWeek
     */
    public String getDayOfWeek() {
      return dayOfWeek;
    }

    /**
     * Accessor for the descriptors property.
     *
     * @return the descriptors
     */
    public List<String> getDescriptors() {
      return descriptors;
    }

    /**
     * Accessor for the featurePage property.
     *
     * @return the featurePage
     */
    public String getFeaturePage() {
      return featurePage;
    }

    /**
     * Accessor for the generalOnlineDescriptors property.
     *
     * @return the generalOnlineDescriptors
     */
    public List<String> getGeneralOnlineDescriptors() {
      return generalOnlineDescriptors;
    }

    /**
     * Accessor for the guid property.
     *
     * @return the guid
     */
    public int getGuid() {
      return guid;
    }

    /**
     * Accessor for the headline property.
     *
     * @return the headline
     */
    public String getHeadline() {
      return headline;
    }

    /**
     * Accessor for the kicker property.
     *
     * @return the kicker
     */
    public String getKicker() {
      return kicker;
    }

    /**
     * Accessor for the leadParagraph property.
     *
     * @return the leadParagraph
     */
    public String getLeadParagraph() {
      return leadParagraph;
    }

    /**
     * Accessor for the locations property.
     *
     * @return the locations
     */
    public List<String> getLocations() {
      return locations;
    }

    /**
     * Accessor for the names property.
     *
     * @return the names
     */
    public List<String> getNames() {
      return names;
    }

    /**
     * Accessor for the newsDesk property.
     *
     * @return the newsDesk
     */
    public String getNewsDesk() {
      return newsDesk;
    }

    /**
     * Accessor for the normalizedByline property.
     *
     * @return the normalizedByline
     */
    public String getNormalizedByline() {
      return normalizedByline;
    }

    /**
     * Accessor for the onlineDescriptors property.
     *
     * @return the onlineDescriptors
     */
    public List<String> getOnlineDescriptors() {
      return onlineDescriptors;
    }

    /**
     * Accessor for the onlineHeadline property.
     *
     * @return the onlineHeadline
     */
    public String getOnlineHeadline() {
      return onlineHeadline;
    }

    /**
     * Accessor for the onlineLeadParagraph property.
     *
     * @return the onlineLeadParagraph
     */
    public String getOnlineLeadParagraph() {
      return onlineLeadParagraph;
    }

    /**
     * Accessor for the onlineLocations property.
     *
     * @return the onlineLocations
     */
    public List<String> getOnlineLocations() {
      return onlineLocations;
    }

    /**
     * Accessor for the onlineOrganizations property.
     *
     * @return the onlineOrganizations
     */
    public List<String> getOnlineOrganizations() {
      return onlineOrganizations;
    }

    /**
     * Accessor for the onlinePeople property.
     *
     * @return the onlinePeople
     */
    public List<String> getOnlinePeople() {
      return onlinePeople;
    }

    /**
     * Accessor for the onlineSection property.
     *
     * @return the onlineSection
     */
    public String getOnlineSection() {
      return onlineSection;
    }

    /**
     * Accessor for the onlineTitles property.
     *
     * @return the onlineTitles
     */
    public List<String> getOnlineTitles() {
      return onlineTitles;
    }

    /**
     * Accessor for the organizations property.
     *
     * @return the organizations
     */
    public List<String> getOrganizations() {
      return organizations;
    }

    /**
     * Accessor for the page property.
     *
     * @return the page
     */
    public Integer getPage() {
      return page;
    }

    /**
     * Accessor for the people property.
     *
     * @return the people
     */
    public List<String> getPeople() {
      return people;
    }

    /**
     * Accessor for the publicationDate property.
     *
     * @return the publicationDate
     */
    public Date getPublicationDate() {
      return publicationDate;
    }

    /**
     * Accessor for the publicationDayOfMonth property.
     *
     * @return the publicationDayOfMonth
     */
    public Integer getPublicationDayOfMonth() {
      return publicationDayOfMonth;
    }

    /**
     * Accessor for the publicationMonth property.
     *
     * @return the publicationMonth
     */
    public Integer getPublicationMonth() {
      return publicationMonth;
    }

    /**
     * Accessor for the publicationYear property.
     *
     * @return the publicationYear
     */
    public Integer getPublicationYear() {
      return publicationYear;
    }

    /**
     * Accessor for the section property.
     *
     * @return the section
     */
    public String getSection() {
      return section;
    }

    /**
     * Accessor for the seriesName property.
     *
     * @return the seriesName
     */
    public String getSeriesName() {
      return seriesName;
    }

    /**
     * Accessor for the slug property.
     *
     * @return the slug
     */
    public String getSlug() {
      return slug;
    }

    /**
     * Accessor for the sourceFile property.
     *
     * @return the sourceFile
     */
    public File getSourceFile() {
      return sourceFile;
    }

    /**
     * Accessor for the taxonomicClassifiers property.
     *
     * @return the taxonomicClassifiers
     */
    public List<String> getTaxonomicClassifiers() {
      return taxonomicClassifiers;
    }

    /**
     * Accessor for the titles property.
     *
     * @return the titles
     */
    public List<String> getTitles() {
      return titles;
    }

    /**
     * Accessor for the typesOfMaterial property.
     *
     * @return the typesOfMaterial
     */
    public List<String> getTypesOfMaterial() {
      return typesOfMaterial;
    }

    /**
     * Accessor for the url property.
     *
     * @return the url
     */
    public URL getUrl() {
      return url;
    }

    /**
     * Accessor for the wordCount property.
     *
     * @return the wordCount
     */
    public Integer getWordCount() {
      return wordCount;
    }

    /**
     * Left justify a string by forcing it to be the specified length. This is
     * done by concatonating space characters to the end of the string until the
     * string is of the specified length. If, however, the string is initially
     * longer than the specified length then the original string is returned.
     *
     * @param s a string
     * @param length the target length for the string.
     * @return a left-justified string
     */
    private String ljust(String s, Integer length) {
      if (s.length() >= length) {
        return s;
      }
      length -= s.length();
      StringBuffer sb = new StringBuffer();
      for (Integer i = 0; i < length; i++) {
        sb.append(" ");
      }
      return s + sb.toString();
    }

    /**
     * Setter for the alternateURL property.
     *
     * @param alternateURL the alternativeURL to set
     */
    public void setAlternateURL(URL alternateURL) {
      this.alternateURL = alternateURL;
    }

    /**
     * Setter for the articleAbstract property.
     *
     * @param articleAbstract the articleAbstract to set
     */
    public void setArticleAbstract(String articleAbstract) {
      this.articleAbstract = articleAbstract;
    }

    /**
     * Setter for the authorBiography property.
     *
     * @param authorBiography the authorBiography to set
     */
    public void setAuthorBiography(String authorBiography) {
      this.authorBiography = authorBiography;
    }

    /**
     * Setter for the banner property.
     *
     * @param banner the banner to set
     */
    public void setBanner(String banner) {
      this.banner = banner;
    }

    /**
     * Setter for the biographicalCategories property.
     *
     * @param biographicalCategories the biographicalCategories to set
     */
    public void setBiographicalCategories(List<String> biographicalCategories) {
      this.biographicalCategories = biographicalCategories;
    }

    /**
     * Setter for the body property.
     *
     * @param body the body to set
     */
    public void setBody(String body) {
      this.body = body;
    }

    /**
     * Setter for the byline property.
     *
     * @param byline the byline to set
     */
    public void setByline(String byline) {
      this.byline = byline;
    }

    /**
     * Setter for the columnName property.
     *
     * @param columnName the columnName to set
     */
    public void setColumnName(String columnName) {
      this.columnName = columnName;
    }

    /**
     * Setter for the columnNumber property.
     *
     * @param columnNumber the columnNumber to set
     */
    public void setColumnNumber(Integer columnNumber) {
      this.columnNumber = columnNumber;
    }

    /**
     * Setter for the correctionDate property.
     *
     * @param correctionDate the correctionDate to set
     */
    public void setCorrectionDate(Date correctionDate) {
      this.correctionDate = correctionDate;
    }

    /**
     * Setter for the correctionText property.
     *
     * @param correctionText the correctionText to set
     */
    public void setCorrectionText(String correctionText) {
      this.correctionText = correctionText;
    }

    /**
     * Setter for the credit property.
     *
     * @param credit the credit to set
     */
    public void setCredit(String credit) {
      this.credit = credit;
    }

    /**
     * Setter for the dateline property.
     *
     * @param dateline the dateline to set
     */
    public void setDateline(String dateline) {
      this.dateline = dateline;
    }

    /**
     * Setter for the dayOfWeek property.
     *
     * @param dayOfWeek the dayOfWeek to set
     */
    public void setDayOfWeek(String dayOfWeek) {
      this.dayOfWeek = dayOfWeek;
    }

    /**
     * Setter for the descriptors property.
     *
     * @param descriptors the descriptors to set
     */
    public void setDescriptors(List<String> descriptors) {
      this.descriptors = descriptors;
    }

    /**
     * Setter for the featurePage property.
     *
     * @param featurePage the featurePage to set
     */
    public void setFeaturePage(String featurePage) {
      this.featurePage = featurePage;
    }

    /**
     * Setter for the generalOnlineDescriptors property.
     *
     * @param generalOnlineDescriptors the generalOnlineDescriptors to set
     */
    public void setGeneralOnlineDescriptors(List<String> generalOnlineDescriptors) {
      this.generalOnlineDescriptors = generalOnlineDescriptors;
    }

    /**
     * Setter for the guid property.
     *
     * @param guid the guid to set
     */
    public void setGuid(int guid) {
      this.guid = guid;
    }

    /**
     * Setter for the headline property.
     *
     * @param headline the headline to set
     */
    public void setHeadline(String headline) {
      this.headline = headline;
    }

    /**
     * Setter for the kicker property.
     *
     * @param kicker the kicker to set
     */
    public void setKicker(String kicker) {
      this.kicker = kicker;
    }

    /**
     * Setter for the leadParagraph property.
     *
     * @param leadParagraph the leadParagraph to set
     */
    public void setLeadParagraph(String leadParagraph) {
      this.leadParagraph = leadParagraph;
    }

    /**
     * Setter for the locations property.
     *
     * @param locations the locations to set
     */
    public void setLocations(List<String> locations) {
      this.locations = locations;
    }

    /**
     * Setter for the names property.
     *
     * @param names the names to set
     */
    public void setNames(List<String> names) {
      this.names = names;
    }

    /**
     * Setter for the newsDesk property.
     *
     * @param newsDesk the newsDesk to set
     */
    public void setNewsDesk(String newsDesk) {
      this.newsDesk = newsDesk;
    }

    /**
     * Setter for the normalizedByline property.
     *
     * @param normalizedByline the normalizedByline to set
     */
    public void setNormalizedByline(String normalizedByline) {
      this.normalizedByline = normalizedByline;
    }

    /**
     * Setter for the onlineDescriptors property.
     *
     * @param onlineDescriptors the onlineDescriptors to set
     */
    public void setOnlineDescriptors(List<String> onlineDescriptors) {
      this.onlineDescriptors = onlineDescriptors;
    }

    /**
     * Setter for the onlineHeadline property.
     *
     * @param onlineHeadline the onlineHeadline to set
     */
    public void setOnlineHeadline(String onlineHeadline) {
      this.onlineHeadline = onlineHeadline;
    }

    /**
     * Setter for the onlineLeadParagraph property.
     *
     * @param onlineLeadParagraph the onlineLeadParagraph to set
     */
    public void setOnlineLeadParagraph(String onlineLeadParagraph) {
      this.onlineLeadParagraph = onlineLeadParagraph;
    }

    /**
     * Setter for the onlineLocations property.
     *
     * @param onlineLocations the onlineLocations to set
     */
    public void setOnlineLocations(List<String> onlineLocations) {
      this.onlineLocations = onlineLocations;
    }

    /**
     * Setter for the onlineOrganizations property.
     *
     * @param onlineOrganizations the onlineOrganizations to set
     */
    public void setOnlineOrganizations(List<String> onlineOrganizations) {
      this.onlineOrganizations = onlineOrganizations;
    }

    /**
     * Setter for the onlinePeople property.
     *
     * @param onlinePeople the onlinePeople to set
     */
    public void setOnlinePeople(List<String> onlinePeople) {
      this.onlinePeople = onlinePeople;
    }

    /**
     * Setter for the onlineSection property.
     *
     * @param onlineSection the onlineSection to set
     */
    public void setOnlineSection(String onlineSection) {
      this.onlineSection = onlineSection;
    }

    /**
     * Setter for the onlineTitles property.
     *
     * @param onlineTitles the onlineTitles to set
     */
    public void setOnlineTitles(List<String> onlineTitles) {
      this.onlineTitles = onlineTitles;
    }

    /**
     * Setter for the organizations property.
     *
     * @param organizations the organizations to set
     */
    public void setOrganizations(List<String> organizations) {
      this.organizations = organizations;
    }

    /**
     * Setter for the page property.
     *
     * @param page the page to set
     */
    public void setPage(Integer page) {
      this.page = page;
    }

    /**
     * Setter for the people property.
     *
     * @param people the people to set
     */
    public void setPeople(List<String> people) {
      this.people = people;
    }

    /**
     * Setter for the publicationDate property.
     *
     * @param publicationDate the publicationDate to set
     */
    public void setPublicationDate(Date publicationDate) {
      this.publicationDate = publicationDate;
    }

    /**
     * Setter for the publicationDayOfMonth property.
     *
     * @param publicationDayOfMonth the publicationDayOfMonth to set
     */
    public void setPublicationDayOfMonth(Integer publicationDayOfMonth) {
      this.publicationDayOfMonth = publicationDayOfMonth;
    }

    /**
     * Setter for the publicationMonth property.
     *
     * @param publicationMonth the publicationMonth to set
     */
    public void setPublicationMonth(Integer publicationMonth) {
      this.publicationMonth = publicationMonth;
    }

    /**
     * Setter for the publicationYear property.
     *
     * @param publicationYear the publicationYear to set
     */
    public void setPublicationYear(Integer publicationYear) {
      this.publicationYear = publicationYear;
    }

    /**
     * Setter for the section property.
     *
     * @param section the section to set
     */
    public void setSection(String section) {
      this.section = section;
    }

    /**
     * Setter for the seriesName property.
     *
     * @param seriesName the seriesName to set
     */
    public void setSeriesName(String seriesName) {
      this.seriesName = seriesName;
    }

    /**
     * Setter for the slug property.
     *
     * @param slug the slug to set
     */
    public void setSlug(String slug) {
      this.slug = slug;
    }

    /**
     * Setter for the sourceFile property.
     *
     * @param sourceFile the sourceFile to set
     */
    public void setSourceFile(File sourceFile) {
      this.sourceFile = sourceFile;
    }

    /**
     * Setter for the taxonomicClassifiers property.
     *
     * @param taxonomicClassifiers the taxonomicClassifiers to set
     */
    public void setTaxonomicClassifiers(List<String> taxonomicClassifiers) {
      this.taxonomicClassifiers = taxonomicClassifiers;
    }

    /**
     * Setter for the titles property.
     *
     * @param titles the titles to set
     */
    public void setTitles(List<String> titles) {
      this.titles = titles;
    }

    /**
     * Setter for the typesOfMaterial property.
     *
     * @param typesOfMaterial the typesOfMaterial to set
     */
    public void setTypesOfMaterial(List<String> typesOfMaterial) {
      this.typesOfMaterial = typesOfMaterial;
    }

    /**
     * Setter for the url property.
     *
     * @param url the url to set
     */
    public void setUrl(URL url) {
      this.url = url;
    }

    /**
     * Setter for the wordCount property.
     *
     * @param wordCount the wordCount to set
     */
    public void setWordCount(Integer wordCount) {
      this.wordCount = wordCount;
    }

    @Override
    public String toString() {
      StringBuffer sb = new StringBuffer();
      appendProperty(sb, "alternativeURL", alternateURL);
      appendProperty(sb, "articleAbstract", articleAbstract);
      appendProperty(sb, "authorBiography", authorBiography);
      appendProperty(sb, "banner", banner);
      appendProperty(sb, "biographicalCategories", biographicalCategories);
      appendProperty(sb, "body", body);
      appendProperty(sb, "byline", byline);
      appendProperty(sb, "columnName", columnName);
      appendProperty(sb, "columnNumber", columnNumber);
      appendProperty(sb, "correctionDate", correctionDate);
      appendProperty(sb, "correctionText", correctionText);
      appendProperty(sb, "credit", credit);
      appendProperty(sb, "dateline", dateline);
      appendProperty(sb, "dayOfWeek", dayOfWeek);
      appendProperty(sb, "descriptors", descriptors);
      appendProperty(sb, "featurePage", featurePage);
      appendProperty(sb, "generalOnlineDescriptors", generalOnlineDescriptors);
      appendProperty(sb, "guid", guid);
      appendProperty(sb, "headline", headline);
      appendProperty(sb, "kicker", kicker);
      appendProperty(sb, "leadParagraph", leadParagraph);
      appendProperty(sb, "locations", locations);
      appendProperty(sb, "names", names);
      appendProperty(sb, "newsDesk", newsDesk);
      appendProperty(sb, "normalizedByline", normalizedByline);
      appendProperty(sb, "onlineDescriptors", onlineDescriptors);
      appendProperty(sb, "onlineHeadline", onlineHeadline);
      appendProperty(sb, "onlineLeadParagraph", onlineLeadParagraph);
      appendProperty(sb, "onlineLocations", onlineLocations);
      appendProperty(sb, "onlineOrganizations", onlineOrganizations);
      appendProperty(sb, "onlinePeople", onlinePeople);
      appendProperty(sb, "onlineSection", onlineSection);
      appendProperty(sb, "onlineTitles", onlineTitles);
      appendProperty(sb, "organizations", organizations);
      appendProperty(sb, "page", page);
      appendProperty(sb, "people", people);
      appendProperty(sb, "publicationDate", publicationDate);
      appendProperty(sb, "publicationDayOfMonth", publicationDayOfMonth);
      appendProperty(sb, "publicationMonth", publicationMonth);
      appendProperty(sb, "publicationYear", publicationYear);
      appendProperty(sb, "section", section);
      appendProperty(sb, "seriesName", seriesName);
      appendProperty(sb, "slug", slug);
      appendProperty(sb, "sourceFile", sourceFile);
      appendProperty(sb, "taxonomicClassifiers", taxonomicClassifiers);
      appendProperty(sb, "titles", titles);
      appendProperty(sb, "typesOfMaterial", typesOfMaterial);
      appendProperty(sb, "url", url);
      appendProperty(sb, "wordCount", wordCount);
      return sb.toString();
    }

    /**
     * Append a property to the specified string.
     *
     * @param sb
     * @param propertyName
     * @param propertyValue
     */
    private void appendProperty(StringBuffer sb, String propertyName, Object propertyValue) {
      if (propertyValue != null) {
        propertyValue = propertyValue.toString().replaceAll("\\s+", " ").trim();
      }
      sb.append(ljust(propertyName + ":", 45) + propertyValue + "\n");
    }
  }

  /**
   * Parser for a document from <a href="https://catalog.ldc.upenn.edu/products/LDC2008T19">New York Times Annotated Corpus</a>.
   * This was originally distributed as part of the corpus as a class called {@code NYTCorpusDocumentParser}.
   *
   * @author Evan Sandhaus
   */
  public static class Parser {
    /** NITF Constant */
    private static final String CORRECTION_TEXT = "correction_text";

    /** NITF Constant */
    private static final String SERIES_NAME_TAG = "series.name";

    /** NITF Constant */
    private final DateFormat format = new SimpleDateFormat("yyyyMMdd'T'HHmmss");

    /** NITF Constant */
    private static final String TAGLINE_TAG = "tagline";

    /** NITF Constant */
    private static final String CLASS_ATTRIBUTE = "class";

    /** NITF Constant */
    private static final String CLASSIFIER_TAG = "classifier";

    /** NITF Constant */
    private static final String HL2_TAG = "hl2";

    /** NITF Constant */
    private static final String BLOCK_TAG = "block";

    /** NITF Constant */
    private static final String ABSTRACT_TAG = "abstract";

    /** NITF Constant */
    private static final String DATELINE_TAG = "dateline";

    /** NITF Constant */
    private static final String BYLINE_TAG = "byline";

    /** NITF Constant */
    private static final String HEDLINE_TAG = "hedline";

    /** NITF Constant */
    private static final String BODY_END_TAG = "body.end";

    /** NITF Constant */
    private static final String BODY_CONTENT_TAG = "body.content";

    /** NITF Constant */
    private static final String BODY_HEAD_TAG = "body.head";

    /** NITF Constant */
    private static final String TYPE_ATTRIBUTE = "type";

    /** NITF Constant */
    private static final String NAME_ATTRIBUTE = "name";

    /** NITF Constant */
    private static final String ITEM_LENGTH_ATTRIBUTE = "item-length";

    /** NITF Constant */
    private static final String EX_REF_ATTRIBUTE = "ex-ref";

    /** NITF Constant */
    private static final String SLUG_ATTRIBUTE = "slug";

    /** NITF Constant */
    private static final String PRINT_SECTION_ATTRIBUTE = "print_section";

    /** NITF Constant */
    private static final String PRINT_PAGE_NUMBER_ATTRIBUTE = "print_page_number";

    /** NITF Constant */
    private static final String DSK_ATTRIBUTE = "dsk";

    /** NITF Constant */
    private static final String HL1_TAG = "hl1";

    /** NITF Constant */
    private static final String CONTENT_ATTRIBUTE = "content";

    /** NITF Constant */
    private static final String DOC_ID_TAG = "doc-id";

    /** NITF Constant */
    private static final String IDENTIFIED_CONTENT_TAG = "identified-content";

    /** NITF Constant */
    private static final String ID_STRING_ATTRIBUTE = "id-string";

    /** NITF Constant */
    private static final String LOCATION_TAG = "location";

    /** NITF Constant */
    private static final String OBJECT_TITLE_TAG = "object.title";

    /** NITF Constant */
    private static final String PERSON_TAG = "person";

    /** NITF Constant */
    private static final String PUBDATA_TAG = "pubdata";

    /** NITF Constant */
    private static final String DOCDATA_TAG = "docdata";

    /** NITF Constant */
    private static final String META_TAG = "meta";

    /** NITF Constant */
    private static final String BODY_TAG = "body";

    /** NITF Constant */
    private static final String HEAD_TAG = "head";

    /** NITF Constant */
    private static final String NITF_TAG = "nitf";

    /** NITF Constant */
    private static final String ALTERNATE_URL_ATTRIBUTE = "alternate_url";

    /** NITF Constant */
    private static final String AUTHOR_INFO_ATTRIBUTE = "author_info";

    /** NITF Constant */
    private static final String DESCRIPTOR_ATTRIBUTE = "descriptor";

    /** NITF Constant */
    private static final String FULL_TEXT_ATTRIBUTE = "full_text";

    /** NITF Constant */
    private static final String INDEXING_SERVICE_ATTRIBUTE = "indexing_service";

    /** NITF Constant */
    private static final String LEAD_PARAGRAPH_ATTRIBUTE = "lead_paragraph";

    /** NITF Constant */
    private static final String NORMALIZED_BYLINE_ATTRIBUTE = "normalized_byline";

    /** NITF Constant */
    private static final String ONLINE_HEADLINE_ATTRIBUTE = "online_headline";

    /** NITF Constant */
    private static final String ONLINE_LEAD_PARAGRAPH_ATTRIBUTE = "online_lead_paragraph";

    /** NITF Constant */
    private static final String ONLINE_PRODUCER_ATTRIBUTE = "online_producer";

    /** NITF Constant */
    private static final String ONLINE_SECTIONS_ATTRIBUTE = "online_sections";

    /** NITF Constant */
    private static final String ORGANIZATION_TAG = "org";

    /** NITF Constant */
    private static final String P_TAG = "p";

    /** NITF Constant */
    private static final String PRINT_BYLINE_ATTRIBUTE = "print_byline";

    /** NITF Constant */
    private static final String PRINT_COLUMN_ATTRIBUTE = "print_column";

    /** NITF Constant */
    private static final String PUBLICATION_DAY_OF_MONTH_ATTRIBUTE = "publication_day_of_month";

    /** NITF Constant */
    private static final String PUBLICATION_MONTH_ATTRIBUTE = "publication_month";

    /** NITF Constant */
    private static final String PUBLICATION_YEAR_ATTRIBUTE = "publication_year";

    /** NITF Constant */
    private static final String PULICATION_DAY_OF_WEEK_ATTRIBUTE = "publication_day_of_week";

    /** NITF Constant */
    private static final String SERIES_NAME_ATTRIBUTE = "series_name";

    /** NITF Constant */
    private static final String SERIES_TAG = "series";

    /** NITF Constant */
    private static final String TAXONOMIC_CLASSIFIER_ATTRIBUTE = "taxonomic_classifier";

    /** NITF Constant */
    private static final String BANNER_ATTRIBUTE = "banner";

    /** NITF Constant */
    private static final String CORRECTION_DATE_ATTRIBUTE = "correction_date";

    /** NITF Constant */
    private static final String FEATURE_PAGE_ATTRIBUTE = "feature_page";

    /** NITF Constant */
    private static final String COLUMN_NAME_ATTRIBUTE = "column_name";

    /** NITF Constant */
    private static final String TYPES_OF_MATERIAL_ATTRIBUTE = "types_of_material";

    /** NITF Constant */
    private static final String NAMES_ATTRIBUTE = "names";

    /** NITF Constant */
    private static final String BIOGRAPHICAL_CATEGORIES_ATTRIBUTE = "biographical_categories";

    /** NITF Constant */
    public static final String DATE_PUBLICATION_ATTRIBUTE = "date.publication";

    /** NITF Constant */
    private static final String GENERAL_DESCRIPTOR_ATTRIBUTE = "general_descriptor";

    public Document parseFile(BufferedReader bRdr, File fileName) throws IOException {
      RawDocument raw = parseNYTCorpusDocumentFromBufferedReader(bRdr, fileName);

      Document d = new Document(raw);
      d.id = String.valueOf(raw.getGuid());
      d.contents = Stream.of(raw.getHeadline(), raw.getArticleAbstract(), raw.getBody())
        .filter(text -> text != null)
        .collect(Collectors.joining("\n"));

      return d;
    }

    /**
     * Parse an New York Times Document from a file.
     *
     * @param file the file from which to parse the document
     * @param validating true if the file is to be validated against the NITF DTD and false if it
     *   is not. It is recommended that validation be disabled, as all documents in the corpus have
     *   previously been validated against the NITF DTD.
     * @return the parsed document, or null if an error occurs
     */
    public RawDocument parseNYTCorpusDocumentFromFile(File file, boolean validating) {
      org.w3c.dom.Document document = null;
      if (validating) {
        document = loadValidating(file);
      } else {
        document = loadNonValidating(file);
      }
      return parseNYTCorpusDocumentFromDOMDocument(file, document);
    }

    /**
     * Parse a New York Time Document from BufferedReader. The parameter `file` is
     * used only to feed in other methods
     *
     * @param file the file from which to parse the document
     * @param bRdr the BufferedReader of file
     * @return the parsed document, or null if an error occurs
     */
    public RawDocument parseNYTCorpusDocumentFromBufferedReader(BufferedReader bRdr, File file) {
      org.w3c.dom.Document document = loadFromBufferedReader(bRdr, file);

      return parseNYTCorpusDocumentFromDOMDocument(file, document);
    }

    public RawDocument parseNYTCorpusDocumentFromDOMDocument(File file, org.w3c.dom.Document document) {
      RawDocument ldcDocument = new RawDocument();
      ldcDocument.setSourceFile(file);
      NodeList children = document.getChildNodes();
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        String name = child.getNodeName();
        if (name.equals(NITF_TAG)) {
          handleNITFNode(child, ldcDocument);
        }
      }

      return ldcDocument;
    }

    private void handleNITFNode(Node node, RawDocument ldcDocument) {
      NodeList children = node.getChildNodes();
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        String name = child.getNodeName();
        if (name.equals(HEAD_TAG)) {
          handleHeadNode(child, ldcDocument);
        } else if (name.equals(BODY_TAG)) {
          handleBodyNode(child, ldcDocument);
        }
      }
    }

    private void handleBodyNode(Node node, RawDocument ldcDocument) {
      NodeList children = node.getChildNodes();
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        String name = child.getNodeName();
        if (name.equals(BODY_HEAD_TAG)) {
          handleBodyHead(child, ldcDocument);
        } else if (name.equals(BODY_CONTENT_TAG)) {
          handleBodyContent(child, ldcDocument);
        } else if (name.equals(BODY_END_TAG)) {
          handleBodyEnd(child, ldcDocument);
        }
      }
    }

    private void handleBodyHead(Node node, RawDocument ldcDocument) {
      NodeList children = node.getChildNodes();
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        String name = child.getNodeName();
        if (name.equals(DATELINE_TAG)) {
          handleDatelineNode(ldcDocument, child);
        } else if (name.equals(ABSTRACT_TAG)) {
          handleAbstractNode(child, ldcDocument);
        } else if (name.equals(BYLINE_TAG)) {
          handleBylineNode(child, ldcDocument);
        } else if (name.equals(HEDLINE_TAG)) {
          handleHeadlineNode(child, ldcDocument);
        }
      }
    }

    private void handleDatelineNode(RawDocument ldcDocument, Node child) {
      String datelineString = getAllText(child);
      ldcDocument.setDateline(datelineString.trim());
    }

    private void handleAbstractNode(Node node, RawDocument ldcDocument) {
      NodeList children = node.getChildNodes();
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        String name = child.getNodeName();
        if (name.equals(P_TAG)) {
          String abstractText = getAllText(child).trim();
          ldcDocument.setArticleAbstract(abstractText);
        }
      }
    }

    private void handleBylineNode(Node node, RawDocument ldcDocument) {
      String classAttribute = getAttributeValue(node, CLASS_ATTRIBUTE);
      if (classAttribute != null) {
        String text = getAllText(node).trim();
        if (classAttribute.equals(NORMALIZED_BYLINE_ATTRIBUTE)) {
          ldcDocument.setNormalizedByline(text);
        } else if (classAttribute.equals(PRINT_BYLINE_ATTRIBUTE)) {
          ldcDocument.setByline(text);
        }
      }
    }

    private void handleHeadlineNode(Node node, RawDocument ldcDocument) {
      NodeList children = node.getChildNodes();
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        String name = child.getNodeName();
        String text = getAllText(child).trim();
        if (name.equals(HL1_TAG)) {
          ldcDocument.setHeadline(text);
        } else if (name.equals(HL2_TAG)) {
          String classAttribute = getAttributeValue(child,
              CLASS_ATTRIBUTE);
          if (classAttribute != null
              && classAttribute.equals(ONLINE_HEADLINE_ATTRIBUTE)) {
            ldcDocument.setOnlineHeadline(text);
          }
        }
      }
    }

    private void handleBodyContent(Node node, RawDocument ldcDocument) {
      NodeList children = node.getChildNodes();
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        String name = child.getNodeName();
        if (name.equals(BLOCK_TAG)) {
          handleBlockNode(child, ldcDocument);
        }
      }
    }

    private void handleBlockNode(Node node, RawDocument ldcDocument) {
      String classAttribute = getAttributeValue(node, CLASS_ATTRIBUTE);
      if (classAttribute != null) {
        if (classAttribute.equals(ONLINE_LEAD_PARAGRAPH_ATTRIBUTE)) {
          ldcDocument.setOnlineLeadParagraph(parseBlock(node));
        } else if (classAttribute.equals(LEAD_PARAGRAPH_ATTRIBUTE)) {
          ldcDocument.setLeadParagraph(parseBlock(node));
        } else if (classAttribute.equals(FULL_TEXT_ATTRIBUTE)) {
          ldcDocument.setBody(parseBlock(node));
        } else if (classAttribute.equals(CORRECTION_TEXT)) {
          ldcDocument.setCorrectionText(parseBlock(node));
        }
      }
    }

    private void handleBodyEnd(Node node, RawDocument ldcDocument) {
      NodeList children = node.getChildNodes();
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        String name = child.getNodeName();
        if (name.equals(TAGLINE_TAG)) {
          String classAttribute = getAttributeValue(child,
              CLASS_ATTRIBUTE);
          if (classAttribute != null
              && classAttribute.equals(AUTHOR_INFO_ATTRIBUTE)) {
            String text = getAllText(child);
            ldcDocument.setAuthorBiography(text);
          }
        }
      }
    }

    private void handleHeadNode(Node node, RawDocument ldcDocument) {
      NodeList children = node.getChildNodes();
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        String name = child.getNodeName();
        if (name.equals(META_TAG)) {
          handleMetaNode(child, ldcDocument);
        } else if (name.equals(DOCDATA_TAG)) {
          handleDocdataNode(child, ldcDocument);
        } else if (name.equals(PUBDATA_TAG)) {
          handlePubdata(child, ldcDocument);
        }
      }
    }

    private void handleDocdataNode(Node node, RawDocument ldcDocument) {
      NodeList children = node.getChildNodes();
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        String name = child.getNodeName();
        if (name.equals(DOC_ID_TAG)) {
          handleDocumentIdNode(ldcDocument, child);
        } else if (name.equals(SERIES_TAG)) {
          ldcDocument
              .setKicker(getAttributeValue(child, SERIES_NAME_TAG));
        } else if (name.equals(IDENTIFIED_CONTENT_TAG)) {
          handleIdentifiedContent(child, ldcDocument);
        }
      }
    }

    private void handlePubdata(Node node, RawDocument ldcDocument) {
      String publicationDateString = getAttributeValue(node,
          DATE_PUBLICATION_ATTRIBUTE);
      if (publicationDateString != null) {
        try {
          Date date = format.parse(publicationDateString);
          ldcDocument.setPublicationDate(date);
        } catch (ParseException e) {
          //e.printStackTrace();
          LOG.error("Error parsing date from string "
              + publicationDateString + " in file "
              + ldcDocument.getSourceFile() + ".");
        }

      }

      String urlString = getAttributeValue(node, EX_REF_ATTRIBUTE);
      if (urlString != null) {
        try {
          URL url = new URL(urlString);
          ldcDocument.setUrl(url);
        } catch (MalformedURLException e) {
          //e.printStackTrace();
          LOG.error("Error parsing url from string " + urlString
              + " in file " + ldcDocument.getSourceFile() + ".");
        }
      }

      String wordCountString = getAttributeValue(node, ITEM_LENGTH_ATTRIBUTE);
      if (wordCountString != null) {
        try {
          Integer wordCount = Integer.parseInt(wordCountString);
          ldcDocument.setWordCount(wordCount);
        } catch (NumberFormatException e) {
          //e.printStackTrace();
          LOG.error("Error parsing integer from string "
              + wordCountString + " in file "
              + ldcDocument.getSourceFile() + ".");
        }
      }

      String creatorString = getAttributeValue(node, NAME_ATTRIBUTE);
      if (creatorString != null) {
        ldcDocument.setCredit(creatorString);
      }
    }

    private void handleIdentifiedContent(Node node, RawDocument ldcDocument) {
      NodeList children = node.getChildNodes();
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        String name = child.getNodeName();
        String value = getAllText(child).trim();
        String classAttribute = getAttributeValue(child, CLASS_ATTRIBUTE);

        if (name.equals(CLASSIFIER_TAG)) {
          String typeAttribute = getAttributeValue(child, TYPE_ATTRIBUTE);
          if (classAttribute.equals(INDEXING_SERVICE_ATTRIBUTE)) {
            if (typeAttribute.equals(DESCRIPTOR_ATTRIBUTE)) {
              ldcDocument.getDescriptors().add(value);
            } else if (typeAttribute
                .equals(BIOGRAPHICAL_CATEGORIES_ATTRIBUTE)) {
              ldcDocument.getBiographicalCategories().add(value);
            } else if (typeAttribute.equals(NAMES_ATTRIBUTE)) {
              ldcDocument.getNames().add(value);
            }
          } else if (classAttribute.equals(ONLINE_PRODUCER_ATTRIBUTE)) {
            if (typeAttribute.equals(DESCRIPTOR_ATTRIBUTE)) {
              ldcDocument.getOnlineDescriptors().add(value);
            } else if (typeAttribute
                .equals(GENERAL_DESCRIPTOR_ATTRIBUTE)) {
              ldcDocument.getGeneralOnlineDescriptors().add(value);
            } else if (typeAttribute
                .equals(TAXONOMIC_CLASSIFIER_ATTRIBUTE)) {
              ldcDocument.getTaxonomicClassifiers().add(value);
            } else if (typeAttribute
                .equals(TYPES_OF_MATERIAL_ATTRIBUTE)) {
              ldcDocument.getTypesOfMaterial().add(value);
            }
          }
        } else if (name.equals(LOCATION_TAG)) {
          if (classAttribute.equals(INDEXING_SERVICE_ATTRIBUTE)) {
            ldcDocument.getLocations().add(value);
          } else if (classAttribute.equals(ONLINE_PRODUCER_ATTRIBUTE)) {
            ldcDocument.getOnlineLocations().add(value);
          }
        } else if (name.equals(OBJECT_TITLE_TAG)) {
          if (classAttribute.equals(INDEXING_SERVICE_ATTRIBUTE)) {
            ldcDocument.getTitles().add(value);
          } else if (classAttribute.equals(ONLINE_PRODUCER_ATTRIBUTE)) {
            ldcDocument.getOnlineTitles().add(value);
          }
        } else if (name.equals(ORGANIZATION_TAG)) {
          if (classAttribute.equals(INDEXING_SERVICE_ATTRIBUTE)) {
            ldcDocument.getOrganizations().add(value);
          } else if (classAttribute.equals(ONLINE_PRODUCER_ATTRIBUTE)) {
            ldcDocument.getOnlineOrganizations().add(value);
          }
        } else if (name.equals(PERSON_TAG)) {
          if (classAttribute.equals(INDEXING_SERVICE_ATTRIBUTE)) {
            ldcDocument.getPeople().add(value);
          } else if (classAttribute.equals(ONLINE_PRODUCER_ATTRIBUTE)) {
            ldcDocument.getOnlinePeople().add(value);
          }
        }
      }
    }

    private void handleDocumentIdNode(RawDocument ldcDocument, Node child) {
      String docIdString = getAttributeValue(child, ID_STRING_ATTRIBUTE);
      if (docIdString != null) {
        try {
          ldcDocument.setGuid(Integer.parseInt(docIdString));
        } catch (NumberFormatException e) {
          //e.printStackTrace();
          LOG.error("Error parsing long from string "
              + docIdString + " in file "
              + ldcDocument.getSourceFile() + ".");
        }
      }
    }

    private void handleMetaNode(Node node, RawDocument ldcDocument) {
      NamedNodeMap attributes = node.getAttributes();

      if (attributes.getNamedItem(NAME_ATTRIBUTE) == null
          || attributes.getNamedItem(CONTENT_ATTRIBUTE) == null) {
        return;
      }

      String name = attributes.getNamedItem(NAME_ATTRIBUTE).getNodeValue();
      String content = attributes.getNamedItem(CONTENT_ATTRIBUTE)
          .getNodeValue();
      try {
        if (name.equals(DSK_ATTRIBUTE)) {
          ldcDocument.setNewsDesk(content);
        } else if (name.equals(ALTERNATE_URL_ATTRIBUTE)) {
          ldcDocument.setAlternateURL((new URL(content)));
        } else if (name.equals(ONLINE_SECTIONS_ATTRIBUTE)) {
          ldcDocument.setOnlineSection(content);
        } else if (name.equals(PRINT_PAGE_NUMBER_ATTRIBUTE)) {
          ldcDocument.setPage(Integer.parseInt(content));
        } else if (name.equals(PRINT_SECTION_ATTRIBUTE)) {
          ldcDocument.setSection(content);
        } else if (name.equals(SLUG_ATTRIBUTE)) {
          ldcDocument.setSlug(content);
        } else if (name.equals(PRINT_COLUMN_ATTRIBUTE)) {
          ldcDocument.setColumnNumber(Integer.parseInt(content.trim()));
        } else if (name.equals(BANNER_ATTRIBUTE)) {
          ldcDocument.setBanner(content);
        } else if (name.equals(CORRECTION_DATE_ATTRIBUTE)) {
          ldcDocument.setCorrectionDate(format.parse(content));
        } else if (name.equals(FEATURE_PAGE_ATTRIBUTE)) {
          ldcDocument.setFeaturePage(content);
        } else if (name.equals(COLUMN_NAME_ATTRIBUTE)) {
          ldcDocument.setColumnName(content);
        } else if (name.equals(SERIES_NAME_ATTRIBUTE)) {
          ldcDocument.setSeriesName(content);
        } else if (name.equals(PUBLICATION_DAY_OF_MONTH_ATTRIBUTE)) {
          ldcDocument.setPublicationDayOfMonth(Integer.parseInt(content));
        } else if (name.equals(PUBLICATION_MONTH_ATTRIBUTE)) {
          ldcDocument.setPublicationMonth(Integer.parseInt(content));
        } else if (name.equals(PUBLICATION_YEAR_ATTRIBUTE)) {
          ldcDocument.setPublicationYear(Integer.parseInt(content));
        } else if (name.equals(PULICATION_DAY_OF_WEEK_ATTRIBUTE)) {
          ldcDocument.setDayOfWeek(content);
        }

      } catch (MalformedURLException e) {
        //e.printStackTrace();
        LOG.error("Error parsing url from string " + content
            + " in file " + ldcDocument.getSourceFile() + ".");
      } catch (NumberFormatException e) {
        //e.printStackTrace();
        LOG.error("Error parsing integer" + " from string "
            + content + " in file "
            + ldcDocument.getSourceFile() + ".");
      } catch (ParseException e) {
        //e.printStackTrace();
        LOG.error("Error parsing date" + " from string " + content
            + " in file " + ldcDocument.getSourceFile() + ".");
      }
    }

    /**
     * Load a document from a BufferedReader without validating it.
     * @param bRdr the BufferedReader that data read in
     * @param file the file that data stored in
     * @return the parsed document or null if an error occurs
     */
    private org.w3c.dom.Document loadFromBufferedReader(BufferedReader bRdr, File file) {
      org.w3c.dom.Document document;
      StringBuffer sb = new StringBuffer();
      try {
        String line;
        while ((line = bRdr.readLine()) != null) {
          sb.append(line + "\n");
        }
        String xmlData = sb.toString();
        xmlData = xmlData.replace("<!DOCTYPE nitf "
                + "SYSTEM \"http://www.nitf.org/"
                + "IPTC/NITF/3.3/specification/dtd/nitf-3-3.dtd\">", "");
        document = parseStringToDOM(xmlData, "UTF-8", file);
        return document;
      } catch (IOException e) {
        LOG.error("Error loading file " + file + ".");
      }
      return null;
    }

    /**
     * Load a document without validating it. Since instructing the java.xml
     * libraries to do this does not actually disable validation, this method
     * disables validation by removing the doctype declaration from the XML
     * document before it is parsed.
     *
     * @param file the file to parse
     * @return the parsed document or null if an error occurs
     */
    private org.w3c.dom.Document loadNonValidating(File file) {
      org.w3c.dom.Document document;
      StringBuffer sb = new StringBuffer();
      try {
        BufferedReader in = new BufferedReader(new InputStreamReader(
            new FileInputStream(file), "UTF8"));
        String line = null;
        while ((line = in.readLine()) != null) {
          sb.append(line + "\n");
        }
        String xmlData = sb.toString();
        xmlData = xmlData.replace("<!DOCTYPE nitf "
            + "SYSTEM \"http://www.nitf.org/"
            + "IPTC/NITF/3.3/specification/dtd/nitf-3-3.dtd\">", "");
        document = parseStringToDOM(xmlData, "UTF-8", file);
        in.close();
        return document;
      } catch (UnsupportedEncodingException e) {
        //e.printStackTrace();
        LOG.error("Error loading file " + file + ".");
      } catch (FileNotFoundException e) {
        //e.printStackTrace();
        LOG.error("Error loading file " + file + ".");
      } catch (IOException e) {
        //e.printStackTrace();
        LOG.error("Error loading file " + file + ".");
      }
      return null;
    }

    /**
     * Parse the specified file into a DOM Document.
     *
     * @param file the file to parse.
     * @return the parsed DOM Document or null if an error occurs
     */
    private org.w3c.dom.Document loadValidating(File file) {
      try {
        return getDOMObject(file.getAbsolutePath(), true);
      } catch (SAXException e) {
        //e.printStackTrace();
        LOG.error("Error parsing digital document from nitf file "
            + file + ".");
      } catch (IOException e) {
        //e.printStackTrace();
        LOG.error("Error parsing digital document from nitf file "
            + file + ".");
      } catch (ParserConfigurationException e) {
        //e.printStackTrace();
        LOG.error("Error parsing digital document from nitf file "
            + file + ".");
      }
      return null;
    }

    /**
     * Parse a string to a DOM document.
     *
     * @param s a string containing an XML document
     * @return the DOM document if it can be parsed, or null otherwise
     */
    private org.w3c.dom.Document parseStringToDOM(String s, String encoding, File file) {
      try {
        DocumentBuilderFactory factory = DocumentBuilderFactory
            .newInstance();
        factory.setValidating(false);
        InputStream is = new ByteArrayInputStream(s.getBytes(encoding));
        org.w3c.dom.Document doc = factory.newDocumentBuilder().parse(is);
        is.close();
        return doc;
      } catch (SAXException e) {
        //e.printStackTrace();
        LOG.error("Exception processing file " + file + ".");
      } catch (ParserConfigurationException e) {
        //e.printStackTrace();
        LOG.error("Exception processing file " + file + ".");
      } catch (IOException e) {
        //e.printStackTrace();
        LOG.error("Exception processing file " + file + ".");
      }
      return null;
    }

    /**
     * Parse a file containing an XML document, into a DOM object.
     *
     * @param filename a path to a valid file
     * @param validating true iff validating should be turned on
     * @return a DOM Object containing a parsed XML document or a null value if there
     *   is an error in parsing
     * @throws ParserConfigurationException if error encountered
     * @throws IOException if error encountered
     * @throws SAXException if error encountered
     */
    private org.w3c.dom.Document getDOMObject(String filename, boolean validating)
        throws SAXException, IOException, ParserConfigurationException {
      // Create a builder factory

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      if (!validating) {
        factory.setValidating(validating);
        factory.setSchema(null);
        factory.setNamespaceAware(false);
      }

      DocumentBuilder builder = factory.newDocumentBuilder();
      // Create the builder and parse the file
      org.w3c.dom.Document doc = builder.parse(new File(filename));
      return doc;
    }

    private String parseBlock(Node node) {
      StringBuffer sb = new StringBuffer();
      NodeList children = node.getChildNodes();
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        String name = child.getNodeName();
        if (name.equals(P_TAG)) {
          sb.append(getAllText(child).trim() + "\n");
        }
      }

      if (sb.length() > 0) {
        sb.setLength(sb.length() - 1);
        String returnVal = sb.toString();
        return returnVal.length() > 0 ? returnVal : null;
      }
      return null;
    }

    private String getAttributeValue(Node node, String attributeName) {
      NamedNodeMap attributes = node.getAttributes();
      if (attributes != null) {
        Node attribute = attributes.getNamedItem(attributeName);
        if (attribute != null) {
          return attribute.getNodeValue();
        }
      }
      return null;
    }

    private String getAllText(Node node) {
      List<Node> textNodes = getNodesByTagName(node, "#text");
      StringBuffer sb = new StringBuffer();
      for (Node textNode : textNodes) {
        sb.append(textNode.getNodeValue().trim() + " ");
      }
      return sb.toString().trim();
    }

    private List<Node> getNodesByTagName(Node node, String tagName) {
      List<Node> matches = new ArrayList<Node>();
      recursiveGetNodesByTagName(node, tagName.toLowerCase(), matches);
      return matches;
    }

    private void recursiveGetNodesByTagName(Node node, String tagName,
                                            List<Node> matches) {
      String name = node.getNodeName();
      if (name != null && name.toLowerCase().equals(tagName)) {
        matches.add(node);
      }
      if (node.getChildNodes() != null
          && node.getChildNodes().getLength() > 0) {
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
          recursiveGetNodesByTagName(node.getChildNodes().item(i),
              tagName, matches);
        }
      }
    }
  }
}

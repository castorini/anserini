/*
 *  
 * Copyright 2008 The New York Times Company
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.anserini.document.nyt;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * NYTimesLDCDocument <BR>
 * Created: Jun 17, 2008 <BR>
 * Author: Evan Sandhaus (sandhes@nytimes.com)<BR>
 * <P>
 * This class represents a New York Times Corpus Document. See field comments
 * for individual field description.
 * <P>
 * 
 * @author Evan Sandhaus
 * 
 */
public class NYTCorpusDocument {
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
	 * <p>
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
	 * The �dateline� field is the dateline of the article. Generally a dateline
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
	 * The �descriptors� field specifies a list of descriptive terms drawn from
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
	 * The
	 */
	protected String featurePage;

	/**
	 * The �general online descriptors� field specifies a list of descriptors
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
	 * The �lead Paragraph� field is the lead paragraph of the article.
	 * Generally this field is populated with the first two paragraphs from the
	 * article.
	 */
	protected String leadParagraph;

	/**
	 * The �locations� field specifies a list of geographic descriptors drawn
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
	 * The �names� field specifies a list of names mentioned in the article.
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
	 * be specified as a �;� delineated list.
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
	 * <li>Bridge & Tunnel (Play)
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
	 * <li>Law & Order (TV Program)
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
	 * @param s
	 *            A string.
	 * @param length
	 *            The target length for the string.
	 * @return A left-justified string.
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
	 * @param alternateURL
	 *            the alternativeURL to set
	 */
	public void setAlternateURL(URL alternateURL) {
		this.alternateURL = alternateURL;
	}

	/**
	 * Setter for the articleAbstract property.
	 * 
	 * @param articleAbstract
	 *            the articleAbstract to set
	 */
	public void setArticleAbstract(String articleAbstract) {
		this.articleAbstract = articleAbstract;
	}

	/**
	 * Setter for the authorBiography property.
	 * 
	 * @param authorBiography
	 *            the authorBiography to set
	 */
	public void setAuthorBiography(String authorBiography) {
		this.authorBiography = authorBiography;
	}

	/**
	 * Setter for the banner property.
	 * 
	 * @param banner
	 *            the banner to set
	 */
	public void setBanner(String banner) {
		this.banner = banner;
	}

	/**
	 * Setter for the biographicalCategories property.
	 * 
	 * @param biographicalCategories
	 *            the biographicalCategories to set
	 */
	public void setBiographicalCategories(List<String> biographicalCategories) {
		this.biographicalCategories = biographicalCategories;
	}

	/**
	 * Setter for the body property.
	 * 
	 * @param body
	 *            the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * Setter for the byline property.
	 * 
	 * @param byline
	 *            the byline to set
	 */
	public void setByline(String byline) {
		this.byline = byline;
	}

	/**
	 * Setter for the columnName property.
	 * 
	 * @param columnName
	 *            the columnName to set
	 */
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	/**
	 * Setter for the columnNumber property.
	 * 
	 * @param columnNumber
	 *            the columnNumber to set
	 */
	public void setColumnNumber(Integer columnNumber) {
		this.columnNumber = columnNumber;
	}

	/**
	 * Setter for the correctionDate property.
	 * 
	 * @param correctionDate
	 *            the correctionDate to set
	 */
	public void setCorrectionDate(Date correctionDate) {
		this.correctionDate = correctionDate;
	}

	/**
	 * Setter for the correctionText property.
	 * 
	 * @param correctionText
	 *            the correctionText to set
	 */
	public void setCorrectionText(String correctionText) {
		this.correctionText = correctionText;
	}

	/**
	 * Setter for the credit property.
	 * 
	 * @param credit
	 *            the credit to set
	 */
	public void setCredit(String credit) {
		this.credit = credit;
	}

	/**
	 * Setter for the dateline property.
	 * 
	 * @param dateline
	 *            the dateline to set
	 */
	public void setDateline(String dateline) {
		this.dateline = dateline;
	}

	/**
	 * Setter for the dayOfWeek property.
	 * 
	 * @param dayOfWeek
	 *            the dayOfWeek to set
	 */
	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	/**
	 * Setter for the descriptors property.
	 * 
	 * @param descriptors
	 *            the descriptors to set
	 */
	public void setDescriptors(List<String> descriptors) {
		this.descriptors = descriptors;
	}

	/**
	 * Setter for the featurePage property.
	 * 
	 * @param featurePage
	 *            the featurePage to set
	 */
	public void setFeaturePage(String featurePage) {
		this.featurePage = featurePage;
	}

	/**
	 * Setter for the generalOnlineDescriptors property.
	 * 
	 * @param generalOnlineDescriptors
	 *            the generalOnlineDescriptors to set
	 */
	public void setGeneralOnlineDescriptors(
			List<String> generalOnlineDescriptors) {
		this.generalOnlineDescriptors = generalOnlineDescriptors;
	}

	/**
	 * Setter for the guid property.
	 * 
	 * @param guid
	 *            the guid to set
	 */
	public void setGuid(int guid) {
		this.guid = guid;
	}

	/**
	 * Setter for the headline property.
	 * 
	 * @param headline
	 *            the headline to set
	 */
	public void setHeadline(String headline) {
		this.headline = headline;
	}

	/**
	 * Setter for the kicker property.
	 * 
	 * @param kicker
	 *            the kicker to set
	 */
	public void setKicker(String kicker) {
		this.kicker = kicker;
	}

	/**
	 * Setter for the leadParagraph property.
	 * 
	 * @param leadParagraph
	 *            the leadParagraph to set
	 */
	public void setLeadParagraph(String leadParagraph) {
		this.leadParagraph = leadParagraph;
	}

	/**
	 * Setter for the locations property.
	 * 
	 * @param locations
	 *            the locations to set
	 */
	public void setLocations(List<String> locations) {
		this.locations = locations;
	}

	/**
	 * Setter for the names property.
	 * 
	 * @param names
	 *            the names to set
	 */
	public void setNames(List<String> names) {
		this.names = names;
	}

	/**
	 * Setter for the newsDesk property.
	 * 
	 * @param newsDesk
	 *            the newsDesk to set
	 */
	public void setNewsDesk(String newsDesk) {
		this.newsDesk = newsDesk;
	}

	/**
	 * Setter for the normalizedByline property.
	 * 
	 * @param normalizedByline
	 *            the normalizedByline to set
	 */
	public void setNormalizedByline(String normalizedByline) {
		this.normalizedByline = normalizedByline;
	}

	/**
	 * Setter for the onlineDescriptors property.
	 * 
	 * @param onlineDescriptors
	 *            the onlineDescriptors to set
	 */
	public void setOnlineDescriptors(List<String> onlineDescriptors) {
		this.onlineDescriptors = onlineDescriptors;
	}

	/**
	 * Setter for the onlineHeadline property.
	 * 
	 * @param onlineHeadline
	 *            the onlineHeadline to set
	 */
	public void setOnlineHeadline(String onlineHeadline) {
		this.onlineHeadline = onlineHeadline;
	}

	/**
	 * Setter for the onlineLeadParagraph property.
	 * 
	 * @param onlineLeadParagraph
	 *            the onlineLeadParagraph to set
	 */
	public void setOnlineLeadParagraph(String onlineLeadParagraph) {
		this.onlineLeadParagraph = onlineLeadParagraph;
	}

	/**
	 * Setter for the onlineLocations property.
	 * 
	 * @param onlineLocations
	 *            the onlineLocations to set
	 */
	public void setOnlineLocations(List<String> onlineLocations) {
		this.onlineLocations = onlineLocations;
	}

	/**
	 * Setter for the onlineOrganizations property.
	 * 
	 * @param onlineOrganizations
	 *            the onlineOrganizations to set
	 */
	public void setOnlineOrganizations(List<String> onlineOrganizations) {
		this.onlineOrganizations = onlineOrganizations;
	}

	/**
	 * Setter for the onlinePeople property.
	 * 
	 * @param onlinePeople
	 *            the onlinePeople to set
	 */
	public void setOnlinePeople(List<String> onlinePeople) {
		this.onlinePeople = onlinePeople;
	}

	/**
	 * Setter for the onlineSection property.
	 * 
	 * @param onlineSection
	 *            the onlineSection to set
	 */
	public void setOnlineSection(String onlineSection) {
		this.onlineSection = onlineSection;
	}

	/**
	 * Setter for the onlineTitles property.
	 * 
	 * @param onlineTitles
	 *            the onlineTitles to set
	 */
	public void setOnlineTitles(List<String> onlineTitles) {
		this.onlineTitles = onlineTitles;
	}

	/**
	 * Setter for the organizations property.
	 * 
	 * @param organizations
	 *            the organizations to set
	 */
	public void setOrganizations(List<String> organizations) {
		this.organizations = organizations;
	}

	/**
	 * Setter for the page property.
	 * 
	 * @param page
	 *            the page to set
	 */
	public void setPage(Integer page) {
		this.page = page;
	}

	/**
	 * Setter for the people property.
	 * 
	 * @param people
	 *            the people to set
	 */
	public void setPeople(List<String> people) {
		this.people = people;
	}

	/**
	 * Setter for the publicationDate property.
	 * 
	 * @param publicationDate
	 *            the publicationDate to set
	 */
	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
	}

	/**
	 * Setter for the publicationDayOfMonth property.
	 * 
	 * @param publicationDayOfMonth
	 *            the publicationDayOfMonth to set
	 */
	public void setPublicationDayOfMonth(Integer publicationDayOfMonth) {
		this.publicationDayOfMonth = publicationDayOfMonth;
	}

	/**
	 * Setter for the publicationMonth property.
	 * 
	 * @param publicationMonth
	 *            the publicationMonth to set
	 */
	public void setPublicationMonth(Integer publicationMonth) {
		this.publicationMonth = publicationMonth;
	}

	/**
	 * Setter for the publicationYear property.
	 * 
	 * @param publicationYear
	 *            the publicationYear to set
	 */
	public void setPublicationYear(Integer publicationYear) {
		this.publicationYear = publicationYear;
	}

	/**
	 * Setter for the section property.
	 * 
	 * @param section
	 *            the section to set
	 */
	public void setSection(String section) {
		this.section = section;
	}

	/**
	 * Setter for the seriesName property.
	 * 
	 * @param seriesName
	 *            the seriesName to set
	 */
	public void setSeriesName(String seriesName) {
		this.seriesName = seriesName;
	}

	/**
	 * Setter for the slug property.
	 * 
	 * @param slug
	 *            the slug to set
	 */
	public void setSlug(String slug) {
		this.slug = slug;
	}

	/**
	 * Setter for the sourceFile property.
	 * 
	 * @param sourceFile
	 *            the sourceFile to set
	 */
	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}

	/**
	 * Setter for the taxonomicClassifiers property.
	 * 
	 * @param taxonomicClassifiers
	 *            the taxonomicClassifiers to set
	 */
	public void setTaxonomicClassifiers(List<String> taxonomicClassifiers) {
		this.taxonomicClassifiers = taxonomicClassifiers;
	}

	/**
	 * Setter for the titles property.
	 * 
	 * @param titles
	 *            the titles to set
	 */
	public void setTitles(List<String> titles) {
		this.titles = titles;
	}

	/**
	 * Setter for the typesOfMaterial property.
	 * 
	 * @param typesOfMaterial
	 *            the typesOfMaterial to set
	 */
	public void setTypesOfMaterial(List<String> typesOfMaterial) {
		this.typesOfMaterial = typesOfMaterial;
	}

	/**
	 * Setter for the url property.
	 * 
	 * @param url
	 *            the url to set
	 */
	public void setUrl(URL url) {
		this.url = url;
	}

	/**
	 * Setter for the wordCount property.
	 * 
	 * @param wordCount
	 *            the wordCount to set
	 */
	public void setWordCount(Integer wordCount) {
		this.wordCount = wordCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
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
	private void appendProperty(StringBuffer sb, String propertyName,
			Object propertyValue) {

		if (propertyValue != null) {
			propertyValue = propertyValue.toString().replaceAll("\\s+", " ")
					.trim();
		}
		sb.append(ljust(propertyName + ":", 45) + propertyValue + "\n");
	}
}

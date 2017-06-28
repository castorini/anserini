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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import io.anserini.index.IndexCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * NYTCorpusDocumentParser <BR>
 * Created: Jun 17, 2008 <BR>
 * Author: Evan Sandhaus (sandhes@nytimes.com)<BR>
 * <P>
 * Class for parsing New York Times articles from NITF files.
 * <P>
 * 
 * @author Evan Sandhaus
 * 
 */
public class NYTCorpusDocumentParser {
	/** NITF Constant */
	private static final String CORRECTION_TEXT = "correction_text";

	/** NITF Constant */
	private static final String SERIES_NAME_TAG = "series.name";

	/** NITF Constant */
	private static final DateFormat format = new SimpleDateFormat(
			"yyyyMMdd'T'HHmmss");

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


	private static final Logger LOG = LogManager.getLogger(IndexCollection.class);
	/**
	 * Parse an New York Times Document from a file.
	 * 
	 * @param file
	 *            The file from which to parse the document.
	 * @param disableValidation
	 *            True if the file is to be validated against the nitf DTD and
	 *            false if it is not. It is recommended that validation be
	 *            disabled, as all documents in the corpus have previously been
	 *            validated against the NITF DTD.
	 * @return The parsed document, or null if an error occurs.
	 */
	public NYTCorpusDocument parseNYTCorpusDocumentFromFile(File file,
			boolean validating) {

		Document document = null;
		if (validating) {
			document = loadValidating(file);
		} else {
			document = loadNonValidating(file);
		}
		return parseNYTCorpusDocumentFromDOMDocument(file, document);
	}

	public NYTCorpusDocument parseNYTCorpusDocumentFromDOMDocument(
			File file, Document document) {
		NYTCorpusDocument ldcDocument = new NYTCorpusDocument();
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

	private void handleNITFNode(Node node, NYTCorpusDocument ldcDocument) {
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

	private void handleBodyNode(Node node, NYTCorpusDocument ldcDocument) {
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

	private void handleBodyHead(Node node, NYTCorpusDocument ldcDocument) {
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

	private void handleDatelineNode(NYTCorpusDocument ldcDocument, Node child) {
		String datelineString = getAllText(child);
		ldcDocument.setDateline(datelineString.trim());
	}

	private void handleAbstractNode(Node node, NYTCorpusDocument ldcDocument) {
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

	private void handleBylineNode(Node node, NYTCorpusDocument ldcDocument) {
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

	private void handleHeadlineNode(Node node, NYTCorpusDocument ldcDocument) {
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

	private void handleBodyContent(Node node, NYTCorpusDocument ldcDocument) {
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String name = child.getNodeName();
			if (name.equals(BLOCK_TAG)) {
				handleBlockNode(child, ldcDocument);
			}
		}
	}

	private void handleBlockNode(Node node, NYTCorpusDocument ldcDocument) {
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

	private void handleBodyEnd(Node node, NYTCorpusDocument ldcDocument) {
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

	private void handleHeadNode(Node node, NYTCorpusDocument ldcDocument) {
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

	private void handleDocdataNode(Node node, NYTCorpusDocument ldcDocument) {
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

	private void handlePubdata(Node node, NYTCorpusDocument ldcDocument) {
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

	private void handleIdentifiedContent(Node node,
			NYTCorpusDocument ldcDocument) {

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

	private void handleDocumentIdNode(NYTCorpusDocument ldcDocument, Node child) {
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

	private void handleMetaNode(Node node, NYTCorpusDocument ldcDocument) {
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
	 * Load a document without validating it. Since instructing the java.xml
	 * libraries to do this does not actually disable validation, this method
	 * disables validation by removing the doctype declaration from the XML
	 * document before it is parsed.
	 * 
	 * @param file
	 *            The file to parse.
	 * @return The parsed document or null if an error occurs.
	 */
	private Document loadNonValidating(File file) {
		Document document;
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
	 * @param file
	 *            The file to parse.
	 * @return The parsed DOM Document or null if an error occurs.
	 */
	private Document loadValidating(File file) {
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
	 * @param s
	 *            A string containing an XML document.
	 * 
	 * @return The DOM document if it can be parsed, or null otherwise.
	 */
	private Document parseStringToDOM(String s, String encoding, File file) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setValidating(false);
			InputStream is = new ByteArrayInputStream(s.getBytes(encoding));
			Document doc = factory.newDocumentBuilder().parse(is);
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
	 * @param filename
	 *            A path to a valid file.
	 * @param validating
	 *            True iff validating should be turned on.
	 * @return A DOM Object containing a parsed XML document or a null value if
	 *         there is an error in parsing.
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	private Document getDOMObject(String filename, boolean validating)
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
		Document doc = builder.parse(new File(filename));
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

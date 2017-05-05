package io.anserini.document;

/**
 * A document that represent an RDF Triple.
 */
public class RDFDocument implements SourceDocument {
  /**
   * Splitter that describes how s,p,o are split in a triple line
   */
  private final String TRIPLE_SPLITTER = "\t";

  /**
   * Id of the triple is just its line index in the dataset
   */
  private String id;

  /**
   * Subject of the triple
   */
  private String subject;

  /**
   * The predicate of the triple
   */
  private String predicate;

  /**
   * The object of the triple
   */
  private String object;

  /**
   * Constructor for an NT triples (NTriples).
   *
   * @param id id of the triple
   * @param s subject
   * @param p predicate
   * @param o object
   */
  public RDFDocument(String id, String s, String p, String o) {
    init(id, s, p, o);
  }

  /**
   * Constructor from a line
   * @param line line that contains triple information
   */
  public RDFDocument(String id, String line) throws IllegalArgumentException {
    String[] pieces = line.split(TRIPLE_SPLITTER);
    if (pieces.length == 4) {
      init(id, pieces[0], pieces[1], pieces[2]);
    } else {
      throw new IllegalArgumentException("Cannot parse triple from line: " + line);
    }
  }

  /**
   * Assign values
   * @param id id of the triple
   * @param s subject
   * @param p predicate
   * @param o object
   */
  private void init(String id, String s, String p, String o) {
    this.id = id;
    this.subject = s;
    this.predicate = p;
    this.object = o;
  }

  @Override
  public String id() {
    return id;
  }

  @Override
  public String content() {
    return this.toString();
  }

  /**
   * Always index all triples
   * @return true
   */
  @Override
  public boolean indexable() {
    return true;
  }

  @Override
  public String toString() {
    return subject + TRIPLE_SPLITTER +
            predicate + TRIPLE_SPLITTER+
            object + TRIPLE_SPLITTER + ".";
  }

  public String getSubject() {
    return subject;
  }

  public String getPredicate() {
    return predicate;
  }

  public String getObject() {
    return object;
  }
}

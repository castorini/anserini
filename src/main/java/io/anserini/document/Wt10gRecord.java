package io.anserini.document;
import io.anserini.index.IndexWebCollection;


public final class Wt10gRecord {

  public static final String DOC = "<DOC>";
  public static final String TERMINATING_DOC = "</DOC>";

  private static final String DOCNO = "<DOCNO>";
  private static final String TERMINATING_DOCNO = "</DOCNO>";

  private static final String DOCOLDNO = "<DOCOLDNO>";
  private static final String TERMINATING_DOCOLDNO = "</DOCOLDNO>";

  private static final String DOCHDR = "<DOCHDR>";
  private static final String TERMINATING_DOCHDR = "</DOCHDR>";


  public static WarcRecord parseWt10gRecord(StringBuilder builder) {

    int i = builder.indexOf(DOCNO);
    if (i == -1) {
      throw new RuntimeException("cannot find start tag " + DOCNO);
    }

    if (i != 0) throw new RuntimeException("should start with " + DOCNO + "in" + builder);

    int j = builder.indexOf(TERMINATING_DOCNO);
    if (j == -1) throw new RuntimeException("cannot find end tag " + TERMINATING_DOCNO);

    i = builder.indexOf(DOCOLDNO);
    if (i == -1) throw new RuntimeException("cannot find old tag " + DOCOLDNO);

    j = builder.indexOf(TERMINATING_DOCOLDNO);
    if (j == -1) throw new RuntimeException("cannot find end tag " + TERMINATING_DOCOLDNO);

    final String docID = builder.substring(i + DOCNO.length(), j).trim();

    i = builder.indexOf(DOCHDR);
    if (i == -1) throw new RuntimeException("cannot find header tag " + DOCHDR);

    j = builder.indexOf(TERMINATING_DOCHDR);
    if (j == -1) throw new RuntimeException("cannot find end tag " + TERMINATING_DOCHDR);

    if (j < i) throw new RuntimeException(TERMINATING_DOCHDR + " comes before " + DOCHDR);

    final String content = builder.substring(j + TERMINATING_DOCHDR.length()).trim();

    return new WarcRecord() {
      @Override
      public String id() {
        return docID;
      }

      @Override
      public String content() {
        return content;
      }

      @Override
      public String url() {
        return null;
      }

      @Override
      public String type() {
        return IndexWebCollection.RESPONSE;
      }
    };
  }
}

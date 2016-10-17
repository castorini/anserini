package io.anserini.document;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import io.anserini.index.IndexWebCollection;

public final class TrecTextRecord {

  public static final String DOCNO = "<DOCNO>";
  public static final String TERMINATING_DOCNO = "</DOCNO>";

  public static final String DOC = "<DOC>";
  public static final String TERMINATING_DOC = "</DOC>";

  public static final String[] startTags = {"<TEXT>", "<HEADLINE>", "<TITLE>", "<HL>", "<HEAD>",
          "<TTL>", "<DD>", "<DATE>", "<LP>", "<LEADPARA>"
  };
  public static final String[] endTags = {"</TEXT>", "</HEADLINE>", "</TITLE>", "</HL>", "</HEAD>",
          "</TTL>", "</DD>", "</DATE>", "</LP>", "</LEADPARA>"
  };

  public static final int BUFFER_SIZE = 1 << 16; // 64K

  public static WarcRecord parseTrecTextRecord(StringBuilder builder) {
    int i = builder.indexOf(DOCNO);
    if (i == -1) throw new RuntimeException("cannot find start tag " + DOCNO);

    if (i != 0) throw new RuntimeException("should start with " + DOCNO);

    int j = builder.indexOf(TERMINATING_DOCNO);
    if (j == -1) throw new RuntimeException("cannot find end tag " + TERMINATING_DOCNO);

    final String docID = builder.substring(i + DOCNO.length(), j).trim();

    final String content = builder.substring(j + TERMINATING_DOCNO.length()).trim();

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

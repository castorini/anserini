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

import java.io.BufferedReader;
import java.io.IOException;

public class TrecRecord implements Indexable {

  protected final String DOCNO = "<DOCNO>";
  protected final String TERMINATING_DOCNO = "</DOCNO>";

  protected final String DOC = "<DOC>";
  protected final String TERMINATING_DOC = "</DOC>";

  protected final int BUFFER_SIZE = 1 << 16; // 64K

  private final String[] startTags = {"<TEXT>", "<HEADLINE>", "<TITLE>", "<HL>", "<HEAD>",
          "<TTL>", "<DD>", "<DATE>", "<LP>", "<LEADPARA>"
  };
  private final String[] endTags = {"</TEXT>", "</HEADLINE>", "</TITLE>", "</HL>", "</HEAD>",
          "</TTL>", "</DD>", "</DATE>", "</LP>", "</LEADPARA>"
  };

  protected String _id;
  protected String _content;

  public Indexable readNextRecord(BufferedReader reader) throws IOException {
    StringBuilder builder = new StringBuilder();
    boolean found = false;
    int inTag = -1;

    while (true) {
      String line = reader.readLine();
      if (line == null)
        return null;

      line = line.trim();

      if (line.startsWith(DOC)) {
        found = true;
        // continue to read DOCNO
        while ((line = reader.readLine()) != null) {
          if (line.startsWith(DOCNO)) {
            builder.append(line).append('\n');
            break;
          }
        }
        while (builder.indexOf(TERMINATING_DOCNO) == -1) {
          line = reader.readLine();
          if (line == null) break;
          builder.append(line).append('\n');
        }
        continue;
      }

      if (found) {
        if (line.startsWith("<")) {
          if (inTag >= 0 && line.startsWith(endTags[inTag])) {
            builder.append(line).append("\n");
            inTag = -1;
          } else if (inTag < 0) {
            for (int k = 0; k < startTags.length; k++) {
              if (line.startsWith(startTags[k])) {
                inTag = k;
                break;
              }
            }
          }
        }
        if (inTag >= 0) {
          builder.append(line).append("\n");
        }
      }

      if (line.startsWith(TERMINATING_DOC)) {
        return parseRecord(builder);
      }
    }
  }

  public Indexable parseRecord(StringBuilder builder) {
    int i = builder.indexOf(DOCNO);
    if (i == -1) throw new RuntimeException("cannot find start tag " + DOCNO);

    if (i != 0) throw new RuntimeException("should start with " + DOCNO);

    int j = builder.indexOf(TERMINATING_DOCNO);
    if (j == -1) throw new RuntimeException("cannot find end tag " + TERMINATING_DOCNO);

    _id = builder.substring(i + DOCNO.length(), j).trim();
    _content = builder.substring(j + TERMINATING_DOCNO.length()).trim();

    return this;
  }

  @Override
  public String id() {
    return _id;
  }

  @Override
  public String content() {
    return _content;
  }
}

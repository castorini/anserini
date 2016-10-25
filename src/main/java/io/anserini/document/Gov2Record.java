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
import java.util.Arrays;
import java.util.HashSet;

public class Gov2Record extends TrecRecordBase{

  public static HashSet<String> allowedFileSuffix = new HashSet<>(Arrays.asList(".gz"));
  public static HashSet<String> skippedDirs = new HashSet<>(Arrays.asList("OtherData"));

  private static final String DOCHDR = "<DOCHDR>";
  private static final String TERMINATING_DOCHDR = "</DOCHDR>";

  public static final int BUFFER_SIZE = 1 << 16; // 64K

  public static ISimpleRecord readNextRecord(BufferedReader reader) throws IOException {
    StringBuilder builder = new StringBuilder();
    boolean found = false;

    for (; ; ) {
      String line = reader.readLine();
      if (line == null)
        return null;

      line = line.trim();

      if (line.startsWith(DOC)) {
        found = true;
        continue;
      }

      if (line.startsWith(TERMINATING_DOC)) {
        return parseRecord(builder);
      }

      if (found)
        builder.append(line).append("\n");
    }
  }

  public static ISimpleRecord parseRecord(StringBuilder builder) {

    int i = builder.indexOf(DOCNO);
    if (i == -1) throw new RuntimeException("cannot find start tag " + DOCNO);

    if (i != 0) throw new RuntimeException("should start with " + DOCNO);

    int j = builder.indexOf(TERMINATING_DOCNO);
    if (j == -1) throw new RuntimeException("cannot find end tag " + TERMINATING_DOCNO);

    final String docID = builder.substring(i + DOCNO.length(), j).trim();

    i = builder.indexOf(DOCHDR);
    if (i == -1) throw new RuntimeException("cannot find header tag " + DOCHDR);

    j = builder.indexOf(TERMINATING_DOCHDR);
    if (j == -1) throw new RuntimeException("cannot find end tag " + TERMINATING_DOCHDR);

    if (j < i) throw new RuntimeException(TERMINATING_DOCHDR + " comes before " + DOCHDR);

    final String content = builder.substring(j + TERMINATING_DOCHDR.length()).trim();

    return new ISimpleRecord() {
      @Override
      public String id() {
        return docID;
      }

      @Override
      public String content() {
        return content;
      }
    };
  }
}

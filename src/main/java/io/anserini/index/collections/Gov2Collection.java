package io.anserini.index.collections;

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

import io.anserini.document.Gov2Record;
import io.anserini.document.Indexable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;

public class Gov2Collection<D extends Gov2Record> extends TrecCollection {
  public Gov2Collection() throws IOException {
    super();
    skippedFilePrefix = new HashSet<>();
    allowedFileSuffix = new HashSet<>(Arrays.asList(".gz"));
    skippedDirs = new HashSet<>(Arrays.asList("OtherData"));
  }

  @Override
  public void prepareInput(Path curInputFile) throws IOException {
    this.curInputFile = curInputFile;
    this.bRdr = null;
    InputStream stream = new GZIPInputStream(Files.newInputStream(curInputFile, StandardOpenOption.READ), BUFFER_SIZE);
    bRdr = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
  }

  @Override
  public Indexable next() {
    Gov2Record doc = new Gov2Record();
    try {
      doc = (D) doc.readNextRecord(bRdr);
      if (doc == null) {
        at_eof = true;
        doc = null;
      }
    } catch (IOException e1) {
      doc = null;
    }
    return doc;
  }
}

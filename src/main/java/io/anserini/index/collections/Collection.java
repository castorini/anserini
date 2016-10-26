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

import io.anserini.document.Indexable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public interface Collection<D extends Indexable> extends Iterator<Indexable> {
    Set<String> skippedFilePrefix = new HashSet<>();
    Set<String> allowedFilePrefix = new HashSet<>();
    Set<String> skippedFileSuffix = new HashSet<>();
    Set<String> allowedFileSuffix = new HashSet<>();
    Set<String> skippedDirs = new HashSet<>();

    public Deque<Path> discoverFiles();
    void prepareInput(Path p) throws IOException;
    void finishInput() throws IOException;
}

package io.anserini.collection;

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


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class DiscoverFiles {

  private static final Logger LOG = LogManager.getLogger(DiscoverFiles.class);

  public static Deque<Path> discover(Path p, final Set<String> skippedFilePrefix, final Set<String> allowedFilePrefix,
                                     final Set<String> skippedFileSuffix, final Set<String> allowedFileSuffix,
                                     final Set<String> skippedDir) {
    final Deque<Path> stack = new ArrayDeque<>();
    FileVisitor<Path> fv = new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

        Path name = file.getFileName();
        boolean shouldAdd = true;
        if (name != null) {
          String fileName = name.toString();
          for (String s : skippedFileSuffix) {
            if (fileName.endsWith(s)) {
              shouldAdd = false;
              break;
            }
          }
          if (shouldAdd && !allowedFileSuffix.isEmpty()) {
            shouldAdd = false;
            for (String s : allowedFileSuffix) {
              if (fileName.endsWith(s)) {
                shouldAdd = true;
                break;
              }
            }
          }
          if (shouldAdd) {
            for (String s : skippedFilePrefix) {
              if (fileName.startsWith(s)) {
                shouldAdd = false;
                break;
              }
            }
          }
          if (shouldAdd && !allowedFilePrefix.isEmpty()) {
            shouldAdd = false;
            for (String s : allowedFilePrefix) {
              if (fileName.startsWith(s)) {
                shouldAdd = true;
                break;
              }
            }
          }
        }
        if (shouldAdd) {
          stack.add(file);
        }
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        if (skippedDir.contains(dir.getFileName().toString())) {
          LOG.info("Skipping: " + dir);
          return FileVisitResult.SKIP_SUBTREE;
        }
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFileFailed(Path file, IOException ioe) {
        LOG.error("Visiting failed for " + file.toString(), ioe);
        return FileVisitResult.SKIP_SUBTREE;
      }
    };

    try {
      Files.walkFileTree(p, fv);
    } catch (IOException e) {
      LOG.error("IOException during file visiting", e);
    }
    return stack;
  }
}

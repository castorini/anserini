/**
 * Anserini: An information retrieval toolkit built on Lucene
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.anserini.document;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * This is a wrapper of parsing the source document.
 * When parsing and extracting raw source document from files
 * we'd better record augmented results: if the parsing/extracting succeeds?
 * if it is not, then what is the reason of failure.
 */
public class SourceDocumentResultWrapper<T extends SourceDocument> {
  public enum FailureReason {
    EOF,
    ParsingError,
    IOError
  };

  private T document;
  private Boolean status;
  private FailureReason reason;

  public SourceDocumentResultWrapper(T document, Boolean status, FailureReason reason) {
    this.document = document;
    this.status = status;
    this.reason = reason;
  }

  public void setDocument(T document) {
    this.document = document;
  }

  public void setStatus(Boolean status) {
    this.status = status;
  }

  public void setReason(FailureReason reason) {
    this.reason = reason;
  }

  public T getDocument() {
    return document;
  }

  public Boolean getStatus() {
    return status;
  }

  public FailureReason getReason() {
    return reason;
  }
}

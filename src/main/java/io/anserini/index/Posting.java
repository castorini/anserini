/**
 * Anserini: A Lucene toolkit for replicable information retrieval research
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

package io.anserini.index;

import org.apache.lucene.index.PostingsEnum;

import java.io.IOException;

/*
 Basic Posting class used to construct postings lists in IndexReaderUtils
 */

public class Posting {
    private int docId;
    private int termFreq;
    private int[] positions;

    public Posting() {}

    public Posting(PostingsEnum postingsEnum) throws IOException {
        this.docId = postingsEnum.docID();
        this.termFreq = postingsEnum.freq();
        this.positions = new int[this.termFreq];
        for (int j=0; j < this.termFreq; j++) {
            this.positions[j] = postingsEnum.nextPosition();
        }
    }

    public int getTF() {
        return this.termFreq;
    }

    public int getDocid() {
        return this.docId;
    }

    public int[] getPositions() {
        return this.positions;
    }
}
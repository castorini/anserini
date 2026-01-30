/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
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

package io.anserini.index.prebuilt;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;

public class PrebuiltImpactIndex extends PrebuiltIndex {
  private static final TypeReference<List<Entry>> ENTRY_LIST_TYPE = new TypeReference<List<Entry>>() {};

  // This is the singleton instance of this class. Use the holder pattern to ensure thread safety.
  private static class Holder {
    private static final PrebuiltImpactIndex INSTANCE = new PrebuiltImpactIndex();
  }

  public static class Entry extends PrebuiltIndex.Entry {
    @JsonProperty("total_terms")
    public long totalTerms;

    @JsonProperty("documents")
    public int documents;

    @JsonProperty("unique_terms")
    public long uniqueTerms;
  }

  private final List<Entry> entries;
  private final Map<String, Entry> byName;

  private PrebuiltImpactIndex() {
    List<Entry> loadedEntries = loadEntries(PrebuiltIndex.Type.IMPACT, ENTRY_LIST_TYPE, PrebuiltImpactIndex.class);
    entries = Collections.unmodifiableList(loadedEntries);

    Map<String, Entry> map = new HashMap<>(Math.max(16, entries.size() * 2));
    for (Entry entry : entries) {
      if (entry != null && entry.name != null) {
        map.put(entry.name, entry);
      }
    }

    this.byName = Collections.unmodifiableMap(map);
  }

  public static List<Entry> entries() {
    return Holder.INSTANCE.entries;
  }

  public static Entry get(String name) {
    return Holder.INSTANCE.byName.get(name);
  }
}

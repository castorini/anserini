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

package io.anserini.fusion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Wrapper class for a TREC run.
*/
public class TrecRun {
  // Enum representing the columns in the TREC run file
  public enum Column {
    TOPIC, Q0, DOCID, RANK, SCORE, TAG
  }
  
  private List<Map<Column, Object>> runData;
  private Path filepath = null;
  private Boolean reSort = false;
  private int queriesMerged = 0;

  private static final Logger LOG = LogManager.getLogger(TrecRun.class);
  
  // Constructor without reSort parameter
  public TrecRun(Path filepath) throws IOException {
    this(filepath, false);
  }

  // Constructor with reSort parameter
  public TrecRun(Path filepath, Boolean reSort) throws IOException {
    this.resetData();
    this.filepath = filepath;
    this.reSort = reSort;
    this.readRun(filepath);
  }

  // Constructor without parameters
  public TrecRun() {
    this.resetData();
  }

  private void resetData() {
    runData = new ArrayList<>();
  }

  /**
   * Reads a TREC run file and loads its data into the runData list.
   * 
   * @param filepath Path to the TREC run file.
   * @throws IOException If the file cannot be read.
   */
  public void readRun(Path filepath) throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader(filepath.toFile()))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] data = line.split("\\s+");
        Map<Column, Object> record = new EnumMap<>(Column.class);
  
        // Populate the record map with the parsed data
        record.put(Column.TOPIC, data[0]);
        record.put(Column.Q0, data[1]);
        record.put(Column.DOCID, data[2]);
        record.put(Column.TAG, data[5]);
        
        // Parse RANK as integer
        int rankInt = Integer.parseInt(data[3]);
        record.put(Column.RANK, rankInt);

        // Parse SCORE as double
        double scoreFloat = Double.parseDouble(data[4]);
        record.put(Column.SCORE, scoreFloat);
  
        // Add the record to runData
        runData.add(record);
      }
    }
  
    if (reSort) {
      runData.sort((record1, record2) -> {
        int topicComparison = ((String)record1.get(Column.TOPIC)).compareTo((String)(record2.get(Column.TOPIC)));
        if (topicComparison != 0) {
          return topicComparison;
        }
        return Double.compare((Double)(record2.get(Column.SCORE)), (Double)record1.get(Column.SCORE));
      });
      String currentTopic = "";
      int rank = 1;
      for (Map<Column, Object> record : runData) {
        String topic = (String) record.get(Column.TOPIC);
        if (!topic.equals(currentTopic)) {
          currentTopic = topic;
          rank = 1;
        }
        record.put(Column.RANK, rank);
        rank++;
      }
    }
  }

  public Set<String> getTopics() {
    return runData.stream().map(record -> (String) record.get(Column.TOPIC)).collect(Collectors.toSet());
  }

  public TrecRun cloneRun() throws IOException {
    TrecRun clone = new TrecRun();
    clone.runData = new ArrayList<>(this.runData);
    clone.filepath = this.filepath;
    clone.reSort = this.reSort;
    return clone;
  }
  
  /**
   * Saves the TREC run data to a text file in the TREC run format.
   * 
   * @param outputPath Path to the output file.
   * @param tag Tag to be added to each record in the TREC run file. If null, the existing tags are retained.
   * @throws IOException If an I/O error occurs while writing to the file.
   * @throws IllegalStateException If the runData list is empty.
   */
  public void saveToTxt(Path outputPath, String tag) throws IOException {
    if (runData.isEmpty()) {
      throw new IllegalStateException("Nothing to save. TrecRun is empty");
    }
    if (tag != null) {
      runData.forEach(record -> record.put(Column.TAG, tag));
    }
    runData.sort(Comparator.comparing((Map<Column, Object> r) -> (String) r.get(Column.TOPIC))
      .thenComparing(r -> (Double) r.get(Column.SCORE), Comparator.reverseOrder()));
    FileUtils.writeLines(outputPath.toFile(), runData.stream()
        .map(record -> record.entrySet().stream()
            .map(entry -> {
              if (entry.getKey() == Column.SCORE) {
                return String.format("%.6f", entry.getValue());
              } else {
                return entry.getValue().toString();
              }
            })
            .collect(Collectors.joining(" ")))
        .collect(Collectors.toList()));
  }

  public List<Map<Column, Object>> getDocsByTopic(String topic, int maxDocs) {
    return runData.stream()
        .filter(record -> record.get(Column.TOPIC).equals(topic))  // Filter by topic
        .limit(maxDocs > 0 ? maxDocs : Integer.MAX_VALUE)           // Limit the number of docs if maxDocs > 0
        .collect(Collectors.toList());                              // Collect as List<Map<Column, Object>>
  }
  
  public TrecRun rescore(RescoreMethod method, int rrfK, double scale) {
    switch (method) {
        case RRF -> rescoreRRF(rrfK);
        case SCALE -> rescoreScale(scale);
        case NORMALIZE -> normalizeScores();
        default -> throw new UnsupportedOperationException("Unknown rescore method: " + method);
    }
    return this;
  }

  private void rescoreRRF(int rrfK) {
    runData.forEach(record -> {
      double score = 1.0 / (rrfK + (Integer)(record.get(Column.RANK)));
      record.put(Column.SCORE, score);
    });
  }

  private void rescoreScale(double scale) {
    runData.forEach(record -> {
      double score = (Double) record.get(Column.SCORE) * scale;
      record.put(Column.SCORE, score);
    });
  }

  private void normalizeScores() {
    for (String topic : getTopics()) {
      List<Map<Column, Object>> topicRecords = runData.stream()
          .filter(record -> record.get(Column.TOPIC).equals(topic))
          .collect(Collectors.toList());

      double minScore = topicRecords.stream()
          .mapToDouble(record -> (Double) record.get(Column.SCORE))
          .min().orElse(0.0);
      double maxScore = topicRecords.stream()
          .mapToDouble(record -> (Double) record.get(Column.SCORE))
          .max().orElse(1.0);

      for (Map<Column, Object> record : topicRecords) {
        double normalizedScore = ((Double) record.get(Column.SCORE) - minScore) / (maxScore - minScore);
        record.put(Column.SCORE, normalizedScore);
      }
    }
  }

  /**
   * Merges multiple TrecRun instances into a single TrecRun instance.
   * The merged run will contain the top documents for each topic, with scores summed across the input runs.
   *
   * @param runs  List of TrecRun instances to merge.
   * @param depth Maximum number of documents to consider from each run for each topic (null for no limit).
   * @param k     Maximum number of top documents to include in the merged run for each topic (null for no limit).
   * @return A new TrecRun instance containing the merged results.
   * @throws IllegalArgumentException if less than 2 runs are provided.
   */
  public static TrecRun merge(List<TrecRun> runs, Integer depth, Integer k) {
    if (runs.size() < 2) {
      throw new IllegalArgumentException("Merge requires at least 2 runs.");
    }

    TrecRun mergedRun = new TrecRun();

    Set<String> topics = runs.stream().flatMap(run -> run.getTopics().stream()).collect(Collectors.toSet());

    topics.forEach(topic -> {
      Map<String, Double> docScores = new HashMap<>();
      for (TrecRun run : runs) {
        run.getDocsByTopic(topic, depth != null ? depth : Integer.MAX_VALUE).forEach(record -> {
          String docId = (String) record.get(Column.DOCID);
          double score = (Double) record.get(Column.SCORE);
          docScores.put(docId, docScores.getOrDefault(docId, 0.0) + score);
        });
      }
      List<Map.Entry<String, Double>> sortedDocScores = docScores.entrySet().stream()
          .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
          .limit(k != null ? k : Integer.MAX_VALUE)
          .collect(Collectors.toList());

      for (int rank = 0; rank < sortedDocScores.size(); rank++) {
        Map.Entry<String, Double> entry = sortedDocScores.get(rank);
        Map<Column, Object> record = new EnumMap<>(Column.class);
        record.put(Column.TOPIC, topic);
        record.put(Column.Q0, "Q0");
        record.put(Column.DOCID, entry.getKey());
        record.put(Column.RANK, rank + 1);
        record.put(Column.SCORE, entry.getValue());
        record.put(Column.TAG, "merge_sum");
        mergedRun.runData.add(record);
      }
      mergedRun.queriesMerged++;
      if(mergedRun.queriesMerged % 100 == 0){
        LOG.info(String.format("%d queries merged", mergedRun.queriesMerged));
      }
    });

    return mergedRun;
  }
}
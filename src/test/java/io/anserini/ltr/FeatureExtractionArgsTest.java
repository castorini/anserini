/*
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

package io.anserini.ltr;

import io.anserini.ltr.FeatureExtractorCli.FeatureExtractionArgs;
import io.anserini.search.topicreader.MicroblogTopicReader;
import io.anserini.search.topicreader.TopicReader;
import io.anserini.search.topicreader.TrecTopicReader;
import io.anserini.search.topicreader.WebxmlTopicReader;
import org.junit.Assert;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ParserProperties;

/**
 * Tests that Arguments for the {@link FeatureExtractorCli} can be parsed.
 */
public class FeatureExtractionArgsTest {

  @Test
  public <K> void checkThatTopicReaderForCluewebCollectionCanBeCreated() throws Exception {
    FeatureExtractionArgs args = createFeatureExtractionArgsWithCollection("clueweb");
    TopicReader<K> topicReaderForCollection = args.buildTopicReaderForCollection();

    Assert.assertEquals(WebxmlTopicReader.class, topicReaderForCollection.getClass());
  }

  @Test
  public <K> void checkThatTopicReaderForGov2CollectionCanBeCreated() throws Exception {
    FeatureExtractionArgs args = createFeatureExtractionArgsWithCollection("gov2");
    TopicReader<K> topicReaderForCollection = args.buildTopicReaderForCollection();

    Assert.assertEquals(TrecTopicReader.class, topicReaderForCollection.getClass());
  }

  @Test
  public void checkThatTopicReaderForTwitterCollectionCanBeCreated() throws Exception {
    FeatureExtractionArgs args = createFeatureExtractionArgsWithCollection("twitter");
    TopicReader<Integer> topicReaderForCollection = args.buildTopicReaderForCollection();

    Assert.assertEquals(MicroblogTopicReader.class, topicReaderForCollection.getClass());
  }

  private static FeatureExtractionArgs createFeatureExtractionArgsWithCollection(String collection) throws CmdLineException {
    String[] args = createProgramArgsWithCollection(collection);
    return parseFeatureExtractionArgs(args);
  }

  private static String[] createProgramArgsWithCollection(String collection) {
    return new String[] { "-index", "example-index-arg", "-qrel", "example-qrel-arg", "-topic", "example-topic-arg",
        "-out", "example-out-arg", "-collection", collection };
  }

  private static FeatureExtractionArgs parseFeatureExtractionArgs(String[] args) throws CmdLineException {
    FeatureExtractionArgs parsedArgs = new FeatureExtractionArgs();
    CmdLineParser parser = new CmdLineParser(parsedArgs, ParserProperties.defaults().withUsageWidth(90));

    parser.parseArgument(args);

    return parsedArgs;
  }
}

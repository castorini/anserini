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

package io.anserini.index.generator;

import com.twitter.twittertext.Extractor;
import com.twitter.twittertext.TwitterTextParseResults;
import com.twitter.twittertext.TwitterTextParser;
import io.anserini.collection.TweetCollection;
import io.anserini.index.Constants;
import io.anserini.index.IndexCollection;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexOptions;
import org.apache.tools.bzip2.CBZip2InputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Converts a {@link TweetCollection.Document} into a Lucene {@link Document}, ready to be indexed.
 */
public class TweetGenerator implements LuceneDocumentGenerator<TweetCollection.Document> {
  private static final Logger LOG = LogManager.getLogger(TweetGenerator.class);

  private IndexCollection.Args args;

  private LongOpenHashSet deletes = null;

  public enum TweetField {
    ID_LONG("id_long"),
    SCREEN_NAME("screen_name"),
    EPOCH("epoch"),
    LANG("lang"),
    IN_REPLY_TO_STATUS_ID("in_reply_to_status_id"),
    IN_REPLY_TO_USER_ID("in_reply_to_user_id"),
    FOLLOWERS_COUNT("followers_count"),
    FRIENDS_COUNT("friends_count"),
    STATUSES_COUNT("statuses_count"),
    RETWEETED_STATUS_ID("retweeted_status_id"),
    RETWEETED_USER_ID("retweeted_user_id"),
    RETWEET_COUNT("retweet_count");

    public final String name;

    TweetField(String s) {
      name = s;
    }
  }

  public TweetGenerator(IndexCollection.Args args) throws IOException {
    this.args = args;

    if (!args.tweetDeletedIdsFile.isEmpty()) {
      deletes = new LongOpenHashSet();
      File deletesFile = new File(args.tweetDeletedIdsFile);
      if (!deletesFile.exists()) {
        System.err.println("Error: " + deletesFile + " does not exist!");
        System.exit(-1);
      }
      LOG.info("Reading deletes from " + deletesFile);

      FileInputStream fin = new FileInputStream(deletesFile);
      byte[] ignoreBytes = new byte[2];
      fin.read(ignoreBytes); // "B", "Z" bytes from commandline tools
      BufferedReader br = new BufferedReader(new InputStreamReader(new CBZip2InputStream(fin)));

      String s;
      while ((s = br.readLine()) != null) {
        if (s.contains("\t")) {
          deletes.add(Long.parseLong(s.split("\t")[0]));
        } else {
          deletes.add(Long.parseLong(s));
        }
      }
      br.close();
      fin.close();
      LOG.info("Read " + deletes.size() + " tweetids from deletes file.");
    }
  }

  @Override
  public Document createDocument(TweetCollection.Document tweetDoc) throws GeneratorException {
    String id = tweetDoc.id();

    if (tweetDoc.contents().trim().isEmpty()) {
      throw new EmptyDocumentException();
    }

    final TwitterTextParseResults result = TwitterTextParser.parseTweet(tweetDoc.contents().trim());
    if (!result.isValid) {
      throw new InvalidDocumentException();
    }

    String text = tweetDoc.contents().trim().substring(result.validTextRange.start, result.validTextRange.end);

    if (!args.tweetKeepUrls) {
      final Extractor extractor = new Extractor();
      final List<String> urls = extractor.extractURLs(text);
      for (String url : urls) {
        text = text.replaceAll(url, "");
      }
    }

    text = text.trim();
    if (text.isEmpty()) {
      throw new EmptyDocumentException();
    }

    // Skip deletes tweetids.
    if (deletes != null && deletes.contains(Long.parseLong(id))) {
      throw new SkippedDocumentException();
    }

    if (tweetDoc.getIdLong() > args.tweetMaxId) {
      throw new SkippedDocumentException();
    }

    if (!args.tweetKeepRetweets && tweetDoc.getRetweetedStatusId().isPresent()) {
      throw new SkippedDocumentException();
    }

    Document doc = new Document();
    doc.add(new StringField(Constants.ID, id, Field.Store.YES));

    // We need this to break scoring ties.
    doc.add(new LongPoint(TweetField.ID_LONG.name, tweetDoc.getIdLong()));
    doc.add(new NumericDocValuesField(TweetField.ID_LONG.name, tweetDoc.getIdLong()));

    tweetDoc.getEpoch().ifPresent(epoch -> doc.add(new LongPoint(TweetField.EPOCH.name, epoch)));
    doc.add(new StringField(TweetField.SCREEN_NAME.name, tweetDoc.getScreenName(), Field.Store.NO));
    doc.add(new IntPoint(TweetField.FRIENDS_COUNT.name, tweetDoc.getFollowersCount()));
    doc.add(new IntPoint(TweetField.FOLLOWERS_COUNT.name, tweetDoc.getFriendsCount()));
    doc.add(new IntPoint(TweetField.STATUSES_COUNT.name, tweetDoc.getStatusesCount()));

    tweetDoc.getInReplyToStatusId().ifPresent(rid -> {
      doc.add(new LongPoint(TweetField.IN_REPLY_TO_STATUS_ID.name, rid));
      tweetDoc.getInReplyToUserId().ifPresent(ruid ->
        doc.add(new LongPoint(TweetField.IN_REPLY_TO_USER_ID.name, ruid)));
    });

    tweetDoc.getRetweetedStatusId().ifPresent(rid -> {
      doc.add(new LongPoint(TweetField.RETWEETED_STATUS_ID.name, rid));
      tweetDoc.getRetweetedUserId().ifPresent(ruid ->
        doc.add(new LongPoint(TweetField.RETWEETED_USER_ID.name, ruid)));
      tweetDoc.getRetweetCount().ifPresent(rc ->
        doc.add(new LongPoint(TweetField.RETWEET_COUNT.name, rc)));
    });

    tweetDoc.getLang().ifPresent(lang -> doc.add(new StringField(TweetField.LANG.name, lang, Field.Store.NO)));

    if (args.storeRaw) { // store the raw json string as one single field
      doc.add(new StoredField(Constants.RAW, tweetDoc.getJsonString()));
    }

    FieldType fieldType = new FieldType();

    fieldType.setStored(args.storeContents);

    // Are we storing document vectors?
    if (args.storeDocvectors) {
      fieldType.setStoreTermVectors(true);
      fieldType.setStoreTermVectorPositions(true);
    }

    // Are we building a "positional" or "count" index?
    if (args.storePositions) {
      fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
    } else {
      fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
    }

    doc.add(new Field(Constants.CONTENTS, text, fieldType));

    return doc;
  }
}

/**
 * Twitter Tools
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

package io.anserini.document.twitter;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Object representing a status.
 */
public class Status {
	private static final Logger LOG = LogManager.getLogger(Status.class);

	private static final JsonParser JSON_PARSER = new JsonParser();
	private static final String DATE_FORMAT = "EEE MMM d k:m:s ZZZZZ yyyy"; // "Fri
																			// Mar
																			// 29
																			// 11:03:41
																			// +0000
																			// 2013";
	private long id;
	private String screenname;
	private String name;
	private String profile_image_url;
	private String createdAt;
	private long epoch;
	private String text;
	private JsonObject jsonObject;
	private String jsonString;
	private String lang;
	private long inReplyToStatusId;
	private long inReplyToUserId;
	private int followersCount;
	private int friendsCount;
	private int statusesCount;
	private double latitude;
	private double longitude;
	private long retweetStatusId;
	private long retweetUserId;
	private int retweetCount;
	private String retweetStatusString;

	protected Status() {
	}

	public long getId() {
		return id;
	}

	public String getScreenname() {
		return screenname;
	}

	public String getName() {
		return name;
	}

	public String getProfileImageURL() {
		return profile_image_url;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public long getEpoch() {
		return epoch;
	}

	public String getText() {
		return text;
	}

	public JsonObject getJsonObject() {
		return jsonObject;
	}

	public String getJsonString() {
		return jsonString;
	}

	public String getLang() {
		return lang;
	}

	public int getFollowersCount() {
		return followersCount;
	}

	public int getFriendsCount() {
		return friendsCount;
	}

	public int getStatusesCount() {
		return statusesCount;
	}

	public long getInReplyToStatusId() {
		return inReplyToStatusId;
	}

	public long getInReplyToUserId() {
		return inReplyToUserId;
	}

	public double getlatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public long getRetweetedStatusId() {
		return retweetStatusId;
	}

	public long getRetweetedUserId() {
		return retweetUserId;
	}

	public int getRetweetCount() {
		return retweetCount;
	}
	public String getRetweetStatusString(){
		return retweetStatusString;
	}

	public static Status fromJson(String json) {
		JsonObject obj = null;
		try {
			obj = (JsonObject) JSON_PARSER.parse(json);
		} catch (Exception e) {
			// Catch any malformed JSON.
			LOG.error("Error parsing: " + json);
			return null;
		}

		if (obj.get("text") == null) {
			return null;
		}

		Status status = new Status();
		status.text = obj.get("text").getAsString();
		status.id = obj.get("id").getAsLong();
		status.screenname = obj.get("user").getAsJsonObject().get("screen_name").getAsString();
		status.name = obj.get("user").getAsJsonObject().get("name").getAsString();
		status.profile_image_url = obj.get("user").getAsJsonObject().get("profile_image_url").getAsString();
		status.createdAt = obj.get("created_at").getAsString();

		try {
			status.epoch = (new SimpleDateFormat(DATE_FORMAT)).parse(status.createdAt).getTime() / 1000;
		} catch (ParseException e) {
			status.epoch = -1L;
		}

		// TODO: trying to fetch fields and then catching exceptions is bad
		// practice, fix!
		try {
			status.inReplyToStatusId = obj.get("in_reply_to_status_id").getAsLong();
		} catch (Exception e) {
			status.inReplyToStatusId = -1L;
		}

		try {
			status.inReplyToUserId = obj.get("in_reply_to_user_id").getAsLong();
		} catch (Exception e) {
			status.inReplyToUserId = -1L;
		}

		try {
			status.retweetStatusString=obj.get("retweeted_status").getAsString();
			status.retweetStatusId = obj.getAsJsonObject("retweeted_status").get("id").getAsLong();
			status.retweetUserId = obj.getAsJsonObject("retweeted_status").get("user").getAsJsonObject().get("id")
					.getAsLong();
			// retweet_count might say "100+"
			// TODO: This is ugly, come back and fix later.
			status.retweetCount = Integer.parseInt(obj.get("retweet_count").getAsString().replace("+", ""));
		} catch (Exception e) {
			status.retweetStatusId = -1L;
			status.retweetUserId = -1L;
			status.retweetCount = -1;
		}

		try {
			status.inReplyToUserId = obj.get("in_reply_to_user_id").getAsLong();
		} catch (Exception e) {
			status.inReplyToUserId = -1L;
		}

		try {
			status.latitude = obj.getAsJsonObject("coordinates").getAsJsonArray("coordinates").get(1).getAsDouble();
			status.longitude = obj.getAsJsonObject("coordinates").getAsJsonArray("coordinates").get(0).getAsDouble();
		} catch (Exception e) {
			status.latitude = Double.NEGATIVE_INFINITY;
			status.longitude = Double.NEGATIVE_INFINITY;
		}

		try {
			status.lang = obj.get("lang").getAsString();
		} catch (Exception e) {
			status.lang = "unknown";
		}

		status.followersCount = obj.get("user").getAsJsonObject().get("followers_count").getAsInt();
		status.friendsCount = obj.get("user").getAsJsonObject().get("friends_count").getAsInt();
		status.statusesCount = obj.get("user").getAsJsonObject().get("statuses_count").getAsInt();

		status.jsonObject = obj;
		status.jsonString = json;

		return status;
	}

	public static Status fromTSV(String tsv) {
		String[] columns = tsv.split("\t");

		if (columns.length < 4) {
			System.err.println("error parsing: " + tsv);
			return null;
		}

		Status status = new Status();
		status.id = Long.parseLong(columns[0]);
		status.screenname = columns[1];
		status.createdAt = columns[2];

		StringBuilder b = new StringBuilder();
		for (int i = 3; i < columns.length; i++) {
			b.append(columns[i] + " ");
		}
		status.text = b.toString().trim();

		return status;
	}
}

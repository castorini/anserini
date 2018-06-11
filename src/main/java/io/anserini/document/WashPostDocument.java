package io.anserini.document;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A Washington Post document.
 */
public class WashPostDocument implements SourceDocument {
    private static final Logger LOG = LogManager.getLogger(WashPostDocument.class);
    private static final String PATTERN = "<\\/?\\w+>";

    private final String ID_TAG = "id";
    private final String DATE_TAG = "published_date";
    private final String CONTENT_TAG = "contents";
    private final String PARAGRAPH_TAG = "content";
    private final String TYPE_TAG = "type";
    private final List<String> CONTENT_TYPE_TAG = Arrays.asList("sanitized_html", "tweet");

    protected String id;
    protected long date;
    protected String content;


    @Override
    public SourceDocument readNextRecord(BufferedReader bufferedReader) throws IOException {
        String nextRecord = bufferedReader.readLine();
        if (nextRecord == null) {
            return null;
        }
        return parseRecord(nextRecord);

    }

    private SourceDocument parseRecord(String record) {
        StringBuilder builder = new StringBuilder();

        JSONObject contentObj;
        JSONObject recordObj = new JSONObject(record);

        if (!recordObj.has(ID_TAG) || !recordObj.has(DATE_TAG)) {
            // For current dataset, we can make sure all record has unique id and
            //  published date. So we just simply log a warning and return null
            //  here in case future data may bring up this issue
            LOG.warn("No unique ID or published date for this record, ignored...");
            return null;
        }
        id = recordObj.getString(ID_TAG);
        date = recordObj.getLong(DATE_TAG);

        JSONArray contentArray = recordObj.getJSONArray(CONTENT_TAG);
        for (Object obj : contentArray) {
            contentObj = (JSONObject) obj;
            if (contentObj.has(TYPE_TAG) && contentObj.has(PARAGRAPH_TAG)) {
                if (CONTENT_TYPE_TAG.contains(contentObj.getString(TYPE_TAG))) {
                    try {
                        builder.append(removeTags(contentObj.getString(PARAGRAPH_TAG).trim())).append("\n");
                    } catch (JSONException e) {
                        LOG.error("Error caught while retrieving JSON string.");
                        e.printStackTrace();
                    }
                }
            } else {
                LOG.warn("No type or content tag defined in Article " + id + ", ignored this file.");
            }
        }
        content = builder.toString();
        return this;
    }

    private String removeTags(String content) {
        return content.replaceAll(PATTERN, " ");
    }


    @Override
    public String id() {
        return id;
    }

    @Override
    public String content() {
        return content;
    }

    @Override
    public boolean indexable() {
        return true;
    }
}

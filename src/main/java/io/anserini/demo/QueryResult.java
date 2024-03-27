package io.anserini.demo;

public class QueryResult {
    private String docId;
    private String content;
    private float score;

    public QueryResult(String docId, String content, float score) {
        this.docId = docId;
        this.content = content;
        this.score = score;
    }

    // Getters
    public String getDocId() {
        return docId;
    }

    public String getContent() {
        return content;
    }

    public float getScore() {
        return score;
    }

    // Setters
    public void setDocId(String docId) {
        this.docId = docId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setRank(int rank) {
        this.score = rank;
    }
}
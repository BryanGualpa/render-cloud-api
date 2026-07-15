package com.analyticore.domain.model;

import java.util.List;

public class AnalysisResult {
    private final String sentiment;
    private final List<String> keywords;

    public AnalysisResult(String sentiment, List<String> keywords) {
        this.sentiment = sentiment;
        this.keywords = keywords;
    }

    public String getSentiment() { return sentiment; }
    public List<String> getKeywords() { return keywords; }
}

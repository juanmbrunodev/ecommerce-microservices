package com.jmb.composite.product;

public class RecommendationSummary {

    private int recommendationId;
    private String author;
    private int rate;
    private String content;

    public RecommendationSummary() {}

    public RecommendationSummary(int recommendationId, String author, int rate, String content) {
        this.recommendationId = recommendationId;
        this.author = author;
        this.rate = rate;
        this.content = content;
    }

    public int getRecommendationId() {
        return recommendationId;
    }

    public String getAuthor() {
        return author;
    }

    public int getRate() {
        return rate;
    }

    public String getContent() {
        return content;
    }
}

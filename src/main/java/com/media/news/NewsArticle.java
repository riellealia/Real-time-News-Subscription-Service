package com.media.news;

import java.time.Instant;

/** Immutable breaking-news message published by the agency. */
public record NewsArticle(
        String headline,
        String content,
        NewsCategory category,
        Instant publishedAt) {

    public NewsArticle {
        if (headline == null || headline.isBlank()) {
            throw new IllegalArgumentException("Headline must not be blank.");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content must not be blank.");
        }
        if (category == null) {
            throw new IllegalArgumentException("Category must not be null.");
        }
        if (publishedAt == null) {
            throw new IllegalArgumentException("Publication time must not be null.");
        }
    }

    public static NewsArticle breaking(String headline, String content, NewsCategory category) {
        return new NewsArticle(headline, content, category, Instant.now());
    }
}


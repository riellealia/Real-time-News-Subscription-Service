package com.media.news;

/** Observer contract used by the news agency without depending on concrete users. */
public interface Subscriber {
    String getName();

    boolean isInterestedIn(NewsCategory category);

    void onBreakingNews(NewsArticle article);
}


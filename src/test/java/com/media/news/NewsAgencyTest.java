package com.media.news;

import java.util.Set;

/** Lightweight tests using only the Java standard library. */
public final class NewsAgencyTest {
    private NewsAgencyTest() {
    }

    public static void main(String[] args) {
        NewsSubscriber allNews = new NewsSubscriber("All News");
        NewsSubscriber techOnly = new NewsSubscriber("Tech Only", Set.of(NewsCategory.TECHNOLOGY));
        NewsSubscriber sportsOnly = new NewsSubscriber("Sports Only", Set.of(NewsCategory.SPORTS));

        try (NewsAgency agency = new NewsAgency(3)) {
            check(agency.subscribe(allNews), "First subscription should succeed");
            check(agency.subscribe(techOnly), "Tech subscription should succeed");
            check(agency.subscribe(sportsOnly), "Sports subscription should succeed");
            check(!agency.subscribe(allNews), "Duplicate subscription should be ignored");
            check(agency.getSubscriberCount() == 3, "There should be three subscribers");

            NewsArticle technology = NewsArticle.breaking(
                    "Technology update", "New platform released.", NewsCategory.TECHNOLOGY);
            agency.publishBreakingNews(technology).join();

            check(allNews.getReceivedArticles().size() == 1, "All News should receive technology");
            check(techOnly.getReceivedArticles().size() == 1, "Tech Only should receive technology");
            check(sportsOnly.getReceivedArticles().isEmpty(), "Sports Only should not receive technology");

            sportsOnly.updatePreferences(Set.of(NewsCategory.WEATHER));
            NewsArticle weather = NewsArticle.breaking(
                    "Weather alert", "Heavy rain is expected.", NewsCategory.WEATHER);
            agency.publishBreakingNews(weather).join();

            check(allNews.getReceivedArticles().size() == 2, "All News should receive weather");
            check(techOnly.getReceivedArticles().size() == 1, "Tech Only should not receive weather");
            check(sportsOnly.getReceivedArticles().size() == 1, "Updated preference should receive weather");

            check(agency.unsubscribe(allNews), "Unsubscription should succeed");
            agency.publishBreakingNews(NewsArticle.breaking(
                    "Business update", "Markets closed higher.", NewsCategory.BUSINESS)).join();
            check(allNews.getReceivedArticles().size() == 2, "Unsubscribed user should receive nothing new");
            check(agency.getSubscriberCount() == 2, "Two subscribers should remain");
        }

        System.out.println("All NewsAgency tests passed.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}


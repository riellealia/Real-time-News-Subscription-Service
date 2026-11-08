package com.media.news;

import java.util.Set;

/** Demonstrates subscriptions, preference changes, and unsubscription. */
public final class NewsSubscriptionApplication {
    private NewsSubscriptionApplication() {
    }

    public static void main(String[] args) {
        NewsSubscriber alex = new NewsSubscriber(
                "Alex", Set.of(NewsCategory.TECHNOLOGY, NewsCategory.BUSINESS));
        NewsSubscriber bea = new NewsSubscriber(
                "Bea", Set.of(NewsCategory.SPORTS));
        NewsSubscriber carlo = new NewsSubscriber("Carlo"); // Empty set means all categories.

        try (NewsAgency agency = new NewsAgency()) {
            agency.subscribe(alex);
            agency.subscribe(bea);
            agency.subscribe(carlo);

            agency.publishBreakingNews(NewsArticle.breaking(
                    "New AI platform announced",
                    "A media technology company announced a new AI platform.",
                    NewsCategory.TECHNOLOGY)).join();

            bea.updatePreferences(Set.of(NewsCategory.SPORTS, NewsCategory.WEATHER));
            agency.unsubscribe(carlo);

            agency.publishBreakingNews(NewsArticle.breaking(
                    "Storm warning issued",
                    "Residents are advised to monitor local weather updates.",
                    NewsCategory.WEATHER)).join();
        }
    }
}


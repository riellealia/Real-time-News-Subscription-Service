package com.media.news;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

/** Concrete observer whose category preferences can change at runtime. */
public final class NewsSubscriber implements Subscriber {
    private final String name;
    private final AtomicReference<Set<NewsCategory>> preferredCategories;
    private final List<NewsArticle> receivedArticles = new CopyOnWriteArrayList<>();

    public NewsSubscriber(String name) {
        this(name, Set.of());
    }

    public NewsSubscriber(String name, Set<NewsCategory> preferredCategories) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Subscriber name must not be blank.");
        }
        this.name = name;
        this.preferredCategories = new AtomicReference<>(copyPreferences(preferredCategories));
    }

    @Override
    public String getName() {
        return name;
    }

    public void updatePreferences(Set<NewsCategory> categories) {
        preferredCategories.set(copyPreferences(categories));
    }

    public Set<NewsCategory> getPreferredCategories() {
        return preferredCategories.get();
    }

    @Override
    public boolean isInterestedIn(NewsCategory category) {
        Set<NewsCategory> categories = preferredCategories.get();
        return categories.isEmpty() || categories.contains(category);
    }

    @Override
    public void onBreakingNews(NewsArticle article) {
        receivedArticles.add(article);
        System.out.printf("[%s] BREAKING %s: %s%n", name, article.category(), article.headline());
    }

    public List<NewsArticle> getReceivedArticles() {
        return List.copyOf(receivedArticles);
    }

    private static Set<NewsCategory> copyPreferences(Set<NewsCategory> categories) {
        if (categories == null) {
            throw new IllegalArgumentException("Preferences must not be null.");
        }
        return Set.copyOf(categories);
    }
}


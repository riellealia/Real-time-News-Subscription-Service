package com.media.news;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** Subject/publisher that broadcasts breaking news to registered observers. */
public final class NewsAgency implements AutoCloseable {
    private final Set<Subscriber> subscribers = ConcurrentHashMap.newKeySet();
    private final ExecutorService notificationExecutor;

    public NewsAgency() {
        this(Math.max(2, Runtime.getRuntime().availableProcessors()));
    }

    public NewsAgency(int workerThreads) {
        if (workerThreads < 1) {
            throw new IllegalArgumentException("Worker thread count must be at least 1.");
        }
        notificationExecutor = Executors.newFixedThreadPool(workerThreads);
    }

    public boolean subscribe(Subscriber subscriber) {
        if (subscriber == null) {
            throw new IllegalArgumentException("Subscriber must not be null.");
        }
        return subscribers.add(subscriber);
    }

    public boolean unsubscribe(Subscriber subscriber) {
        return subscriber != null && subscribers.remove(subscriber);
    }

    public int getSubscriberCount() {
        return subscribers.size();
    }

    public CompletableFuture<Void> publishBreakingNews(NewsArticle article) {
        if (article == null) {
            throw new IllegalArgumentException("Article must not be null.");
        }

        List<Subscriber> interestedSubscribers = new ArrayList<>();
        for (Subscriber subscriber : subscribers) {
            if (subscriber.isInterestedIn(article.category())) {
                interestedSubscribers.add(subscriber);
            }
        }

        CompletableFuture<?>[] notifications = interestedSubscribers.stream()
                .map(subscriber -> CompletableFuture.runAsync(
                        () -> subscriber.onBreakingNews(article), notificationExecutor))
                .toArray(CompletableFuture[]::new);

        return CompletableFuture.allOf(notifications);
    }

    @Override
    public void close() {
        notificationExecutor.shutdown();
        try {
            if (!notificationExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                notificationExecutor.shutdownNow();
            }
        } catch (InterruptedException interrupted) {
            notificationExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}


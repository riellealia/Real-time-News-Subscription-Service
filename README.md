# Real-time News Subscription Service

## Problem Statement

**Background:** You're tasked with designing a real-time news subscription service for a media company. The system needs to notify subscribers about breaking news as soon as it's published by the news agency.

**Scenario Description:** The system comprises two main components: `NewsAgency` and `Subscriber`.

- **NewsAgency:** Represents the central hub for publishing news. It maintains a list of subscribers and notifies them whenever new news is available.
- **Subscriber:** Represents users or entities subscribed to the news service. Subscribers receive immediate notifications about any breaking news published by the agency.

**Requirements:**

1. **Subscription Management:**
   - Users should be able to subscribe to the news service.
   - Subscribers should be notified promptly when new news is published.
2. **Dynamic Subscription Updates:**
   - The system should allow for dynamic subscription updates. Subscribers can subscribe, unsubscribe, or modify their preferences without disrupting other subscribers.
3. **Decoupled Communication:**
   - Ensure that the communication between the news agency and subscribers is decoupled. Subscribers shouldn't directly request news updates but should be notified by the news agency when new information is available.

**Expected Behavior:**

- When the news agency publishes breaking news, all subscribed users should receive immediate updates.
- Subscribers can join or leave the service without affecting the delivery of news to other subscribers.
- The system should provide flexibility for future enhancements, such as personalized subscriptions or categorization of news updates.

**Constraints:**

- The system should be scalable to handle a growing number of subscribers without compromising performance.
- Ensure that the implementation follows object-oriented design principles and promotes loose coupling between the news agency and subscribers.

## Design Pattern

This solution uses the **Observer design pattern**:

- `NewsAgency` is the subject or publisher.
- `Subscriber` is the observer interface.
- `NewsSubscriber` is a concrete observer.
- Subscribers register or unregister without changing the agency or affecting other subscribers.
- The agency pushes matching news to subscribers instead of subscribers repeatedly requesting updates.

Notifications are dispatched asynchronously through a worker pool. The subscriber collection is thread-safe, and each subscriber can update category preferences while notifications are running.

## UML Class Diagram

```mermaid
classDiagram
    class Subscriber {
        <<interface>>
        +getName() String
        +isInterestedIn(category: NewsCategory) boolean
        +onBreakingNews(article: NewsArticle) void
    }

    class NewsAgency {
        -subscribers: Set~Subscriber~
        -notificationExecutor: ExecutorService
        +subscribe(subscriber: Subscriber) boolean
        +unsubscribe(subscriber: Subscriber) boolean
        +publishBreakingNews(article: NewsArticle) CompletableFuture~Void~
        +getSubscriberCount() int
        +close() void
    }

    class NewsSubscriber {
        -name: String
        -preferredCategories: AtomicReference~Set~NewsCategory~~
        -receivedArticles: List~NewsArticle~
        +updatePreferences(categories: Set~NewsCategory~) void
        +isInterestedIn(category: NewsCategory) boolean
        +onBreakingNews(article: NewsArticle) void
        +getReceivedArticles() List~NewsArticle~
    }

    class NewsArticle {
        <<record>>
        +headline: String
        +content: String
        +category: NewsCategory
        +publishedAt: Instant
    }

    class NewsCategory {
        <<enumeration>>
        GENERAL
        BUSINESS
        TECHNOLOGY
        SPORTS
        ENTERTAINMENT
        WEATHER
    }

    class NewsSubscriptionApplication {
        +main(args: String[]) void
    }

    Subscriber <|.. NewsSubscriber
    NewsAgency o-- Subscriber
    NewsAgency --> NewsArticle
    NewsSubscriber --> NewsArticle
    NewsArticle --> NewsCategory
    NewsSubscriptionApplication --> NewsAgency
    NewsSubscriptionApplication --> NewsSubscriber
```

## Project Structure

```text
src/main/java/com/media/news/
  NewsAgency.java
  NewsArticle.java
  NewsCategory.java
  NewsSubscriber.java
  NewsSubscriptionApplication.java
  Subscriber.java
src/test/java/com/media/news/
  NewsAgencyTest.java
```

## Compile and Run

```bash
javac -d out src/main/java/com/media/news/*.java src/test/java/com/media/news/NewsAgencyTest.java
java -cp out com.media.news.NewsAgencyTest
java -cp out com.media.news.NewsSubscriptionApplication
```


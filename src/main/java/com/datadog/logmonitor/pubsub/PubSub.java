package com.datadog.logmonitor.pubsub;

import com.datadog.logmonitor.pubsub.subscriber.Subscriber;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import static com.google.common.collect.Multimaps.synchronizedMultimap;

public class PubSub {

    private static final int DEFAULT_QUEUE_MAX_SIZE = 10_000;

    private Multimap<Topic, Subscriber> subscribersPerTopic = synchronizedMultimap(HashMultimap.create());

    private BlockingQueue<Message> messages;

    public PubSub() {
        this(DEFAULT_QUEUE_MAX_SIZE);
    }

    public PubSub(int queueMaxSize) {
        messages = new LinkedBlockingQueue<>(queueMaxSize);
        Executors.newSingleThreadExecutor().execute(this::distribute);
    }

    public void publish(Message<?> message) {
        try {
            messages.put(message);
        } catch (InterruptedException e) {
            System.err.printf("Failed to publish message : '%s' due to '%s'\n", message, e.getMessage());
            System.exit(1);
        }
    }

    public void subscribe(Subscriber subscriber, Topic... topics) {
        Arrays.stream(topics).forEach(topic -> subscribersPerTopic.put(topic, subscriber));
    }

    public void unsubscribe(Subscriber subscriber, Topic... topics) {
        Arrays.stream(topics).forEach(topic -> subscribersPerTopic.remove(topic, subscriber));
    }

    private void distribute() {
        try {
            while (true) {
                Message<?> message = messages.take();
                subscribersPerTopic.get(message.getTopic())
                        .forEach(subscriber -> subscriber.onMessage(message));
            }
        } catch (InterruptedException e) {
            System.err.printf("Failed to poll queue due to '%s'\n", e.getMessage());
            System.exit(1);
        }
    }

}

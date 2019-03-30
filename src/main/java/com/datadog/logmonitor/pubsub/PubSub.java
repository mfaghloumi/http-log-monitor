package com.datadog.logmonitor.pubsub;

import com.datadog.logmonitor.pubsub.subscriber.Subscriber;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import static com.google.common.collect.Multimaps.synchronizedMultimap;

public class PubSub {

    private static final int DEFAULT_QUEUE_MAX_SIZE = 10_000;

    private Multimap<String, Subscriber> subscribersPerTopic = synchronizedMultimap(HashMultimap.create());

    private BlockingQueue<Message> messages;

    private Executor executor = Executors.newSingleThreadExecutor();

    public PubSub() {
        this(DEFAULT_QUEUE_MAX_SIZE);
    }

    public PubSub(int queueMaxSize) {
        messages = new LinkedBlockingQueue<>(queueMaxSize);
        executor.execute(this::distribute);
    }

    public void publish(Message<?> message) {
        messages.add(message);
    }

    public void subscribe(Subscriber subscriber, String... topics) {
        Arrays.stream(topics).forEach(topic -> subscribersPerTopic.put(topic, subscriber));
    }

    public void unsubscribe(Subscriber subscriber, String... topics) {
        Arrays.stream(topics).forEach(topic -> subscribersPerTopic.remove(topic, subscriber));
    }

    //TODO Manage exceptions
    private void distribute() {
        try {
            while (true) {
                Message<?> message = messages.take();
                subscribersPerTopic.get(message.getTopic())
                        .forEach(subscriber -> subscriber.onMessage(message));
            }
        } catch (InterruptedException e) {
            System.exit(1);
        }
    }

}

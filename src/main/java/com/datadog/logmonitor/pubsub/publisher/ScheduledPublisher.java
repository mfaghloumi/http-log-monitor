package com.datadog.logmonitor.pubsub.publisher;

import com.datadog.logmonitor.pubsub.Message;
import com.datadog.logmonitor.pubsub.PubSub;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class ScheduledPublisher implements Publisher {

    private final PubSub pubSub;

    private ScheduledExecutorService executor;

    public ScheduledPublisher(PubSub pubSub,
                              Supplier<Message<?>> supplier,
                              long initialDelay,
                              long period,
                              TimeUnit unit) {
        this.pubSub = pubSub;
        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.executor.scheduleAtFixedRate(() -> publish(supplier.get()), initialDelay, period, unit);
    }

    @Override
    public void publish(Message<?> message) {
        pubSub.publish(message);
    }

}

package com.datadog.httplogmonitor.pubsub.publisher;

import com.datadog.httplogmonitor.pubsub.Message;
import com.datadog.httplogmonitor.pubsub.PubSub;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class ScheduledPublisher implements Publisher {

    private final PubSub pubSub;

    public ScheduledPublisher(PubSub pubSub,
                              Supplier<Message<?>> supplier,
                              long initialDelay,
                              long period,
                              TimeUnit unit) {
        this.pubSub = pubSub;
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(() -> publish(supplier.get()), initialDelay, period, unit);
    }

    @Override
    public void publish(Message<?> message) {
        pubSub.publish(message);
    }

}

package com.datadog.httplogmonitor.pubsub.publisher;

import com.datadog.httplogmonitor.pubsub.Message;

@FunctionalInterface
public interface Publisher {

    void publish(Message<?> message);

}
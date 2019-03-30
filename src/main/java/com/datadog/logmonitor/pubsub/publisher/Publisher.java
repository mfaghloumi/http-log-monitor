package com.datadog.logmonitor.pubsub.publisher;

import com.datadog.logmonitor.pubsub.Message;

@FunctionalInterface
public interface Publisher {

    void publish(Message<?> message);

}
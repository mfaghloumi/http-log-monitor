package com.datadog.logmonitor.pubsub.subscriber;


import com.datadog.logmonitor.pubsub.Message;

@FunctionalInterface
public interface Subscriber {

    void onMessage(Message<?> message);

}
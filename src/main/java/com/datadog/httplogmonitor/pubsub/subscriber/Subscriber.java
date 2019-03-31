package com.datadog.httplogmonitor.pubsub.subscriber;


import com.datadog.httplogmonitor.pubsub.Message;

@FunctionalInterface
public interface Subscriber {

    void onMessage(Message<?> message);

}
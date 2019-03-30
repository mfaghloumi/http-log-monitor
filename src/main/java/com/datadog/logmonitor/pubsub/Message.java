package com.datadog.logmonitor.pubsub;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message<T> {

    private String topic;

    private T payload;

    Message(String topic) {
        this.topic = topic;
    }
}

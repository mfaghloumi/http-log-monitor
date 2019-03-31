package com.datadog.httplogmonitor.pubsub;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message<T> {

    private Topic topic;

    private T payload;

    Message(Topic topic) {
        this.topic = topic;
    }
}

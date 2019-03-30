package com.datadog.logmonitor.pubsub;

import com.datadog.logmonitor.domain.Hit;
import lombok.experimental.UtilityClass;

import static com.datadog.logmonitor.pubsub.Topics.NEW_HIT;

@UtilityClass
public class Messages {

    public Message<Hit> newHitMessage(Hit hit) {
        return new Message<>(NEW_HIT, hit);
    }
}

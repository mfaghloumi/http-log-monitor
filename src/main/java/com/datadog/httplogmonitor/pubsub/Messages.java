package com.datadog.httplogmonitor.pubsub;

import com.datadog.httplogmonitor.domain.Hit;
import lombok.experimental.UtilityClass;

import static com.datadog.httplogmonitor.pubsub.Topic.*;

@UtilityClass
public class Messages {

    public Message<Hit> newHitMessage(Hit hit) {
        return new Message<>(NEW_HIT, hit);
    }

    public Message<?> aggregatePerSecondMessage() {
        return new Message<>(AGGREGATE_PER_SECOND);
    }

    public Message<?> aggregatePerSectionMessage() {
        return new Message<>(AGGREGATE_PER_SECTION);
    }

}

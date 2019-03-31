package com.datadog.httplogmonitor.pubsub.subscriber;

import com.datadog.httplogmonitor.domain.Hit;
import com.datadog.httplogmonitor.pubsub.Message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class HitsPerSectionSubscriber implements Subscriber {

    private ConcurrentMap<String, Long> hitsPerSection = new ConcurrentHashMap<>();

    @Override
    public void onMessage(Message<?> message) {
        switch (message.getTopic()) {
            case NEW_HIT:
                updateHits(message);
                return;
            case AGGREGATE_PER_SECTION:
                aggregateTopSections();
        }
    }

    private void updateHits(Message<?> message) {
        String section = ((Hit) message.getPayload()).getSection();
        hitsPerSection.compute(section, (oldSection, oldCount) -> {
            if (oldCount == null) {
                return 1L;
            }
            return oldCount + 1L;
        });
    }

    private void aggregateTopSections() {
        if (hitsPerSection.isEmpty()) {
            return;
        }
        System.out.println("### Top 10 sections :");
        hitsPerSection.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .forEach(e -> System.out.printf("\t - %s:\t%s\n", e.getKey(), e.getValue()));
    }

}

package com.datadog.httplogmonitor.pubsub.subscriber;

import com.datadog.httplogmonitor.domain.Hit;
import com.datadog.httplogmonitor.domain.SectionStatistics;
import com.datadog.httplogmonitor.pubsub.Message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class HitsPerSectionSubscriber implements Subscriber {

    private ConcurrentMap<String, SectionStatistics> hitsPerSection = new ConcurrentHashMap<>();

    private static final int DEFAULT_TOP_N = 10;

    private int topN;

    public HitsPerSectionSubscriber(int topN) {
        this.topN = topN;
    }

    public HitsPerSectionSubscriber() {
        this(DEFAULT_TOP_N);
    }

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
        hitsPerSection.compute(section, (oldSection, oldStats) -> {
            if (oldStats == null) {
                return new SectionStatistics().update((Hit) message.getPayload());
            }
            return oldStats.update((Hit) message.getPayload());
        });
    }

    private void aggregateTopSections() {
        if (hitsPerSection.isEmpty()) {
            return;
        }
        System.out.println("### Top " + topN + " sections :");
        hitsPerSection.entrySet()
                .stream()
                .sorted(Map.Entry.<String, SectionStatistics>comparingByValue().reversed())
                .limit(topN)
                .forEach(e -> System.out.printf("\t - %s:\t%s\n", e.getKey(), e.getValue()));
    }

}

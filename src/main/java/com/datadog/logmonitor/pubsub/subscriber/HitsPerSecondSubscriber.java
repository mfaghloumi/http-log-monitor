package com.datadog.logmonitor.pubsub.subscriber;

import com.datadog.logmonitor.pubsub.Message;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.time.LocalDateTime;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static com.datadog.logmonitor.pubsub.Topics.AGGREGATE_PER_SECOND;
import static com.datadog.logmonitor.pubsub.Topics.NEW_HIT;

public class HitsPerSecondSubscriber implements Subscriber {

    private static final String HIGH_TRAFFIC_LOG_PATTERN = "### High traffic generated an alert - hits = %s, triggered at %s%n";

    private static final String TRAFFIC_RECOVERED_LOG_PATTERN = "### High traffic recovered - hits = %s, recovered at %s%n";

    private AtomicLong hitsPerSecond = new AtomicLong(0);

    private Queue<Long> hitsPerSecondForLastTwoMinutes = new CircularFifoQueue<>(120);

    private AtomicBoolean alert = new AtomicBoolean();

    private final double alertThreshold;

    public HitsPerSecondSubscriber(double threshold) {
        this.alertThreshold = threshold;
    }

    @Override
    public void onMessage(Message<?> message) {
        switch (message.getTopic()) {
            case NEW_HIT:
                hitsPerSecond.incrementAndGet();
                return;
            case AGGREGATE_PER_SECOND:
                aggregatePerSecond();
        }
    }

    //TODO Race condition on Circular Queue
    private void aggregatePerSecond() {
        hitsPerSecondForLastTwoMinutes.add(hitsPerSecond.getAndSet(0));
        double averageHitsPerTwoMinutes = hitsPerSecondForLastTwoMinutes
                .stream()
                .mapToDouble(x -> x)
                .average()
                .orElse(0.0);
        if (averageHitsPerTwoMinutes > alertThreshold && alert.compareAndSet(false, true)) {
            System.out.printf(HIGH_TRAFFIC_LOG_PATTERN,
                    averageHitsPerTwoMinutes,
                    LocalDateTime.now());
        } else if (averageHitsPerTwoMinutes < alertThreshold && alert.compareAndSet(true, false)) {
            System.out.printf(TRAFFIC_RECOVERED_LOG_PATTERN,
                    averageHitsPerTwoMinutes,
                    LocalDateTime.now().toString());
        }
    }

}

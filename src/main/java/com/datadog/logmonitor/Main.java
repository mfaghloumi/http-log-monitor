package com.datadog.logmonitor;

import com.datadog.logmonitor.pubsub.Messages;
import com.datadog.logmonitor.pubsub.PubSub;
import com.datadog.logmonitor.pubsub.publisher.HitPublisher;
import com.datadog.logmonitor.pubsub.publisher.ScheduledPublisher;
import com.datadog.logmonitor.pubsub.subscriber.HitsPerSecondSubscriber;
import org.apache.commons.io.input.Tailer;

import java.io.File;

import static com.datadog.logmonitor.pubsub.Topics.AGGREGATE_PER_SECOND;
import static com.datadog.logmonitor.pubsub.Topics.NEW_HIT;
import static java.util.concurrent.TimeUnit.SECONDS;

//TODO Statistics
public class Main {

    private static final String DEFAULT_ACCESS_LOG = "access.log";

    private static final int DELAY_MILLIS = 500;

    private static final double DEFAULT_THRESHOLD = 10;

    //TODO
    public static void main(String[] args) {
        PubSub pubsub = new PubSub();

        ScheduledPublisher aggregatePerSecondPublisher = new ScheduledPublisher(pubsub,
                Messages::aggregatePerSecondMessage, 0, 1, SECONDS);
        HitPublisher hitPublisher = new HitPublisher(pubsub);
        Tailer tailer = new Tailer(new File(DEFAULT_ACCESS_LOG), hitPublisher, DELAY_MILLIS, true);

        pubsub.subscribe(new HitsPerSecondSubscriber(DEFAULT_THRESHOLD), NEW_HIT, AGGREGATE_PER_SECOND);

        tailer.run();
    }


}

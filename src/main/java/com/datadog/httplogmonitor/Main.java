package com.datadog.httplogmonitor;

import com.datadog.httplogmonitor.pubsub.Messages;
import com.datadog.httplogmonitor.pubsub.PubSub;
import com.datadog.httplogmonitor.pubsub.publisher.HitPublisher;
import com.datadog.httplogmonitor.pubsub.publisher.ScheduledPublisher;
import com.datadog.httplogmonitor.pubsub.subscriber.HitsPerSecondSubscriber;
import com.datadog.httplogmonitor.pubsub.subscriber.HitsPerSectionSubscriber;
import com.datadog.httplogmonitor.utils.CommandUtils;
import com.datadog.httplogmonitor.utils.CommandUtils.Parameters;
import org.apache.commons.io.input.Tailer;

import java.io.File;

import static com.datadog.httplogmonitor.pubsub.Topic.*;
import static java.util.concurrent.TimeUnit.SECONDS;

public class Main {

    private static final int DELAY_MILLIS = 500;

    public static void main(String[] args) {

        Parameters parameters = CommandUtils.parse(args);

        PubSub pubsub = new PubSub();

        new ScheduledPublisher(pubsub, Messages::aggregatePerSectionMessage, 0, 10, SECONDS);
        new ScheduledPublisher(pubsub, Messages::aggregatePerSecondMessage, 0, 1, SECONDS);
        HitPublisher hitPublisher = new HitPublisher(pubsub);
        Tailer tailer = new Tailer(new File(parameters.getFilename()), hitPublisher, DELAY_MILLIS, true);

        pubsub.subscribe(new HitsPerSecondSubscriber(parameters.getThreshold()), NEW_HIT, AGGREGATE_PER_SECOND);
        pubsub.subscribe(new HitsPerSectionSubscriber(), NEW_HIT, AGGREGATE_PER_SECTION);

        tailer.run();

    }


}

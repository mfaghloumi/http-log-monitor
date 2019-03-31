package com.datadog.logmonitor;

import com.datadog.logmonitor.pubsub.Messages;
import com.datadog.logmonitor.pubsub.PubSub;
import com.datadog.logmonitor.pubsub.publisher.HitPublisher;
import com.datadog.logmonitor.pubsub.publisher.ScheduledPublisher;
import com.datadog.logmonitor.pubsub.subscriber.HitsPerSecondSubscriber;
import com.datadog.logmonitor.pubsub.subscriber.HitsPerSectionSubscriber;
import com.datadog.logmonitor.utils.CommandUtils;
import com.datadog.logmonitor.utils.CommandUtils.Parameters;
import org.apache.commons.io.input.Tailer;

import java.io.File;

import static com.datadog.logmonitor.pubsub.Topics.*;
import static java.util.concurrent.TimeUnit.SECONDS;

//TODO Statistics
//TODO Builder for P/S Framework
public class Main {

    private static final int DELAY_MILLIS = 500;

    public static void main(String[] args) {

        Parameters parameters = CommandUtils.parse(args);

        PubSub pubsub = new PubSub();

        ScheduledPublisher aggregatePerSectionPublisher = new ScheduledPublisher(pubsub,
                Messages::aggregatePerSectionMessage, 0, 10, SECONDS);
        ScheduledPublisher aggregatePerSecondPublisher = new ScheduledPublisher(pubsub,
                Messages::aggregatePerSecondMessage, 0, 1, SECONDS);
        HitPublisher hitPublisher = new HitPublisher(pubsub);
        Tailer tailer = new Tailer(new File(parameters.getFilename()), hitPublisher, DELAY_MILLIS, true);

        pubsub.subscribe(new HitsPerSecondSubscriber(parameters.getThreshold()), NEW_HIT, AGGREGATE_PER_SECOND);
        pubsub.subscribe(new HitsPerSectionSubscriber(), NEW_HIT, AGGREGATE_PER_SECTION);

        tailer.run();

    }


}

package com.datadog.logmonitor;

import com.datadog.logmonitor.pubsub.PubSub;
import com.datadog.logmonitor.pubsub.publisher.HitPublisher;
import org.apache.commons.io.input.Tailer;

import java.io.File;

import static com.datadog.logmonitor.pubsub.Topics.NEW_HIT;

//TODO Statistics
public class Main {

    private static final String ACCESS_LOG = "access.log";

    private static final int DELAY_MILLIS = 500;

    public static void main(String[] args) {

        PubSub pubsub = new PubSub();
        HitPublisher hitPublisher = new HitPublisher(pubsub);
        Tailer tailer = new Tailer(new File(ACCESS_LOG), hitPublisher, DELAY_MILLIS, true);
        pubsub.subscribe(System.out::println, NEW_HIT);
        tailer.run();

    }


}

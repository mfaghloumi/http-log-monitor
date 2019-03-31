package com.datadog.logmonitor.pubsub.publisher;

import com.datadog.logmonitor.transformer.HitTransformer;
import com.datadog.logmonitor.pubsub.Message;
import com.datadog.logmonitor.pubsub.Messages;
import com.datadog.logmonitor.pubsub.PubSub;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;

import java.util.Optional;

public class HitPublisher implements TailerListener, Publisher {

    private final PubSub pubSub;

    public HitPublisher(PubSub pubSub) {
        this.pubSub = pubSub;
    }

    @Override
    public void init(Tailer tailer) {
        //Nothing to do
    }

    @Override
    public void fileNotFound() {
        System.err.println("File does not exist");
        System.exit(1);
    }

    @Override
    public void fileRotated() {
        System.err.println("Log file rotation not managed");
        System.exit(1);
    }

    @Override
    public void handle(String line) {
        Optional.ofNullable(line)
                .map(HitTransformer::toHit)
                .map(Messages::newHitMessage)
                .ifPresent(this::publish);
    }

    @Override
    public void handle(Exception ex) {
        System.err.printf("Issue when processing the log file:\n\t%s\n", ex.getMessage());
        System.exit(1);
    }

    @Override
    public void publish(Message<?> message) {
        pubSub.publish(message);
    }

}

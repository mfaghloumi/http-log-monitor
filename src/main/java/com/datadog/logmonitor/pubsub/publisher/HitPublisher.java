package com.datadog.logmonitor.pubsub.publisher;

import com.datadog.logmonitor.HitTransformer;
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
        //TODO
    }

    @Override
    public void fileNotFound() {
        //TODO
    }

    @Override
    public void fileRotated() {
        //TODO
    }

    @Override
    public void handle(String line) {
        Optional.of(line)
                .map(HitTransformer::toHit)
                .map(Messages::newHitMessage)
                .ifPresent(this::publish);
    }

    @Override
    public void handle(Exception ex) {
        //TODO
    }

    @Override
    public void publish(Message<?> message) {
        pubSub.publish(message);
    }

}

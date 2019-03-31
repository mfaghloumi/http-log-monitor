package com.datadog.httplogmonitor.pubsub.subscriber;

import com.datadog.httplogmonitor.domain.Hit;
import com.datadog.httplogmonitor.pubsub.Messages;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.stream.IntStream;

import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;

public class HitsPerSecondSubscriberTest {

    private static final int THRESHOLD = 10;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private final PrintStream originalOut = System.out;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void alert_when_threshold_is_triggered() {
        HitsPerSecondSubscriber subscriber = new HitsPerSecondSubscriber(THRESHOLD);
        IntStream.rangeClosed(1, 12).forEach(i -> subscriber.onMessage(Messages.newHitMessage(new Hit("A"))));
        subscriber.onMessage(Messages.aggregatePerSecondMessage());
        assertThat("A high traffic alert should have been generated",
                outContent.toString(), startsWith("### High traffic generated an alert - hits = 12.0, triggered at"));
    }

    @Test
    public void alert_when_threshold_is_recovered() throws Exception {
        HitsPerSecondSubscriber subscriber = new HitsPerSecondSubscriber(THRESHOLD);
        IntStream.rangeClosed(1, 12).forEach(i -> subscriber.onMessage(Messages.newHitMessage(new Hit("A"))));
        subscriber.onMessage(Messages.aggregatePerSecondMessage());
        outContent.reset();
        Thread.sleep(1_000);
        subscriber.onMessage(Messages.aggregatePerSecondMessage());
        assertThat("A traffic recovered alert should have been generated",
                outContent.toString(), startsWith("### High traffic recovered - hits = 6.0, recovered at"));
    }

}
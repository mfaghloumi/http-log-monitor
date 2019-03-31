package com.datadog.httplogmonitor.pubsub.subscriber;

import com.datadog.httplogmonitor.domain.Hit;
import com.datadog.httplogmonitor.pubsub.Messages;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class HitsPerSectionSubscriberTest {

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
    public void display_ordered_top_2_section() {
        HitsPerSectionSubscriber subscriber = new HitsPerSectionSubscriber(2);
        IntStream.rangeClosed(0, 30).forEach(i ->
                subscriber.onMessage(Messages.newHitMessage(new Hit("user", "A", 1))));
        IntStream.rangeClosed(0, 20).forEach(i ->
                subscriber.onMessage(Messages.newHitMessage(new Hit("user", "B", 1))));
        IntStream.rangeClosed(0, 30).forEach(i ->
                subscriber.onMessage(Messages.newHitMessage(new Hit("user", "C", 1))));
        subscriber.onMessage(Messages.aggregatePerSectionMessage());
        String top10 = "### Top 2 sections :\n" +
                "\t - A:\t[ Top 3 users = [user], totalContentSize=31, hitsCount=31 ]\n" +
                "\t - C:\t[ Top 3 users = [user], totalContentSize=31, hitsCount=31 ]\n";
        assertEquals("Top 10 sections should be logged from the top visited to the least visited",
                outContent.toString(), top10);
    }

    @Test
    public void display_nothing_when_no_hit() {
        HitsPerSectionSubscriber subscriber = new HitsPerSectionSubscriber();
        subscriber.onMessage(Messages.aggregatePerSectionMessage());
        assertEquals("No message should be displayed when no hit was recorded", outContent.toString(), "");
    }

}
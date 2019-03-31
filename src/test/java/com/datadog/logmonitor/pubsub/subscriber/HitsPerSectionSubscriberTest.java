package com.datadog.logmonitor.pubsub.subscriber;

import com.datadog.logmonitor.domain.Hit;
import com.datadog.logmonitor.pubsub.Messages;
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
    public void display_ordered_top_10_section() {
        HitsPerSectionSubscriber subscriber = new HitsPerSectionSubscriber();
        IntStream.rangeClosed(0, 100).forEach(i -> subscriber.onMessage(Messages.newHitMessage(new Hit("A"))));
        IntStream.rangeClosed(0, 90).forEach(i -> subscriber.onMessage(Messages.newHitMessage(new Hit("B"))));
        IntStream.rangeClosed(0, 80).forEach(i -> subscriber.onMessage(Messages.newHitMessage(new Hit("C"))));
        IntStream.rangeClosed(0, 70).forEach(i -> subscriber.onMessage(Messages.newHitMessage(new Hit("D"))));
        IntStream.rangeClosed(0, 60).forEach(i -> subscriber.onMessage(Messages.newHitMessage(new Hit("E"))));
        IntStream.rangeClosed(0, 40).forEach(i -> subscriber.onMessage(Messages.newHitMessage(new Hit("F"))));
        IntStream.rangeClosed(0, 30).forEach(i -> subscriber.onMessage(Messages.newHitMessage(new Hit("G"))));
        IntStream.rangeClosed(0, 20).forEach(i -> subscriber.onMessage(Messages.newHitMessage(new Hit("H"))));
        IntStream.rangeClosed(0, 10).forEach(i -> subscriber.onMessage(Messages.newHitMessage(new Hit("I"))));
        IntStream.rangeClosed(0, 1).forEach(i -> subscriber.onMessage(Messages.newHitMessage(new Hit("J"))));
        IntStream.rangeClosed(0, 100).forEach(i -> subscriber.onMessage(Messages.newHitMessage(new Hit("Z"))));
        subscriber.onMessage(Messages.aggregatePerSectionMessage());
        String top10 = "### Top 10 sections :\n" +
                "\t - A:\t101\n" +
                "\t - Z:\t101\n" +
                "\t - B:\t91\n" +
                "\t - C:\t81\n" +
                "\t - D:\t71\n" +
                "\t - E:\t61\n" +
                "\t - F:\t41\n" +
                "\t - G:\t31\n" +
                "\t - H:\t21\n" +
                "\t - I:\t11\n";
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
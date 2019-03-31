package com.datadog.httplogmonitor.domain;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Data
public class SectionStatistics implements Comparable<SectionStatistics> {

    private ConcurrentMap<String, Long> hitsPerUser = new ConcurrentHashMap<>();

    private AtomicLong totalContentSize = new AtomicLong(0);

    private AtomicLong hitsCount = new AtomicLong(0);

    public SectionStatistics update(Hit hit) {
        hitsCount.incrementAndGet();
        totalContentSize.addAndGet(hit.getContentSize());
        hitsPerUser.compute(hit.getUser(), (oldSection, oldCount) -> {
            if (oldCount == null) {
                return 1L;
            }
            return oldCount + 1L;
        });
        return this;
    }

    @Override
    public int compareTo(SectionStatistics that) {
        if (this == that) return 0;
        return Long.compare(hitsCount.get(), that.getHitsCount().get());
    }

    @Override
    public String toString() {
        String topNUsers = hitsPerUser.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(", ", "[", "]"));
        return "[ Top 3 users = " + topNUsers +
                ", totalContentSize=" + totalContentSize +
                ", hitsCount=" + hitsCount +
                " ]";
    }
}

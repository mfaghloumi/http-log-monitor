package com.datadog.logmonitor;

import com.datadog.logmonitor.domain.Hit;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HitTransformer {

    private static final Pattern SECTION_PATTERN = Pattern.compile(".*\"GET /([^/ ]+).*");

    public static Hit toHit(String log) {
        return Optional.ofNullable(log)
                .map(SECTION_PATTERN::matcher)
                .filter(Matcher::find)
                .map(matcher -> matcher.group(1))
                .map(Hit::new)
                .orElseThrow(() -> new RuntimeException("Failed to extract to transform log line to a Hit"));
    }

}

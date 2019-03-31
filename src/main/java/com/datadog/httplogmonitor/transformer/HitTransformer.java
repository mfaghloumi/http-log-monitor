package com.datadog.httplogmonitor.transformer;

import com.datadog.httplogmonitor.domain.Hit;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HitTransformer {

    private static final Pattern SECTION_PATTERN = Pattern.compile(".* - (\\S+) .*\"(?:GET|POST) /([^/ ]+).*\" (?:[0-9]{3}) ([0-9]+).*");

    @Nullable
    public static Hit toHit(@Nullable String log) {
        Optional<Hit> hit;
        try {
            hit = Optional.ofNullable(log)
                    .map(SECTION_PATTERN::matcher)
                    .filter(Matcher::find)
                    .map(matcher -> new Hit(matcher.group(1), matcher.group(2), Long.parseLong(matcher.group(3))));
        } catch (Exception e) {
            System.err.printf("Failed to transform log line to a Hit : '%s' reason : '%s'\n", log, e.getMessage());
            return null;
        }
        if (hit.isPresent()) {
            return hit.get();
        }
        System.err.printf("Failed to transform log line to a Hit : '%s'\n", log);
        return null;
    }

}

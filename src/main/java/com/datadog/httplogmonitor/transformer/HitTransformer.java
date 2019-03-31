package com.datadog.httplogmonitor.transformer;

import com.datadog.httplogmonitor.domain.Hit;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HitTransformer {

    private static final Pattern SECTION_PATTERN = Pattern.compile(".*\"(?:GET|POST) /([^/ ]+).*");

    @Nullable
    public static Hit toHit(@Nullable String log) {
        Optional<Hit> hit = Optional.ofNullable(log)
                .map(SECTION_PATTERN::matcher)
                .filter(Matcher::find)
                .map(matcher -> matcher.group(1))
                .map(Hit::new);
        if (hit.isPresent()) {
            return hit.get();
        }
        System.err.printf("Failed to transform log line to a Hit : '%s'\n", log);
        return null;
    }

}

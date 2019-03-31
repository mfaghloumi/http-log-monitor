package com.datadog.httplogmonitor.domain;

import lombok.Data;

@Data
public class Hit {

    private final String user;

    private final String section;

    private final long contentSize;

}

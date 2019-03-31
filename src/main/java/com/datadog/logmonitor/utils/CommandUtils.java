package com.datadog.logmonitor.utils;

import lombok.Data;
import lombok.experimental.UtilityClass;
import org.apache.commons.cli.*;

import java.util.Optional;

@UtilityClass
public class CommandUtils {

    private final String CMD_NAME = "http-log-monitor";

    private static final String DEFAULT_ACCESS_LOG = "access.log";

    private static final String DEFAULT_THRESHOLD = "10";

    public Parameters parse(String... args) {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        Options options = new Options();
        Option fileOption = Option.builder("f")
                .longOpt("file")
                .hasArg()
                .desc("HTTP log file path, default value is : " + DEFAULT_ACCESS_LOG)
                .type(String.class)
                .build();
        Option thresholdOption = Option.builder("t")
                .longOpt("threshold")
                .hasArg()
                .desc("Alerting threshold average hits/second, default value is : " + DEFAULT_THRESHOLD)
                .type(Double.class)
                .build();
        options.addOption(fileOption);
        options.addOption(thresholdOption);
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException pe) {
            formatter.printHelp(CMD_NAME, options);
            System.exit(1);
        }
        String filename = cmd.getOptionValue("file", DEFAULT_ACCESS_LOG);
        Double threshold = null;
        try {
            threshold = Optional.of(cmd.getOptionValue("threshold", DEFAULT_THRESHOLD))
                    .map(Double::parseDouble)
                    .get();
        } catch (NumberFormatException e) {
            formatter.printHelp(CMD_NAME, options);
            System.exit(1);
        }
        return new Parameters(filename, threshold);
    }

    @Data
    public static class Parameters {

        private final String filename;

        private final Double threshold;

    }
}

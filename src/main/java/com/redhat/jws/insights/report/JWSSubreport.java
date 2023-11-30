/* Copyright (C) Red Hat 2023 */
package com.redhat.jws.insights.report;

import org.apache.catalina.Server;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.redhat.insights.logging.InsightsLogger;
import com.redhat.insights.reports.InsightsSubreport;

/**
 * JWS subreport which only focuses on connector names and fingerprinting binaries
 * of the deployed webapps.
 */
public class JWSSubreport implements InsightsSubreport {

    private final JWSSubreportSerializer serializer;

    public JWSSubreport(Server server, InsightsLogger logger) {
        serializer = new JWSSubreportSerializer(server, logger);
    }

    @Override
    public void generateReport() {
        // The serializer will generate the report
    }

    @Override
    public JsonSerializer<InsightsSubreport> getSerializer() {
        return serializer;
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

}
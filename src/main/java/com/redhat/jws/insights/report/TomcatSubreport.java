/* Copyright (C) Red Hat 2023 */
package com.redhat.jws.insights.report;

import org.apache.catalina.util.ServerInfo;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.redhat.insights.reports.InsightsSubreport;

/**
 * JMX based subreport for Tomcat, which simply uses the json status from the
 * status manager servlet. This includes a lot of telemetry data.
 */
public class TomcatSubreport implements InsightsSubreport {

    private final TomcatSubreportSerializer serializer;

    public TomcatSubreport() {
        serializer = new TomcatSubreportSerializer();
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
        return ServerInfo.getServerNumber();
    }

}
/* Copyright (C) Red Hat 2023 */
package com.redhat.jws.insights.report;

import java.util.Map;

import com.redhat.insights.config.InsightsConfiguration;
import com.redhat.insights.logging.InsightsLogger;
import com.redhat.insights.reports.AbstractTopLevelReportBase;
import com.redhat.insights.reports.InsightsSubreport;

/**
 * The main report for Tomcat/JWS. The superclass is the one generating the
 * "basic" report as long as generateReport is called.
 */
public class TomcatReport extends AbstractTopLevelReportBase {

    private TomcatReport(
            InsightsLogger logger,
            InsightsConfiguration config,
            Map<String, InsightsSubreport> subReports) {
        super(logger, config, subReports);
    }

    public static TomcatReport of(InsightsLogger logger, InsightsConfiguration configuration, Map<String, InsightsSubreport> subReports) {
        return new TomcatReport(logger, configuration, subReports);
    }

    @Override
    protected long getProcessPID() {
        return Long.parseLong(
                java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
    }

    @Override
    protected Package[] getPackages() {
        return Package.getPackages();
    }

}
/* Copyright (C) Red Hat 2023 */
package com.redhat.jws.insights;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

import javax.net.ssl.SSLContext;

import com.redhat.insights.InsightsException;
import com.redhat.insights.InsightsReportController;
import com.redhat.insights.config.EnvAndSysPropsInsightsConfiguration;
import com.redhat.insights.config.InsightsConfiguration;
import com.redhat.insights.core.httpclient.InsightsJdkHttpClient;
import com.redhat.insights.http.InsightsFileWritingClient;
import com.redhat.insights.http.InsightsMultiClient;
import com.redhat.insights.jars.ClasspathJarInfoSubreport;
import com.redhat.insights.logging.InsightsLogger;
import com.redhat.insights.reports.InsightsReport;
import com.redhat.insights.reports.InsightsSubreport;
import com.redhat.insights.tls.PEMSupport;

import com.redhat.jws.insights.report.JWSSubreport;
import com.redhat.jws.insights.report.TomcatReport;
import com.redhat.jws.insights.report.TomcatSubreport;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Server;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class InsightsLifecycleListener implements LifecycleListener {

    private static final Log log = LogFactory.getLog(InsightsLifecycleListener.class);

    private InsightsReportController insightsReportController;
    private InsightsReport insightsReport;
    private final InsightsLogger logger = new TomcatLogger();

    private final InsightsConfiguration configuration = new TomcatInsightsConfiguration();

    @Override
    public void lifecycleEvent(LifecycleEvent event) {

        if (Lifecycle.AFTER_START_EVENT.equals(event.getType())) {
            if (!(event.getLifecycle() instanceof Server)) {
                throw new IllegalArgumentException("Not associated with a server");
            }
            Server server = (Server) event.getLifecycle();

            // Init Insights
            PEMSupport pemSupport = new PEMSupport(logger, configuration);
            Supplier<SSLContext> sslContextSupplier = () -> {
               try {
                  return pemSupport.createTLSContext();
               } catch (Throwable e) {
                  throw new IllegalStateException("Error setting TLS", e);
               }
            };

            Map<String, InsightsSubreport> subReports = new LinkedHashMap<>(3);
            subReports.put("jars", new ClasspathJarInfoSubreport(logger));
            subReports.put("jws", new JWSSubreport(server, logger));
            // The "tomcat" report is the json from the status manager servlet
            subReports.put("tomcat", new TomcatSubreport());
            insightsReport = TomcatReport.of(logger, configuration, subReports);

            TomcatInsightsScheduler insightsScheduler =
                  new TomcatInsightsScheduler(logger, configuration, server.getUtilityExecutor());

            try {
               insightsReportController = InsightsReportController.of(logger, configuration, insightsReport,
                     () -> new InsightsMultiClient(logger,
                           new InsightsJdkHttpClient(logger, configuration, sslContextSupplier),
                           new InsightsFileWritingClient(logger, configuration)), insightsScheduler,
                     new LinkedBlockingQueue<>());
               insightsReportController.generate();
            } catch (Throwable e) {
               throw new IllegalStateException("Insights init failure", e);
            }

        } else if (Lifecycle.STOP_EVENT.equals(event.getType())) {
            if (insightsReportController != null) {
                insightsReportController.shutdown();
            }
        } else if (Lifecycle.PERIODIC_EVENT.equals(event.getType())) {
        }
    }
}

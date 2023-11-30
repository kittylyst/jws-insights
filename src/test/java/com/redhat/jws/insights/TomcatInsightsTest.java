/* Copyright (C) Red Hat 2023 */
package com.redhat.jws.insights;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import com.redhat.jws.insights.report.JWSSubreport;
import com.redhat.jws.insights.report.TomcatReport;
import com.redhat.jws.insights.report.TomcatSubreport;
import com.redhat.jws.insights.report.TomcatSubreportSerializer;
import org.apache.catalina.Globals;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.json.JSONParser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.insights.Filtering;
import com.redhat.insights.config.InsightsConfiguration;
import com.redhat.insights.jars.ClasspathJarInfoSubreport;
import com.redhat.insights.logging.InsightsLogger;
import com.redhat.insights.reports.InsightsReport;
import com.redhat.insights.reports.InsightsSubreport;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class TomcatInsightsTest extends TestCase {

    private static final Log log = LogFactory.getLog(TomcatSubreportSerializer.class);

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public TomcatInsightsTest(String testName) {
        super(testName);
        System.setProperty(Globals.CATALINA_HOME_PROP, "target/test-classes");
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(TomcatInsightsTest.class);
    }

    public void testEmptyTomcat() throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.start();

        InsightsLogger logger = new TomcatLogger();
        Map<String, InsightsSubreport> subReports = new LinkedHashMap<>(3);
        TomcatSubreport tomcatSubreport = new TomcatSubreport();
        ClasspathJarInfoSubreport jarsSubreport = new ClasspathJarInfoSubreport(logger);
        subReports.put("jars", jarsSubreport);
        subReports.put("jws", new JWSSubreport(tomcat.getServer(), logger));
        subReports.put("tomcat", tomcatSubreport);
        InsightsConfiguration configuration = new TomcatInsightsConfiguration();
        InsightsReport insightsReport = TomcatReport.of(logger, configuration, subReports);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                JsonGenerator generator = createFor(insightsReport).writerWithDefaultPrettyPrinter().createGenerator(out)) {
            insightsReport.generateReport(Filtering.DEFAULT);
            insightsReport.getSerializer().serialize(insightsReport, generator, null);
            generator.flush();
            String report = new String(out.toByteArray(), "UTF-8");
            log.info("Report: " + report);
            String result = (new JSONParser(report)).parse().toString();
            // Verify presence of basic report
            assertTrue(result.indexOf("basic") > 0);
        }
    }

    public void testBasicTomcat() throws Exception {
        Tomcat tomcat = new Tomcat();
        Connector connector = new Connector("HTTP/1.1");
        connector.setPort(0);
        tomcat.getService().addConnector(connector);
        tomcat.setConnector(connector);
        tomcat.addContext("/test", null);
        tomcat.start();

        InsightsLogger logger = new TomcatLogger();
        Map<String, InsightsSubreport> subReports = new LinkedHashMap<>(3);
        TomcatSubreport tomcatSubreport = new TomcatSubreport();
        ClasspathJarInfoSubreport jarsSubreport = new ClasspathJarInfoSubreport(logger);
        subReports.put("jars", jarsSubreport);
        subReports.put("jws", new JWSSubreport(tomcat.getServer(), logger));
        subReports.put("tomcat", tomcatSubreport);
        InsightsConfiguration configuration = new TomcatInsightsConfiguration();
        InsightsReport insightsReport = TomcatReport.of(logger, configuration, subReports);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                JsonGenerator generator = createFor(insightsReport).writerWithDefaultPrettyPrinter().createGenerator(out)) {
            insightsReport.generateReport(Filtering.DEFAULT);
            insightsReport.getSerializer().serialize(insightsReport, generator, null);
            generator.flush();
            String report = new String(out.toByteArray(), "UTF-8");
            log.info("Report: " + report);
            String result = (new JSONParser(report)).parse().toString();
            // Verify connector info
            assertTrue(result.indexOf("http-nio-auto") > 0);
            assertTrue(result.indexOf("java.version") > 0);
        }
    }

    public void testTomcat() throws Exception {
        Tomcat tomcat = new Tomcat();
        Connector connector = new Connector("HTTP/1.1");
        connector.setPort(0);
        tomcat.getService().addConnector(connector);
        tomcat.setConnector(connector);
        tomcat.addContext("/test", null);
        tomcat.addContext("/examples", "test-webapp");
        tomcat.start();

        InsightsLogger logger = new TomcatLogger();
        Map<String, InsightsSubreport> subReports = new LinkedHashMap<>(3);
        TomcatSubreport tomcatSubreport = new TomcatSubreport();
        ClasspathJarInfoSubreport jarsSubreport = new ClasspathJarInfoSubreport(logger);
        subReports.put("jars", jarsSubreport);
        subReports.put("jws", new JWSSubreport(tomcat.getServer(), logger));
        subReports.put("tomcat", tomcatSubreport);
        InsightsConfiguration configuration = new TomcatInsightsConfiguration();
        InsightsReport insightsReport = TomcatReport.of(logger, configuration, subReports);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                JsonGenerator generator = createFor(insightsReport).writerWithDefaultPrettyPrinter().createGenerator(out)) {
            insightsReport.generateReport(Filtering.DEFAULT);
            insightsReport.getSerializer().serialize(insightsReport, generator, null);
            generator.flush();
            String report = new String(out.toByteArray(), "UTF-8");
            log.info("Report: " + report);
            String result = (new JSONParser(report)).parse().toString();
            // Verify webapp jar checksum
            assertTrue(result.indexOf("a8dda6f938e91e18d47a6cb8593167222ba6abea") > 0);
        }
    }

    private static ObjectMapper createFor(InsightsReport insightsReport) {
        return new ObjectMapper();
      }

}

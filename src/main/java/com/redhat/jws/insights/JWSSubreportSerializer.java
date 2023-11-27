package com.redhat.jws.insights;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.redhat.insights.InsightsSubreport;
import com.redhat.insights.jars.JarAnalyzer;
import com.redhat.insights.jars.JarInfo;
import com.redhat.insights.logging.InsightsLogger;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Loader;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.IntrospectionUtils;

public class JWSSubreportSerializer extends JsonSerializer<InsightsSubreport> {

    private static final Log log = LogFactory.getLog(JWSSubreportSerializer.class);

    private final Server server;
    private final InsightsLogger logger;

    JWSSubreportSerializer(Server server, InsightsLogger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Override
    public void serialize(InsightsSubreport subreport, JsonGenerator generator, SerializerProvider serializerProvider)
            throws IOException {
        generator.writeStartObject();
        generator.writeStringField("version", subreport.getVersion());
        JarAnalyzer analyzer = new JarAnalyzer(logger, true);
        Service[] services = server.findServices();
        for (Service service : services) {
            Connector[] connectors = service.findConnectors();
            generator.writeFieldName("connectors");
            generator.writeStartArray();
            for (Connector connector : connectors) {
                String name = String.valueOf(IntrospectionUtils.getProperty(connector, "name"));
                generator.writeStartObject();
                generator.writeStringField("name", name);
                generator.writeEndObject();
            }
            generator.writeEndArray();
            Container[] hosts = service.getContainer().findChildren();
            generator.writeFieldName("hosts");
            generator.writeStartArray();
            for (Container host : hosts) {
                String hostName = host.getName();
                generator.writeStartObject(); //host
                generator.writeStringField("name", hostName);
                Container[] contexts = host.findChildren();
                generator.writeFieldName("contexts");
                generator.writeStartArray(); //contexts
                for (Container context : contexts) {
                    String contextName = context.getName();
                    generator.writeStartObject(); //context
                    generator.writeStringField("name", contextName);
                    Loader loader = ((Context) context).getLoader();
                    URLClassLoader classLoader = (URLClassLoader) loader.getClassLoader();
                    URL[] urls = classLoader.getURLs();
                    ArrayList<JarInfo> jarInfos = new ArrayList<>();
                    for (URL url : urls) {
                        try {
                            JarInfo jarInfo = analyzer.process(url).orElse(null);
                            if (jarInfo != null) {
                                jarInfos.add(jarInfo);
                            }
                        } catch (URISyntaxException e) {
                            log.info("URI error", e);
                        }
                    }
                    generator.writeFieldName("jars");
                    generator.writeStartArray();
                    for (JarInfo jarInfo : jarInfos) {
                        generator.writeStartObject();
                        generator.writeStringField("name", jarInfo.name());
                        generator.writeStringField("version", jarInfo.version());
                        generator.writeObjectField("attributes", jarInfo.attributes());
                        generator.writeEndObject();
                    }
                    generator.writeEndArray();
                    generator.writeEndObject(); //context
                }
                generator.writeEndArray(); //contexts
                generator.writeEndObject(); //host
            }
            generator.writeEndArray();
            // Only do the first service ...
            break;
        }
        generator.writeEndObject();
        generator.flush();
    }

}

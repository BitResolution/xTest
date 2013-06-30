package com.bitresolution.xtest;

import com.bitresolution.succor.reflection.FullyQualifiedClassName;
import com.bitresolution.xtest.core.Engine;
import com.bitresolution.xtest.core.SourceConfiguration;
import com.bitresolution.xtest.core.spring.context.DefaultAnnotationConfigApplicationContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class Bootstrap {

    private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);

    public static void main(String[] args) {
        FullyQualifiedClassName configurationClass = new FullyQualifiedClassName(XTestDefaultContext.class);
        Properties properties = new Properties();
        SourceConfiguration sourceConfiguration = new SourceConfiguration();
        DefaultAnnotationConfigApplicationContextFactory contextFactory = new DefaultAnnotationConfigApplicationContextFactory();

        Engine engine = new Engine(configurationClass, sourceConfiguration, properties, contextFactory);
        engine.start();
        try {
            engine.join();
        }
        catch (InterruptedException e) {
            log.error("Error waiting for engine to complete", e);
        }
    }
}

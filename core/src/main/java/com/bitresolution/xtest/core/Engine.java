package com.bitresolution.xtest.core;

import com.bitresolution.xtest.core.lifecycle.LifecycleExecutor;
import com.bitresolution.xtest.core.spring.context.AnnotationConfigApplicationContextFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Engine extends Thread {

    private static final Logger log = LoggerFactory.getLogger(Engine.class);

    private final AnnotationConfigApplicationContextFactory factory;
    private final XTestConfiguration configuration;

    public Engine(XTestConfiguration configuration, AnnotationConfigApplicationContextFactory annotationConfigApplicationContextFactory) {
        setName("engine");
        this.configuration = configuration;
        this.factory = annotationConfigApplicationContextFactory;
    }

    @Override
    public void run() {
        execute();
    }

    public ExitStatus execute() {
        logBorderedMessage("Starting xTest");
        ExitStatus status;
        AnnotationConfigApplicationContext context = null;
        try {
            context = factory.create();
            context.register(configuration.getConfigurationClass().loadClass());
            ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
            beanFactory.registerSingleton("configuration", configuration);
            context.refresh();

            log.info("Starting engine...");
            LifecycleExecutor executor = context.getBean(LifecycleExecutor.class);

            log.info("Executing lifecycle...");
            executor.execute();

            log.info("Lifecycle complete, shutting down xTest...");
            status = ExitStatus.SUCCESS;
        }
        catch (Exception e) {
            log.error("Terminal error: {}", e.getMessage(), e);
            status = ExitStatus.ERROR;
        }
        finally {
            if(context != null) {
                try {
                    context.close();
                }
                catch (Exception e) {
                    log.error("Error closing Spring context", e);
                }
            }
            logBorderedMessage("Stopped xTest");
        }
        return status;
    }

    private void logBorderedMessage(String message) {
        StringBuilder builder = new StringBuilder(message.length() * 2);
        for(char c : message.toCharArray()) {
            builder.append(c).append(" ");
        }

        log.info(StringUtils.repeat("-", 53));
        log.info("- " + StringUtils.rightPad(builder.toString().toUpperCase(), 50) + "-");
        log.info(StringUtils.repeat("-", 53));
    }

    public static enum ExitStatus {
        SUCCESS,
        ERROR
    }
}
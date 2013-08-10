package com.bitresolution.xtest.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;

public class PhaseConfigurationBeanFactory {

    public void registerBeans(XTestConfiguration configuration, GenericApplicationContext context) throws BeansException {
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        for(PhaseConfiguration phaseConfiguration : configuration.getPhaseConfigurations().values()) {
            beanFactory.registerSingleton(buildPhaseConfigurationBeanName(phaseConfiguration), phaseConfiguration);
        }
    }

    private String buildPhaseConfigurationBeanName(PhaseConfiguration phaseConfiguration) {
        return phaseConfiguration.getPhase().getSimpleName() + "Configuration";
    }
}

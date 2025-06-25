package org.hl7.davinci.ehrserver;

import org.hl7.davinci.ehrserver.interceptor.CapabilityStatementCustomizer;
import org.hl7.davinci.ehrserver.interceptor.OrderIdentifierAdditionInterceptor;
import org.hl7.davinci.ehrserver.interceptor.SmartInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import ca.uhn.fhir.jpa.starter.AppProperties;
import ca.uhn.fhir.rest.server.RestfulServer;
import jakarta.annotation.PostConstruct;

/**
 * Configuration class to register interceptors for the FHIR server.
 */
@Configuration
public class InterceptorConfig {

    @Autowired
    private RestfulServer restfulServer;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private SecurityProperties securityProperties;

    @PostConstruct
    public void registerInterceptors() {
        restfulServer.registerInterceptor(new CapabilityStatementCustomizer(securityProperties));
        restfulServer.registerInterceptor(new ClientAuthorizationInterceptor(securityProperties));
        restfulServer.registerInterceptor(new OrderIdentifierAdditionInterceptor());
        restfulServer.registerInterceptor(new SmartInterceptor(appProperties, securityProperties));
    }
}

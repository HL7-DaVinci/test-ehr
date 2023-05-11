package org.hl7.davinci.ehrserver;

import ca.uhn.fhir.context.support.IValidationSupport;
import ca.uhn.fhir.jpa.api.config.DaoConfig;
import ca.uhn.fhir.jpa.api.dao.IFhirSystemDao;
import ca.uhn.fhir.jpa.provider.JpaCapabilityStatementProvider;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.util.ISearchParamRegistry;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.UriType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class ServerConformanceR4 extends JpaCapabilityStatementProvider {
  @Autowired
  private org.springframework.core.env.Environment env;
  public ServerConformanceR4(RestfulServer theRestfulServer, IFhirSystemDao<Bundle, Meta> theSystemDao, DaoConfig theDaoConfig, ISearchParamRegistry theSearchParamRegistry, IValidationSupport theValidationSupport) {
    super(theRestfulServer, theSystemDao, theDaoConfig, theSearchParamRegistry, theValidationSupport);
  }

  @Override
  public CapabilityStatement getServerConformance(HttpServletRequest theRequest, RequestDetails details) {
    Extension securityExtension = new Extension();
    System.out.println();
    System.out.println(env.toString());;
    System.out.println();
    securityExtension.setUrl("http://fhir-registry.smarthealthit.org/StructureDefinition/oauth-uris");
    securityExtension.addExtension()
        .setUrl("authorize")
        .setValue(new UriType(env.getProperty("proxy_authorize")));
    securityExtension.addExtension()
        .setUrl("token")
        .setValue(new UriType(env.getProperty("proxy_token")));
    CapabilityStatement.CapabilityStatementRestSecurityComponent securityComponent = new CapabilityStatement.CapabilityStatementRestSecurityComponent();
    securityComponent.setCors(true);
    securityComponent
        .addExtension(securityExtension);

    CapabilityStatement regularConformance = (CapabilityStatement) super.getServerConformance(theRequest, details);
    regularConformance.getRest().get(0).setSecurity(securityComponent);
    return regularConformance;
  }
}

package org.hl7.davinci.ehrserver.interceptor;

import org.hl7.davinci.ehrserver.SecurityProperties;
import org.hl7.fhir.instance.model.api.IBaseConformance;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.UriType;

import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Interceptor;
import ca.uhn.fhir.interceptor.api.Pointcut;

@Interceptor
public class CapabilityStatementCustomizer {

   SecurityProperties securityProperties;

   public CapabilityStatementCustomizer(SecurityProperties securityProperties) {
      this.securityProperties = securityProperties;
   }

   @Hook(Pointcut.SERVER_CAPABILITY_STATEMENT_GENERATED)
   public void customize(IBaseConformance theCapabilityStatement) {

      Extension securityExtension = new Extension();
      securityExtension.setUrl("http://fhir-registry.smarthealthit.org/StructureDefinition/oauth-uris");
      securityExtension.addExtension()
         .setUrl("authorize")
         .setValue(new UriType(securityProperties.getProxyAuthorize()));
      securityExtension.addExtension()
         .setUrl("token")
         .setValue(new UriType(securityProperties.getProxyToken()));
      CapabilityStatement.CapabilityStatementRestSecurityComponent securityComponent = new CapabilityStatement.CapabilityStatementRestSecurityComponent();
      securityComponent.setCors(true);
      securityComponent
         .addExtension(securityExtension);

      // Cast to the appropriate version
      CapabilityStatement cs = (CapabilityStatement) theCapabilityStatement;

      // Customize the CapabilityStatement as desired
      cs.getRest().get(0).setSecurity(securityComponent);

   }

}
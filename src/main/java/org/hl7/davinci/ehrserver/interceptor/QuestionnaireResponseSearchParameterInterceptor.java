package org.hl7.davinci.ehrserver.interceptor;

import org.hl7.fhir.r4.model.Enumerations.PublicationStatus;
import org.hl7.fhir.r4.model.Enumerations.SearchParamType;

import org.hl7.fhir.r4.model.SearchParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Interceptor;
import ca.uhn.fhir.interceptor.api.Pointcut;
import ca.uhn.fhir.rest.client.api.IGenericClient;

@Interceptor
public class QuestionnaireResponseSearchParameterInterceptor {
    static final Logger logger = LoggerFactory.getLogger(QuestionnaireResponseSearchParameterInterceptor.class.getName());
    private boolean bQRContextSPCreated = false;
    private FhirContext ctx;
    private String url = "http://localhost:8080";
    private IGenericClient client;

    public QuestionnaireResponseSearchParameterInterceptor(FhirContext ctx, String serverAddress) {
        bQRContextSPCreated = false;
       if (serverAddress != null && !serverAddress.equals("")) {
         url = serverAddress;
       }
       this.ctx = ctx;
       this.client = this.ctx.newRestfulGenericClient(this.url + "/test-ehr/r4");
    }

    @Hook(Pointcut.SERVER_INCOMING_REQUEST_PRE_PROCESSED)
    public void createQRContextSearchParameter(FhirContext ctx, String serverAddress) {
        if(!bQRContextSPCreated) {
            bQRContextSPCreated = true;
            SearchParameter sp = new SearchParameter();
            sp.addBase("QuestionnaireResponse");
            sp.setType(SearchParamType.REFERENCE);
            sp.setTitle("context");
            sp.setCode("context");
            sp.setStatus(PublicationStatus.ACTIVE);
            sp.setExpression("QuestionnaireResponse.extension('http://hl7.org/fhir/us/davinci-dtr/StructureDefinition/context')");
            
            sp.addComparator(SearchParameter.SearchComparator.EQ);
            client.create().resource(sp).execute();
        }           
    }
}

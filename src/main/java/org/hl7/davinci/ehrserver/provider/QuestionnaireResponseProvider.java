package org.hl7.davinci.ehrserver.provider;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.QuestionnaireResponse;

import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.param.ReferenceParam;

import ca.uhn.fhir.rest.server.IResourceProvider;

public class QuestionnaireResponseProvider implements IResourceProvider {


    @Override
    public Class<QuestionnaireResponse> getResourceType() {
        return QuestionnaireResponse.class;
    } 
    
    @Search
    List<QuestionnaireResponse> getQuestionnaireResponseByContext(@RequiredParam(name = "context") ReferenceParam reference) {
        List<QuestionnaireResponse> newList = new ArrayList<>();

        //validate if the reference is valid 

        //search the QR and filter out 
        return newList;
    }
}

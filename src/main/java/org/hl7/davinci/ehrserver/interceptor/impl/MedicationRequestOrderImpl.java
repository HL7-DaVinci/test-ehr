package org.hl7.davinci.ehrserver.interceptor.impl;

import java.util.List;

import org.hl7.davinci.ehrserver.interceptor.OrderInterface;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.*;

public class MedicationRequestOrderImpl implements OrderInterface {

    private MedicationRequest request;

    public MedicationRequestOrderImpl(MedicationRequest request) {
        this.request = request;
    }

    @Override
    public List<Identifier> getIdentifier() {
        return request.getIdentifier();
    }

    @Override
    public Identifier addIdentifier() {
        return request.addIdentifier();
    }

    @Override
    public ResourceType getResourceType() {
        return request.getResourceType();
    }

    @Override
    public IBaseResource getRequest() {
        return this.request;
    }
}

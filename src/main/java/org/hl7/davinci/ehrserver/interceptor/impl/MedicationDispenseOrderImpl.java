package org.hl7.davinci.ehrserver.interceptor.impl;

import org.hl7.davinci.ehrserver.interceptor.OrderInterface;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.*;
import java.util.List;

public class MedicationDispenseOrderImpl implements OrderInterface {
    private MedicationDispense request;

    public MedicationDispenseOrderImpl(MedicationDispense request) {
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

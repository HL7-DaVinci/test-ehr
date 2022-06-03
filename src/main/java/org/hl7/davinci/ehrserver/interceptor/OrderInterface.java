package org.hl7.davinci.ehrserver.interceptor;

import java.util.List;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.instance.model.api.IBaseResource;

public interface OrderInterface {
    List<Identifier> getIdentifier();

    Identifier addIdentifier();

    ResourceType getResourceType();

    IBaseResource getRequest();
}

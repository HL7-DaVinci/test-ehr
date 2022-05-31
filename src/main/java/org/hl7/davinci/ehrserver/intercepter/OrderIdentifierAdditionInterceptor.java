package org.hl7.davinci.ehrserver.intercepter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ca.uhn.fhir.interceptor.api.Interceptor;
import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Pointcut;
import org.hl7.fhir.instance.model.api.IBaseResource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.hl7.fhir.r4.model.*;
import java.util.stream.Collectors;
import java.util.List;
import java.util.UUID;

@Interceptor
public class OrderIdentifierAdditionInterceptor {

    static final Logger logger = LoggerFactory.getLogger(OrderIdentifierAdditionInterceptor.class.getName());

    private static final String CODE_PLAC = "PLAC";

    public OrderIdentifierAdditionInterceptor() {

    }

    @Hook(Pointcut.STORAGE_PRESTORAGE_RESOURCE_CREATED)
    public void insert(IBaseResource theResource) {
        switch (theResource.fhirType()) {
            case "DeviceRequest":
            case "ServiceRequest":
            case "MedicationRequest":
            case "MedicationDispense":
                processIdentifier(theResource);
                break;
            default:
                break;

        }

    }

    private void processIdentifier(IBaseResource theResource) {
        logger.info("Processing order identifier");
        switch (theResource.fhirType()) {
            case "DeviceRequest":
                DeviceRequest deviceRequest = ((DeviceRequest) theResource);
                addIdentifier(deviceRequest);
                break;
            case "ServiceRequest":
                ServiceRequest serviceRequest = (ServiceRequest) theResource;
                addIdentifier(serviceRequest);
                break;
            case "MedicationRequest":
                MedicationRequest medicationRequest = (MedicationRequest) theResource;
                addIdentifier(medicationRequest);
                break;
            case "MedicationDispense":
                MedicationDispense medicationDispense = (MedicationDispense) theResource;
                addIdentifier(medicationDispense);
                break;

            default:
                break;
        }
    }

    private <T extends DeviceRequest, ServiceRequest, MedicationRequest, MedicationDispense> void addIdentifier(
            T request) {
        List<Identifier> placerList = request.getIdentifier().stream().filter(id -> {
            List<Coding> placerCodingList = id.getType().getCoding().stream()
                    .filter(coding -> coding.getCode().equals(CODE_PLAC))
                    .collect(Collectors.toList());
            return placerCodingList.size() > 0;
        })
                .collect(Collectors.toList());
        if (placerList.size() <= 0) {
            logger.info("----- adding placer identifier for order. Resource type: " + request.getResourceType());
            request
                    .addIdentifier().setType(new CodeableConcept()
                            .addCoding(new Coding().setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
                                    .setCode(CODE_PLAC)))
                    .setValue(UUID.randomUUID().toString());
        }

    }
}

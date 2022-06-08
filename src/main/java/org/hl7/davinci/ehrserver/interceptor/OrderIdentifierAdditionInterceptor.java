package org.hl7.davinci.ehrserver.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ca.uhn.fhir.interceptor.api.Interceptor;
import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Pointcut;

import org.hl7.davinci.ehrserver.interceptor.impl.DeviceRequestOrderImpl;
import org.hl7.davinci.ehrserver.interceptor.impl.MedicationDispenseOrderImpl;
import org.hl7.davinci.ehrserver.interceptor.impl.MedicationRequestOrderImpl;
import org.hl7.davinci.ehrserver.interceptor.impl.ServiceRequestOrderImpl;
import org.hl7.fhir.instance.model.api.IBaseResource;
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
                if(!hasPlacerIdentifier(deviceRequest.getIdentifier())){
                    deviceRequest.addIdentifier(getIdentifier());
                }
                break;
            case "ServiceRequest":
                ServiceRequest serviceRequest = (ServiceRequest) theResource;
                if(!hasPlacerIdentifier(serviceRequest.getIdentifier())){
                    serviceRequest.addIdentifier(getIdentifier());
                }
                break;
            case "MedicationRequest":
                MedicationRequest medicationRequest = (MedicationRequest) theResource;
                if(!hasPlacerIdentifier(medicationRequest.getIdentifier())){
                    medicationRequest.addIdentifier(getIdentifier());
                }
                break;
            case "MedicationDispense":
                MedicationDispense medicationDispense = (MedicationDispense) theResource;
                if(!hasPlacerIdentifier(medicationDispense.getIdentifier())){
                    medicationDispense.addIdentifier(getIdentifier());
                }
                break;
            default:
                break;
        }
    }

    private <T extends OrderInterface> void addIdentifier(T request) {
        List<Identifier> placerList = request.getIdentifier().stream().filter(id -> {
            List<Coding> placerCodingList = id.getType().getCoding().stream()
                    .filter(coding -> coding.getCode().equals(CODE_PLAC))
                    .collect(Collectors.toList());
            return placerCodingList.size() > 0;
        })
                .collect(Collectors.toList());
        if (placerList.size() <= 0) {
            logger.info("Adding placer identifier for order. Resource type: " + request.getResourceType());
            request
                    .addIdentifier().setType(new CodeableConcept()
                            .addCoding(new Coding().setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
                                    .setCode(CODE_PLAC)))
                    .setValue(UUID.randomUUID().toString());
        }
    }

    private Identifier getIdentifier() {
        Identifier identifier = new Identifier();
        identifier.setType(new CodeableConcept()
                .addCoding(new Coding().setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
                        .setCode(CODE_PLAC)))
                .setValue(UUID.randomUUID().toString());
        return identifier;
    }

    private boolean hasPlacerIdentifier(List<Identifier> identifierList) {
        List<Identifier> placerList = identifierList.stream().filter(id -> {
            List<Coding> placerCodingList = id.getType().getCoding().stream()
                    .filter(coding -> coding.getCode().equals(CODE_PLAC))
                    .collect(Collectors.toList());
            return placerCodingList.size() > 0;
        }).collect(Collectors.toList());
        ;
        return placerList.size() > 0;
    }
}

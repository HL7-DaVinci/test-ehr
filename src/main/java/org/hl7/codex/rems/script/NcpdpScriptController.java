package org.hl7.codex.rems.script;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.MedicationDispense;
import org.hl7.fhir.r4.model.Reference;

import ca.uhn.fhir.jpa.starter.JpaRestfulServer;

import org.hl7.fhir.instance.model.api.IIdType;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.jpa.partition.SystemRequestDetails;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;


@RestController
public class NcpdpScriptController {
    static final Logger logger = LoggerFactory.getLogger(NcpdpScriptController.class);

    private static final String APPLICATION_XML = "application/xml";

    @Autowired
    private JpaRestfulServer jpaRestfulServer;

    /**
     * A custom endpoint that handles NCPDP Script messages.
     * @param payload - the object used to serialize the XML in the request body.
     * @return - an object containing the NCPDP Script Status to return.
     */
    @PostMapping(value = "/ncpdp/script", produces = {APPLICATION_XML}, consumes = {APPLICATION_XML})
    @ResponseBody
    public Message handleScriptMessage(@RequestBody Message payload) {
        logger.info("NcpdpScriptController::handleScriptMessage /ncpdp/script");

        Header header = payload.getHeader();
        Body body = payload.getBody();

        Boolean returnSuccess = false;
        String errorMessage = "";

        if (body.getRxFill() != null) {
            logger.info("NcpdpScriptController::handleScriptMessage RxFill Message");
            handleRxFillMessage(body.getRxFill(), header);
            returnSuccess = true;
        } else if (body.getStatus() != null) {
            errorMessage = "Status Message NOT supported";
            logger.info("NcpdpScriptController::handleScriptMessage " + errorMessage);
        } else {
            errorMessage = "Unsupported NCPDP SCRIPT Message";
            logger.info("NcpdpScriptController::handleScriptMessage " + errorMessage);
        }

        Body returnBody;
        if (returnSuccess) {
            // build the status to return
            Status status = new Status("000", null, null, null);
            returnBody = new Body(status);
        } else {
            // build the error to return
            Error error = new Error("900", "1000", errorMessage);
            returnBody = new Body(error);
        }

        Header returnHeader = new Header(header.getFrom().value, header.getTo().value, 
            header.getMessageId(), header.getPrescriberOrderNumber());
        Message returnMessage = new Message(returnHeader, returnBody);
        return returnMessage;
    }

    private void handleRxFillMessage(RxFill rxFill, Header header) {
        FillStatus.DispensedStatusEnum dispensedStatus = rxFill.getFillStatus().getStatus();

        logger.info("    PrescriberOrderNumber: " + header.getPrescriberOrderNumber());
        logger.info("    Dispensed Status: " + dispensedStatus);

        IFhirResourceDao<MedicationRequest> medicationRequestDao = 
            jpaRestfulServer.getDao(MedicationRequest.class);
        IFhirResourceDao<MedicationDispense> medicationDispenseDao = 
            jpaRestfulServer.getDao(MedicationDispense.class);

        IIdType id = new IdDt(header.getPrescriberOrderNumber());

        RequestDetails requestDetails = new SystemRequestDetails();
        requestDetails.setRequestId(header.getPrescriberOrderNumber());

        // retrieve the MedicationRequest
        MedicationRequest medicationRequest = null;
        try {
            medicationRequest = medicationRequestDao.read(id, requestDetails);
            logger.info("    Retrieved MedicationRequest: " + medicationRequest.getId() + 
                " subject: " + medicationRequest.getSubject().getReference());

        } catch (ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException e1) {
            logger.warn("    ERROR: resource not found");
        } catch (ca.uhn.fhir.rest.server.exceptions.ResourceGoneException e2) {
            logger.warn("    ERROR: resource has been deleted");
        }

        // create the MedicationDispense
        MedicationDispense medicationDispense = new MedicationDispense();
        if (medicationRequest != null) {
            String[] requestIdParts = medicationRequest.getId().split("/");
            String requestId = requestIdParts[0] + "/" + requestIdParts[1];
            String dispenseId = requestIdParts[1] + "-dispense";
            medicationDispense.setId(dispenseId);
            medicationDispense.setStatus(convertRxFillDispensedStatusToMedicationDispenseStatus(dispensedStatus));
            medicationDispense.setMedication(medicationRequest.getMedication());
            medicationDispense.setSubject(medicationRequest.getSubject());
            medicationDispense.addAuthorizingPrescription(new Reference(requestId));

            // store the MedicationDispense
            RequestDetails dispenseDetails = new SystemRequestDetails();
            dispenseDetails.setRequestId(dispenseId);
            medicationDispenseDao.update(medicationDispense, dispenseDetails);
            logger.info("    Created new MedicationDispense: " + dispenseId);
        } 
    }

    private MedicationDispense.MedicationDispenseStatus convertRxFillDispensedStatusToMedicationDispenseStatus(FillStatus.DispensedStatusEnum dispensedStatus) {
        switch(dispensedStatus) {
            case DISPENSED:
                return MedicationDispense.MedicationDispenseStatus.COMPLETED;
            case PARTIALLY_DISPENSED:
                return MedicationDispense.MedicationDispenseStatus.INPROGRESS;
            case NOT_DISPENSED:
                return MedicationDispense.MedicationDispenseStatus.PREPARATION;
            case TRANSFERRED:
            case UNKNOWN:
            default:
                return MedicationDispense.MedicationDispenseStatus.UNKNOWN;
        }
    }
}

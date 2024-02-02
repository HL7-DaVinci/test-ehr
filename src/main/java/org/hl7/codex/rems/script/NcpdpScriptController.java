package org.hl7.codex.rems.script;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NcpdpScriptController {
    static final Logger logger = LoggerFactory.getLogger(NcpdpScriptController.class);

    private static final String APPLICATION_XML = "application/xml";

    @Autowired
    private org.springframework.core.env.Environment environment;

    /**
     * A custom endpoint that handles NCPDP Script messages.
     * @param payload - the object used to serialize the XML in the request body.
     * @return - an object containing the NCPDP Script Status to return.
     */
    @PostMapping(value = "/script/rxfill", produces = {APPLICATION_XML}, consumes = {APPLICATION_XML})
    @ResponseBody
    public RxFillStatusMessage getScriptResponse(@RequestBody RxFillMessage payload) {
        /*
         * TODO: zzzz
         * x create classes for xml NCPDP SCRIPT RxFill input
         * x create classes for xml NCPDP SCRIPT Status output
         * grab matching MedicationRequest
         * create MedicationResponse
         * store MedicationResponse
         * x creat the output message and return
         */
        logger.info("getScriptResponse /script/rxfill");

        Header header = payload.getHeader();
        RxFillBody body = payload.getBody();

        logger.info("zzzz: MessageID: " + header.getMessageId());
        logger.info("zzzz: To (" + header.getTo().qualifier + "): " + header.getTo().value);
        logger.info("zzzz: From (" + header.getFrom().qualifier + "): " + header.getFrom().value);
        logger.info("zzzz: PrescriberOrderNumber: " + header.getPrescriberOrderNumber());
        logger.info("zzzz: Dispensed Status: " + body.getRxFill().getFillStatus().getDispensed().Note);

        // build the status to return
        Header statusHeader = new Header(header.getFrom().value, header.getTo().value, 
            header.getMessageId(), header.getPrescriberOrderNumber());
        Status status = new Status("000", null, null, null);
        RxFillStatusBody statusBody = new RxFillStatusBody(status);
        RxFillStatusMessage statusMessage = new RxFillStatusMessage(statusHeader, statusBody);
        return statusMessage;
    }
}

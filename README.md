# EHR FHIR Server
This subproject hosts a HAPI FHIR server that is based on the [hapi-fhir-jpaserver-example](https://github.com/jamesagnew/hapi-fhir/tree/master/hapi-fhir-jpaserver-example).

## Init the test-ehr 
1. delete `target` and `build` folders from test-ehr 
2. from Terminal (1) while in test-ehr folder: `gradle appRun`
3. from Terminal (2) while in test-ehr folder:   `gradle loadData`

>Tip: Some VPNs might prevent the test-ehr from starting with 'gradle appRun'. To workaround this you could disable your VPN then run 'gradle appRun'. Once the test-ehr has started you should be able to restart your VPN. The test-ehr should continue to run. 

## Running the server
`gradle appRun`

This will start the server running on http://localhost:8080/test-ehr.

## Adding resources to the database
The FHIR server will persist FHIR resources between restarts. You can delete the folder `target` to clear all resources.

To load the data from the json files in fhirResourcesToLoad, run the following script:  
`gradle loadData` 

>Note: 'gradle loadData' can only be run while the FHIR server is running and `use_oauth` is false in         `src/main/resources/fhirServer.properties`

## Server endpoints
|Relative URL|Endpoint Description|
|----|----|
|`/`|Web page with basic RI information|
|`/test-ehr/`|Base server endpoint|
|`/test-ehr/r4`|EHR FHIR Server endpoint (will not resolve in browser)|

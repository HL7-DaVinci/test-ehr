# EHR FHIR Server
This subproject hosts a HAPI FHIR server that is based on the [hapi-fhir-jpaserver-example](https://github.com/jamesagnew/hapi-fhir/tree/master/hapi-fhir-jpaserver-example).

## Init the test-ehr 
1. delete `target` and `build` folders from test-ehr if they exist
2. from Terminal (1) while in test-ehr folder: `gradle bootRun`
3. from Terminal (2) while in test-ehr folder:   `gradle loadData`

## Running the server
`gradle bootRun`

This will start the server running on http://localhost:8080/test-ehr.

## Adding resources to the database
The FHIR server will persist FHIR resources between restarts. You can delete the folder `target` to clear all resources.

To load the data from the json files in fhirResourcesToLoad, run the following script:  
`gradle loadData` 

>Note: 'gradle loadData' can only be run while the FHIR server is running and `use_oauth` is false in         `src/main/resources/fhirServer.properties`

## Server endpoints
|Relative URL|Endpoint Description|
|----|----|
|`/test-ehr/`|Base server endpoint|
|`/test-ehr/r4`|EHR FHIR Server endpoint (will not resolve in browser)|

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

## Using OAuth security features
The FHIR server is open by default, but this can be changed in the `fhirServer.properties` file.  

First, change the `use_oauth` flag to `true` to turn on security.  Then set the `client_id`, `client_secret`, and `oauth_token` fields.

If using Keycloak and following the [CRD](https://github.com/mcode/CRD) guide, the `client_id` and `oauth_token` fields can be left as default.  The `client_secret` can be found with the following steps:

1) Open the keycloak admin console (http://localhost:8180/auth) and log in
2) Open the ClientFhirServer, then the `clients` tab, and click `app-token`.  
3) Click on the `Credentials` tab, use the `regenerate secret` option if needed.
4) Copy the client secret into the properties file under `client_secret`

Finally, ensure that the [request generator](https://github.com/mcode/crd-request-generator) has the correct username and password in the `properties.json` file.  If following the CRD guide, this will be one of the users created when setting up Keycloak.

## Server endpoints
|Relative URL|Endpoint Description|
|----|----|
|`/test-ehr/`|Base server endpoint|
|`/test-ehr/r4`|EHR FHIR Server endpoint (will not resolve in browser)|
 

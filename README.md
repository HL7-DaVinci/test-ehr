# EHR FHIR Server
This subproject hosts a HAPI FHIR server that is based on the [hapi-fhir-jpaserver-example](https://github.com/hapifhir/hapi-fhir-jpaserver-starter).

## Foundry
A live demo is hosted by [HL7 FHIR Foundry](https://foundry.hl7.org/products/ee6c4df0-49d8-4e16-ad17-7aba82c47b25).

## Prerequisites
- Java 17+
- Maven

## Running the server
`mvn spring-boot:run`

This will start the server running on http://localhost:8080/test-ehr.

## Adding resources to the database
The database will be initialized with any resources in the `src/main/resources/seed-data` directory.

## Using OAuth security features
The FHIR server is open by default, but this can be changed in the `fhirServer.properties` file.  

First, change the `use_oauth` flag to `true` to turn on security.  Then set the `client_id`, `client_secret`, and `oauth_token` fields.

If using Keycloak and following the [CRD](https://github.com/HL7-DaVinci/CRD) guide, the `client_id` and `oauth_token` fields can be left as default.  The `client_secret` can be found with the following steps:

1) Open the keycloak admin console (http://localhost:8180/auth) and log in
2) Open the ClientFhirServer, then the `clients` tab, and click `app-token`.  
3) Click on the `Credentials` tab, use the `regenerate secret` option if needed.
4) Copy the client secret into the properties file under `client_secret`

Finally, ensure that the [request generator](https://github.com/HL7-DaVinci/crd-request-generator) has the correct username and password in the `properties.json` file.  If following the CRD guide, this will be one of the users created when setting up Keycloak.

## Server endpoints
|Relative URL|Endpoint Description|
|----|----|
|`/test-ehr/`|Base server endpoint|
|`/test-ehr/r4`|EHR FHIR Server endpoint (will not resolve in browser)|
 
## Questions and Contributions
Questions about the project can be asked in the [Da Vinci CRD stream on the FHIR Zulip Chat](https://chat.fhir.org/#narrow/stream/180803-Da-Vinci.20CRD).

This project welcomes Pull Requests. Any issues identified with the RI should be submitted via the [GitHub issue tracker](https://github.com/HL7-DaVinci/test-ehr/issues).

As of October 1, 2022, The Lantana Consulting Group is responsible for the management and maintenance of this Reference Implementation.
In addition to posting on FHIR Zulip Chat channel mentioned above you can contact [Corey Spears](mailto:corey.spears@lantanagroup.com) for questions or requests.

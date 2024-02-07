# EHR FHIR Server

This subproject hosts a HAPI FHIR server that is based on the now-deprecated [hapi-fhir-jpaserver-example](https://github.com/jamesagnew/hapi-fhir/tree/master/hapi-fhir-jpaserver-example). The new repo is found [here](https://github.com/hapifhir/hapi-fhir-jpaserver-starter).

## Running the server

1. Make sure `gradle` is installed on your machine. Gradle v6.9 or higher.
2. Run `gradle bootRun`
3. In a separate terminal tab, run `gradle loadData` to load resources

This will start the server running on http://localhost:8080/test-ehr.

**If you've loaded resources before, and want a clean slate, delete the `target` and `build` folders from test-ehr if they exist.**

## Adding resources to the database

The FHIR server will persist FHIR resources between restarts. You can delete the folder `target` to clear all resources.

To load the data from the json files in fhirResourcesToLoad, run the following script:  
`gradle loadData`

> Note: 'gradle loadData' can only be run while the FHIR server is running and `use_oauth` is false in `src/main/resources/fhirServer.properties`

## Using OAuth security features

The FHIR server is open by default, but this can be changed in the `fhirServer.properties` file.

First, change the `use_oauth` flag to `true` to turn on security. Then set the `client_id`, `client_secret`, and `oauth_token` fields.

If using Keycloak and following the [REMS Admin](https://github.com/mcode/rems-admin) guide, the `client_id` and `oauth_token` fields can be left as default. The `client_secret` can be found with the following steps:

1. Open the Keycloak admin console (http://localhost:8180/auth) and log in
2. Open the ClientFhirServer, then the `clients` tab, and click `app-token`.
3. Click on the `Credentials` tab, use the `regenerate secret` option if needed.
4. Copy the client secret into the properties file under `client_secret`

Finally, ensure that the [request generator](https://github.com/mcode/request-generator) has the correct username and password in the `properties.json` file. If following the REMS Admin guide, this will be one of the users created when setting up Keycloak.

## Server endpoints

| Relative URL   | Endpoint Description                                   |
| -------------- | ------------------------------------------------------ |
| `/test-ehr/`   | Base server endpoint                                   |
| `/test-ehr/r4` | EHR FHIR Server endpoint (will not resolve in browser) |
| `/test-ehr/script/rxfill` | NCPDP SCRIPT endpoint that RxFill messages from the pharmacy can be sent to (will not resolve in browser) |

## Version

Java v11 or higher is required to run this application.

## Run configurations

Run configurations to run, clean, and load data for the FHIR server can be used for the IntelliJ IDEA CE IDE. You can set breakpoints on the `Boot Run` configuration.

These were last used with IntelliJ IDEA 2021.2.2 (Community Edition) and Microsoft Edge DevTools.

### Steps:

1. Select "Boot Run" as a Run Configuration from the dropdown menu.
2. Put a breakpoint (red dot) on the desired line, e.g. line 43 of `test-ehr/src/main/java/org/hl7/davinci/ehrserver/interceptor/QuestionnaireResponseSearchParameterInterceptor.java`.
3. Click the debug icon (green bug) up top.
4. Go to http://localhost:3000 (where a locally run request-generator is served at) and try to select a patient. You may have to wait for bootRun to finish starting up if you see a "Network Request Failed Error". request-generator will ask test-ehr for QuestionnaireResponse resources (to get the list of in-progress forms).
5. request-generator will pause as the breakpoint at line 43 gets hit. Hit F12 to open up DevTools on http://localhost:3000. You will see the QuestionnaireResponse (and other FHIR resources) have yet to be received.

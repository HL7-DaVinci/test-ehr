package org.hl7.davinci.ehrserver.datainitializer;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.api.dao.IFhirSystemDao;
import ca.uhn.fhir.rest.api.server.SystemRequestDetails;
import jakarta.annotation.PostConstruct;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.util.FileCopyUtils;

import java.nio.charset.StandardCharsets;

@Configuration
public class DataInitializer {

	private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

	@Autowired
	private FhirContext fhirContext;

	@Autowired
	private DataInitializerProperties dataInitializerProperties;

	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private IFhirSystemDao<Bundle, ?> systemDao;

	@PostConstruct
	public void initializeData() {

		if (dataInitializerProperties.getInitialData() == null
				|| dataInitializerProperties.getInitialData().isEmpty()) {
			return;
		}

		logger.info("Initializing data");

		for (String directoryPath : dataInitializerProperties.getInitialData()) {
			logger.info("Loading resources from directory: " + directoryPath);

			Resource[] resources = null;

			try {
				resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
						.getResources("classpath:" + directoryPath + "/**/*.json");
			} catch (Exception e) {
				logger.error("Error loading resources from directory: " + directoryPath, e);
				continue;
			}


			// Using a Bundle transaction to update resources
			// This way we can read the resources in any order and still maintain referential integrity which maintains indexing
			Bundle bundle = new Bundle();
			bundle.setType(Bundle.BundleType.TRANSACTION);

			// Iterate through each resource and add it to the bundle
			for (Resource resource : resources) {
				try {
					String resourceText = new String(
							FileCopyUtils.copyToByteArray(resource.getInputStream()), StandardCharsets.UTF_8);

					IBaseResource fhirResource = fhirContext.newJsonParser().parseResource(resourceText);
					bundle.addEntry().setResource((org.hl7.fhir.r4.model.Resource)fhirResource).setRequest(
							new Bundle.BundleEntryRequestComponent().setMethod(Bundle.HTTPVerb.PUT)
									.setUrl(fhirResource.getIdElement().getValue()));

					logger.info("Adding resource to bundle: " + resource.getFilename() + " with URL: " + fhirResource.getIdElement().getValue());
				} catch (Exception e) {
					logger.error("Error loading resource: " + resource.getFilename(), e);
				}
			}

			// Execute the transaction
			systemDao.transaction(new SystemRequestDetails(), bundle);
		}

	}
}

package org.hl7.davinci.ehrserver.datainitializer;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.api.config.JpaStorageSettings;
import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.starter.AppProperties;
import ca.uhn.fhir.rest.api.server.SystemRequestDetails;
import jakarta.annotation.PostConstruct;
import org.hl7.fhir.instance.model.api.IBaseResource;
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
	private DaoRegistry daoRegistry;

	@Autowired
	private AppProperties appProperties;

	@Autowired
	private DataInitializerProperties dataInitializerProperties;

	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private JpaStorageSettings storageSettings;

	@PostConstruct
	public void initializeData() {

		if (dataInitializerProperties.getInitialData() == null
				|| dataInitializerProperties.getInitialData().isEmpty()) {
			return;
		}

		logger.info("Initializing data");

		// Disable referential integrity checks so that resources can be loaded in any order
		storageSettings.setEnforceReferentialIntegrityOnWrite(false);

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

			for (Resource resource : resources) {
				try {
					String resourceText = new String(
							FileCopyUtils.copyToByteArray(resource.getInputStream()), StandardCharsets.UTF_8);

					IBaseResource fhirResource = fhirContext.newJsonParser().parseResource(resourceText);

					IFhirResourceDao<IBaseResource> dao = daoRegistry.getResourceDao(fhirResource);
					dao.update(fhirResource, new SystemRequestDetails());
					logger.info("Loaded resource: " + resource.getFilename());
				} catch (Exception e) {
					logger.error("Error loading resource: " + resource.getFilename(), e);
				}
			}
		}

		// Re-enable referential integrity checks if they were previously enabled
		storageSettings.setEnforceReferentialIntegrityOnWrite(appProperties.getEnforce_referential_integrity_on_write());
	}
}

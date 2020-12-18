package ca.uhn.fhir.jpa.starter;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.interceptor.api.IInterceptorBroadcaster;
import ca.uhn.fhir.interceptor.api.IInterceptorService;
import ca.uhn.fhir.jpa.binstore.BinaryStorageInterceptor;
import ca.uhn.fhir.jpa.bulk.BulkDataExportProvider;
import ca.uhn.fhir.jpa.dao.DaoConfig;
import ca.uhn.fhir.jpa.dao.DaoRegistry;
import ca.uhn.fhir.jpa.dao.IFhirSystemDao;
import ca.uhn.fhir.jpa.interceptor.CascadingDeleteInterceptor;
import ca.uhn.fhir.jpa.provider.GraphQLProvider;
import ca.uhn.fhir.jpa.provider.JpaConformanceProviderDstu2;
import ca.uhn.fhir.jpa.provider.JpaSystemProviderDstu2;
import ca.uhn.fhir.jpa.provider.SubscriptionTriggeringProvider;
import ca.uhn.fhir.jpa.provider.TerminologyUploaderProvider;
import ca.uhn.fhir.jpa.provider.dstu3.JpaConformanceProviderDstu3;
import ca.uhn.fhir.jpa.provider.dstu3.JpaSystemProviderDstu3;
import ca.uhn.fhir.jpa.provider.r4.JpaConformanceProviderR4;
import ca.uhn.fhir.jpa.provider.r4.JpaSystemProviderR4;
import ca.uhn.fhir.jpa.provider.r5.JpaConformanceProviderR5;
import ca.uhn.fhir.jpa.provider.r5.JpaSystemProviderR5;
import ca.uhn.fhir.jpa.search.DatabaseBackedPagingProvider;
import ca.uhn.fhir.jpa.subscription.SubscriptionInterceptorLoader;
import ca.uhn.fhir.jpa.subscription.module.interceptor.SubscriptionDebugLogInterceptor;
import ca.uhn.fhir.jpa.util.ResourceProviderFactory;
import ca.uhn.fhir.model.dstu2.composite.MetaDt;
import ca.uhn.fhir.narrative.DefaultThymeleafNarrativeGenerator;
import ca.uhn.fhir.rest.server.HardcodedServerAddressStrategy;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.CorsInterceptor;
import ca.uhn.fhir.rest.server.interceptor.LoggingInterceptor;
import ca.uhn.fhir.rest.server.interceptor.RequestValidatingInterceptor;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;
import ca.uhn.fhir.rest.server.interceptor.ResponseValidatingInterceptor;
import ca.uhn.fhir.validation.IValidatorModule;
import ca.uhn.fhir.validation.ResultSeverityEnum;

import java.io.IOException;
import java.util.*;

import org.hl7.davinci.ehrserver.Config;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.Meta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hl7.davinci.ehrserver.ClientAuthorizationInterceptor;
import org.hl7.davinci.ehrserver.ServerConformanceDstu3;
import org.hl7.davinci.ehrserver.ServerConformanceR4;


public class JpaRestfulServer extends RestfulServer {

  private static final long serialVersionUID = 1L;
  static final Logger logger = LoggerFactory.getLogger(JpaRestfulServer.class);

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {\

    if (request.getRequestURI().contains("/token")) {
      // redirect calls to /token to the root /token
      String redirectUrl = Config.get("redirect_post_token");
      logger.info("JpaRestfulServer::doPost: redirect " + request.getRequestURI() + " to " + redirectUrl);
      response.setHeader("Location", redirectUrl);
      response.setStatus(307);

    } else if (request.getRequestURI().contains("/_services/smart/Launch")) {
      // redirect calls to /_services/smart/Launch to the root /_services/smart/Launch
      String redirectUrl = Config.get("redirect_post_launch");
      logger.info("JpaRestfulServer::doPost: redirect " + request.getRequestURI() + " to " + redirectUrl);
      response.setHeader("Location", redirectUrl);
      response.setStatus(307);

    } else {
      // forward on all other get requests
      super.doPost(request, response);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void initialize() throws ServletException {
    super.initialize();

    /*
     * Create a FhirContext object that uses the version of FHIR
     * specified in the properties file.
     */
    ApplicationContext appCtx = (ApplicationContext) getServletContext()
        .getAttribute("org.springframework.web.context.WebApplicationContext.ROOT");
    // Customize supported resource types
    Set<String> supportedResourceTypes = HapiProperties.getSupportedResourceTypes();

    if (!supportedResourceTypes.isEmpty() && !supportedResourceTypes.contains("SearchParameter")) {
        supportedResourceTypes.add("SearchParameter");
    }

    if (!supportedResourceTypes.isEmpty()) {
      DaoRegistry daoRegistry = appCtx.getBean(DaoRegistry.class);
      daoRegistry.setSupportedResourceTypes(supportedResourceTypes);
    }

    /*
     * ResourceProviders are fetched from the Spring context
     */
    FhirVersionEnum fhirVersion = HapiProperties.getFhirVersion();
    ResourceProviderFactory resourceProviders;
    Object systemProvider;
    if (fhirVersion == FhirVersionEnum.DSTU2) {
      resourceProviders = appCtx.getBean("myResourceProvidersDstu2", ResourceProviderFactory.class);
      systemProvider = appCtx.getBean("mySystemProviderDstu2", JpaSystemProviderDstu2.class);
    } else if (fhirVersion == FhirVersionEnum.DSTU3) {
      resourceProviders = appCtx.getBean("myResourceProvidersDstu3", ResourceProviderFactory.class);
      systemProvider = appCtx.getBean("mySystemProviderDstu3", JpaSystemProviderDstu3.class);
    } else if (fhirVersion == FhirVersionEnum.R4) {
      resourceProviders = appCtx.getBean("myResourceProvidersR4", ResourceProviderFactory.class);
      systemProvider = appCtx.getBean("mySystemProviderR4", JpaSystemProviderR4.class);
    } else if (fhirVersion == FhirVersionEnum.R5) {
      resourceProviders = appCtx.getBean("myResourceProvidersR5", ResourceProviderFactory.class);
      systemProvider = appCtx.getBean("mySystemProviderR5", JpaSystemProviderR5.class);
    } else {
      throw new IllegalStateException();
    }

    setFhirContext(appCtx.getBean(FhirContext.class));

    registerProviders(resourceProviders.createProviders());
    registerProvider(systemProvider);

    /*
     * The conformance provider exports the supported resources, search parameters, etc for
     * this server. The JPA version adds resourceProviders counts to the exported statement, so it
     * is a nice addition.
     *
     * You can also create your own subclass of the conformance provider if you need to
     * provide further customization of your server's CapabilityStatement
     */
    if (fhirVersion == FhirVersionEnum.DSTU2) {
      IFhirSystemDao<ca.uhn.fhir.model.dstu2.resource.Bundle, MetaDt> systemDao = appCtx
          .getBean("mySystemDaoDstu2", IFhirSystemDao.class);
      JpaConformanceProviderDstu2 confProvider = new JpaConformanceProviderDstu2(this, systemDao,
          appCtx.getBean(DaoConfig.class));
      confProvider.setImplementationDescription("HAPI FHIR DSTU2 Server");
      setServerConformanceProvider(confProvider);
    } else if (fhirVersion == FhirVersionEnum.DSTU3) {
      IFhirSystemDao<Bundle, Meta> systemDao = appCtx
          .getBean("mySystemDaoDstu3", IFhirSystemDao.class);
      ServerConformanceDstu3 confProvider = new ServerConformanceDstu3(this, systemDao,
          appCtx.getBean(DaoConfig.class));
      confProvider.setImplementationDescription("HAPI FHIR DSTU3 Server");
      setServerConformanceProvider(confProvider);
    } else if (fhirVersion == FhirVersionEnum.R4) {
      IFhirSystemDao<org.hl7.fhir.r4.model.Bundle, org.hl7.fhir.r4.model.Meta> systemDao = appCtx
          .getBean("mySystemDaoR4", IFhirSystemDao.class);
      ServerConformanceR4 confProvider = new ServerConformanceR4(this, systemDao,
          appCtx.getBean(DaoConfig.class));
      confProvider.setImplementationDescription("HAPI FHIR R4 Server");
      setServerConformanceProvider(confProvider);
    } else if (fhirVersion == FhirVersionEnum.R5) {
      IFhirSystemDao<org.hl7.fhir.r5.model.Bundle, org.hl7.fhir.r5.model.Meta> systemDao = appCtx
          .getBean("mySystemDaoR5", IFhirSystemDao.class);
      JpaConformanceProviderR5 confProvider = new JpaConformanceProviderR5(this, systemDao,
          appCtx.getBean(DaoConfig.class));
      confProvider.setImplementationDescription("HAPI FHIR R5 Server");
      setServerConformanceProvider(confProvider);
    } else {
      throw new IllegalStateException();
    }

    /*
     * ETag Support
     */
    setETagSupport(HapiProperties.getEtagSupport());

    /*
     * This server tries to dynamically generate narratives
     */
    FhirContext ctx = getFhirContext();
    ctx.setNarrativeGenerator(new DefaultThymeleafNarrativeGenerator());

    /*
     * Default to JSON and pretty printing
     */
    setDefaultPrettyPrint(HapiProperties.getDefaultPrettyPrint());

    /*
     * Default encoding
     */
    setDefaultResponseEncoding(HapiProperties.getDefaultEncoding());

    /*
     * This configures the server to page search results to and from
     * the database, instead of only paging them to memory. This may mean
     * a performance hit when performing searches that return lots of results,
     * but makes the server much more scalable.
     */
    setPagingProvider(appCtx.getBean(DatabaseBackedPagingProvider.class));

    /*
     * This interceptor formats the output using nice colourful
     * HTML output when the request is detected to come from a
     * browser.
     */
    ResponseHighlighterInterceptor responseHighlighterInterceptor = new ResponseHighlighterInterceptor();
    ;
    this.registerInterceptor(responseHighlighterInterceptor);

    /*
     * Add some logging for each request
     */
    LoggingInterceptor loggingInterceptor = new LoggingInterceptor();
    loggingInterceptor.setLoggerName(HapiProperties.getLoggerName());
    loggingInterceptor.setMessageFormat(HapiProperties.getLoggerFormat());
    loggingInterceptor.setErrorMessageFormat(HapiProperties.getLoggerErrorFormat());
    loggingInterceptor.setLogExceptions(HapiProperties.getLoggerLogExceptions());
    this.registerInterceptor(loggingInterceptor);

    ClientAuthorizationInterceptor clientAuthorizationInterceptor = new ClientAuthorizationInterceptor();
    this.registerInterceptor(clientAuthorizationInterceptor);

    /*
     * If you are hosting this server at a specific DNS name, the server will try to
     * figure out the FHIR base URL based on what the web container tells it, but
     * this doesn't always work. If you are setting links in your search bundles that
     * just refer to "localhost", you might want to use a server address strategy:
     */
    String serverAddress = HapiProperties.getServerAddress();
    if (serverAddress != null && serverAddress.length() > 0) {
      setServerAddressStrategy(new HardcodedServerAddressStrategy(serverAddress));
    }

    /*
     * If you are using DSTU3+, you may want to add a terminology uploader, which allows
     * uploading of external terminologies such as Snomed CT. Note that this uploader
     * does not have any security attached (any anonymous user may use it by default)
     * so it is a potential security vulnerability. Consider using an AuthorizationInterceptor
     * with this feature.
     */
    if (false) { // <-- DISABLED RIGHT NOW
      registerProvider(appCtx.getBean(TerminologyUploaderProvider.class));
    }

    // If you want to enable the $trigger-subscription operation to allow
    // manual triggering of a subscription delivery, enable this provider
    if (false) { // <-- DISABLED RIGHT NOW
      SubscriptionTriggeringProvider retriggeringProvider = appCtx
          .getBean(SubscriptionTriggeringProvider.class);
      registerProvider(retriggeringProvider);
    }

    // Define your CORS configuration. This is an example
    // showing a typical setup. You should customize this
    // to your specific needs
    if (HapiProperties.getCorsEnabled()) {
      CorsConfiguration config = new CorsConfiguration();
      config.addAllowedHeader(HttpHeaders.ORIGIN);
      config.addAllowedHeader(HttpHeaders.ACCEPT);
      config.addAllowedHeader(HttpHeaders.CONTENT_TYPE);
      config.addAllowedHeader(HttpHeaders.AUTHORIZATION);
      config.addAllowedHeader(HttpHeaders.CACHE_CONTROL);
      config.addAllowedHeader("x-fhir-starter");
      config.addAllowedHeader("X-Requested-With");
      config.addAllowedHeader("Prefer");
      String allAllowedCORSOrigins = HapiProperties.getCorsAllowedOrigin();
      Arrays.stream(allAllowedCORSOrigins.split(",")).forEach(o -> {
        config.addAllowedOrigin(o);
      });
      config.addAllowedOrigin(HapiProperties.getCorsAllowedOrigin());

      config.addExposedHeader("Location");
      config.addExposedHeader("Content-Location");
      config.setAllowedMethods(
          Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
      config.setAllowCredentials(HapiProperties.getCorsAllowedCredentials());

      // Create the interceptor and register it
      CorsInterceptor interceptor = new CorsInterceptor(config);
      registerInterceptor(interceptor);
    }

    // If subscriptions are enabled, we want to register the interceptor that
    // will activate them and match results against them
    if (HapiProperties.getSubscriptionWebsocketEnabled() ||
        HapiProperties.getSubscriptionEmailEnabled() ||
        HapiProperties.getSubscriptionRestHookEnabled()) {
      // Loads subscription interceptors (SubscriptionActivatingInterceptor, SubscriptionMatcherInterceptor)
      // with activation of scheduled subscription
      SubscriptionInterceptorLoader subscriptionInterceptorLoader = appCtx
          .getBean(SubscriptionInterceptorLoader.class);
      subscriptionInterceptorLoader.registerInterceptors();

      // Subscription debug logging
      IInterceptorService interceptorService = appCtx.getBean(IInterceptorService.class);
      interceptorService.registerInterceptor(new SubscriptionDebugLogInterceptor());
    }

    // Cascading deletes
    DaoRegistry daoRegistry = appCtx.getBean(DaoRegistry.class);
    IInterceptorBroadcaster interceptorBroadcaster = appCtx.getBean(IInterceptorBroadcaster.class);
    if (HapiProperties.getAllowCascadingDeletes()) {
      CascadingDeleteInterceptor cascadingDeleteInterceptor = new CascadingDeleteInterceptor(
          daoRegistry, interceptorBroadcaster);
      getInterceptorService().registerInterceptor(cascadingDeleteInterceptor);
    }

    // Binary Storage
    if (HapiProperties.isBinaryStorageEnabled()) {
      BinaryStorageInterceptor binaryStorageInterceptor = appCtx
          .getBean(BinaryStorageInterceptor.class);
      getInterceptorService().registerInterceptor(binaryStorageInterceptor);
    }

    // Validation
    IValidatorModule validatorModule;
    switch (fhirVersion) {
            case DSTU2:
                validatorModule = appCtx.getBean("myInstanceValidatorDstu2", IValidatorModule.class);
                break;
      case DSTU3:
        validatorModule = appCtx.getBean("myInstanceValidatorDstu3", IValidatorModule.class);
        break;
      case R4:
        validatorModule = appCtx.getBean("myInstanceValidatorR4", IValidatorModule.class);
        break;
      case R5:
        validatorModule = appCtx.getBean("myInstanceValidatorR5", IValidatorModule.class);
        break;
            // These versions are not supported by HAPI FHIR JPA
            case DSTU2_HL7ORG:
            case DSTU2_1:
      default:
        validatorModule = null;
        break;
    }
    if (validatorModule != null) {
      if (HapiProperties.getValidateRequestsEnabled()) {
        RequestValidatingInterceptor interceptor = new RequestValidatingInterceptor();
        interceptor.setFailOnSeverity(ResultSeverityEnum.ERROR);
        interceptor.setValidatorModules(Collections.singletonList(validatorModule));
        registerInterceptor(interceptor);
      }
      if (HapiProperties.getValidateResponsesEnabled()) {
        ResponseValidatingInterceptor interceptor = new ResponseValidatingInterceptor();
        interceptor.setFailOnSeverity(ResultSeverityEnum.ERROR);
        interceptor.setValidatorModules(Collections.singletonList(validatorModule));
        registerInterceptor(interceptor);
      }
    }

    // GraphQL
    if (HapiProperties.getGraphqlEnabled()) {
      if (fhirVersion.isEqualOrNewerThan(FhirVersionEnum.DSTU3)) {
        registerProvider(appCtx.getBean(GraphQLProvider.class));
      }
    }

    if (!HapiProperties.getAllowedBundleTypes().isEmpty()) {
      String allowedBundleTypesString = HapiProperties.getAllowedBundleTypes();
      Set<String> allowedBundleTypes = new HashSet<>();
      Arrays.stream(allowedBundleTypesString.split(",")).forEach(o -> {
        BundleType type = BundleType.valueOf(o);
        allowedBundleTypes.add(type.toCode());
      });
      DaoConfig config = appCtx.getBean(DaoConfig.class);
      config.setBundleTypesAllowedForStorage(
          Collections.unmodifiableSet(new TreeSet<>(allowedBundleTypes)));
    }
  
        // Bulk Export
        if (HapiProperties.getBulkExportEnabled()) {
            registerProvider(appCtx.getBean(BulkDataExportProvider.class));
        }

    }

}

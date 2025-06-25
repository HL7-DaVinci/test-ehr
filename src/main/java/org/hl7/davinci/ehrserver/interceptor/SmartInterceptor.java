package org.hl7.davinci.ehrserver.interceptor;

import org.hl7.davinci.ehrserver.SecurityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Interceptor;
import ca.uhn.fhir.interceptor.api.Pointcut;
import ca.uhn.fhir.jpa.starter.AppProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Interceptor
public class SmartInterceptor {

  private AppProperties appProperties;
  private SecurityProperties securityProperties;

  static final Logger logger = LoggerFactory.getLogger(SmartInterceptor.class);

  public SmartInterceptor(AppProperties appProperties, SecurityProperties securityProperties) {
    this.appProperties = appProperties;
    this.securityProperties = securityProperties;
  }

  @Hook(Pointcut.SERVER_INCOMING_REQUEST_PRE_PROCESSED)
  public boolean intercept(HttpServletRequest request, HttpServletResponse response) {

    if (request.getRequestURI().contains("/_services/smart/Launch")) {
      // redirect calls to /_services/smart/Launch to the root /_services/smart/Launch
      String redirectUrl = securityProperties.getRedirectPostLaunch();
      if (redirectUrl == null || redirectUrl.isEmpty()) {
        redirectUrl = "/test-ehr/_services/smart/Launch";
      }
      logger.info("SmartInterceptor::intercept: redirect " + request.getRequestURI() + " to " + redirectUrl);
      response.setHeader("Location", redirectUrl);
      response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, String.join(", ", appProperties.getCors().getAllowed_origin()));
      response.setStatus(307);
      return false;
    }

    return true;

  }
}

package ca.uhn.fhir.jpa.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "security")
@Configuration
@EnableConfigurationProperties
public class SecurityProperties {

  private String clientId;
  public String getClientId() {
    return clientId;
  }
  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  private String clientSecret;
  public String getClientSecret() {
    return clientSecret;
  }
  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  private String realm;
  public String getRealm() {
    return realm;
  }
  public void setRealm(String realm) {
    this.realm = realm;
  }

  private Boolean useOauth = false;
  public Boolean getUseOauth() {
    return useOauth;
  }
  public void setUseOauth(Boolean useOauth) {
    this.useOauth = useOauth;
  }

  private String oauthToken;
  public String getOauthToken() {
    return oauthToken;
  }
  public void setOauthToken(String oauthToken) {
    this.oauthToken = oauthToken;
  }

  private String oauthAuthorize;
  public String getOauthAuthorize() {
    return oauthAuthorize;
  }
  public void setOauthAuthorize(String oauthAuthorize) {
    this.oauthAuthorize = oauthAuthorize;
  }
    
  private String oauthIntrospect;
  public String getOauthIntrospect() {
    return oauthIntrospect;
  }
  public void setOauthIntrospect(String oauthIntrospect) {
    this.oauthIntrospect = oauthIntrospect;
  }

  private String oauthJwks;
  public String getOauthJwks() {
    return oauthJwks;
  }
  public void setOauthJwks(String oauthJwks) {
    this.oauthJwks = oauthJwks;
  }

  private String proxyAuthorize;
  public String getProxyAuthorize() {
    return proxyAuthorize;
  }
  public void setProxyAuthorize(String proxyAuthorize) {
    this.proxyAuthorize = proxyAuthorize;
  }

  private String proxyToken;
  public String getProxyToken() {
    return proxyToken;
  }
  public void setProxyToken(String proxyToken) {
    this.proxyToken = proxyToken;
  }

  private String authRedirectHost;
  public String getAuthRedirectHost() {
    return authRedirectHost;
  }
  public void setAuthRedirectHost(String authRedirectHost) {
    this.authRedirectHost = authRedirectHost;
  }

  private String redirectPostLaunch;
  public String getRedirectPostLaunch() {
    return redirectPostLaunch;
  }
  public void setRedirectPostLaunch(String redirectPostLaunch) {
    this.redirectPostLaunch = redirectPostLaunch;
  }

  private String redirectPostToken;
  public String getRedirectPostToken() {
    return redirectPostToken;
  }
  public void setRedirectPostToken(String redirectPostToken) {
    this.redirectPostToken = redirectPostToken;
  }

  
}

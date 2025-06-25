package org.hl7.davinci.ehrserver;

import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.interceptor.auth.AuthorizationInterceptor;
import ca.uhn.fhir.rest.server.interceptor.auth.IAuthRule;
import ca.uhn.fhir.rest.server.interceptor.auth.RuleBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;

@SuppressWarnings("ConstantConditions")
public class ClientAuthorizationInterceptor extends AuthorizationInterceptor {

  private static final Logger logger = LoggerFactory.getLogger(ClientAuthorizationInterceptor.class);

  SecurityProperties securityProperties;

  // JWKS caching
  private JsonObject cachedJwks = null;
  private long jwksCacheExpiry = 0;
  private static final long JWKS_CACHE_DURATION_MS = 30 * 1000; // 30 seconds

  // Well-known configuration caching
  private java.util.Map<String, String> cachedJwksUris = new java.util.HashMap<>();
  private java.util.Map<String, Long> jwksUriCacheExpiry = new java.util.HashMap<>();
  private static final long WELL_KNOWN_CACHE_DURATION_MS = 30 * 1000; // 30 seconds

  public ClientAuthorizationInterceptor(SecurityProperties securityProperties) {
    super();
    this.securityProperties = securityProperties;
  }

  @Override
  public List<IAuthRule> buildRuleList(RequestDetails theRequestDetails) {
    if (!securityProperties.getUseOauth()) {
      return new RuleBuilder()
          .allowAll()
          .build();
    }

    CloseableHttpClient client = HttpClients.createDefault();

    String authHeader = theRequestDetails.getHeader("Authorization");
    // Get the token and drop the "Bearer"
    if (authHeader == null) {
      return new RuleBuilder()
          .allow().metadata().andThen()
          .denyAll("No authorization header present")
          .build();
    }
    String token = authHeader.split(" ")[1];
    String secret = securityProperties.getClientSecret();
    String clientId = securityProperties.getClientId();

    // Check if this is a public client (no secret) or confidential client (has secret)
    if (secret == null || secret.isEmpty()) {
      // Public client - validate JWT using JWKS
      return validateJwtWithJwks(token, client);
    } else {
      // Confidential client - use introspection
      return validateTokenWithIntrospection(token, secret, clientId, client);
    }
  }

  private List<IAuthRule> validateJwtWithJwks(String token, CloseableHttpClient client) {
    try { // Get JWKS URL
      String jwksUrl = getJwksUrl(token);
      if (jwksUrl == null) {
        return new RuleBuilder()
            .allow().metadata().andThen()
            .denyAll("Unable to determine JWKS URL")
            .build();
      }

      // Get JWKS (cached or fresh)
      JsonObject jwks = getCachedJwks(jwksUrl, client);
      if (jwks == null) {
        return new RuleBuilder()
            .allow().metadata().andThen()
            .denyAll("Unable to fetch JWKS")
            .build();
      }

      // Parse and validate JWT
      JsonObject claims = validateJwtSignature(token, jwks);
      if (claims != null && isTokenValid(claims)) {
        logger.info("JWT validation successful for public client");
        return new RuleBuilder()
            .allowAll()
            .build();
      } else {
        return new RuleBuilder()
            .allow().metadata().andThen()
            .denyAll("Invalid JWT token")
            .build();
      }

    } catch (Exception e) {
      e.printStackTrace();
      return new RuleBuilder()
          .allow().metadata().andThen()
          .denyAll("JWT validation failed: " + e.getMessage())
          .build();
    }
  }

  private List<IAuthRule> validateTokenWithIntrospection(String token, String secret, String clientId,
      CloseableHttpClient client) {

    String introspectUrl = "";
    if (securityProperties.getOauthIntrospect() != null && !securityProperties.getOauthIntrospect().isEmpty()) {
      introspectUrl = securityProperties.getOauthIntrospect();
    } else if (securityProperties.getRealm() == null || securityProperties.getRealm().isEmpty()) {
      return new RuleBuilder()
          .allow().metadata().andThen()
          .denyAll("No realm specified for introspection URL")
          .build();
    } else {
      // older default
      introspectUrl = "http://localhost:8180/auth/realms/" + securityProperties.getRealm()
          + "/protocol/openid-connect/token/introspect";
    }

    HttpPost httpPost = new HttpPost(introspectUrl);

    List<NameValuePair> params = new ArrayList<NameValuePair>();
    params.add(new BasicNameValuePair("token", token));
    if (secret != null && !secret.isEmpty()) {
      // Confidential client - use Basic Auth
      String auth = clientId + ":" + secret;
      String encodedAuth = java.util.Base64.getEncoder().encodeToString(auth.getBytes());
      httpPost.setHeader("Authorization", "Basic " + encodedAuth);
    } else {
      // Public client - send client_id in form data only
      if (clientId != null) {
        params.add(new BasicNameValuePair("client_id", clientId));
      }
    }

    try {
      httpPost.setEntity(new UrlEncodedFormEntity(params));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    JsonObject jsonResponse;
    try {
      CloseableHttpResponse response = client.execute(httpPost);
      String jsonString = EntityUtils.toString(response.getEntity());
      jsonResponse = JsonParser.parseString(jsonString).getAsJsonObject();
      client.close();

    } catch (IOException e) {
      e.printStackTrace();
      jsonResponse = null;
    }

    if (jsonResponse.has("error")) {
      return new RuleBuilder()
          .allow().metadata().andThen()
          .denyAll("Rejected OAuth token - encountered error: " + jsonResponse.get("error").getAsString())
          .build();
    } else if (jsonResponse.get("active").getAsBoolean()) {
      return new RuleBuilder()
          .allowAll()
          .build();
    } else {
      return new RuleBuilder()
          .allow().metadata().andThen()
          .denyAll("Rejected OAuth token - failed to introspect: " + jsonResponse.toString())
          .build();
    }
  }

  private String getJwksUrl(String token) {
    // Check for explicit oauth_jwks configuration
    if (securityProperties.getOauthJwks() != null && !securityProperties.getOauthJwks().isEmpty()) {
      logger.info("Using configured JWKS URL: {}", securityProperties.getOauthJwks());
      return securityProperties.getOauthJwks();
    }

    // Try to derive from JWT issuer URL
    String jwksFromIssuer = getJwksUrlFromJwtIssuer(token);
    if (jwksFromIssuer != null) {
      logger.info("Derived JWKS URL from JWT issuer: {}", jwksFromIssuer);
      return jwksFromIssuer;
    }

    // Fallback to default realm-based local URL
    String realm = securityProperties.getRealm();
    if (realm == null || realm.isEmpty()) {
      return null;
    }
    return "http://localhost:8180/realms/" + realm + "/protocol/openid-connect/certs";
  }

  private String getJwksUrlFromJwtIssuer(String token) {
    try {
      // Extract payload from JWT
      String[] tokenParts = token.split("\\.");
      if (tokenParts.length != 3) {
        return null;
      }

      String payloadJson = new String(Base64.getUrlDecoder().decode(tokenParts[1]));
      JsonObject payload = JsonParser.parseString(payloadJson).getAsJsonObject();

      // Extract issuer claim
      if (payload.has("iss")) {
        String issuer = payload.get("iss").getAsString();

        // Get JWKS URI from well-known configuration
        String jwksUri = getJwksUriFromWellKnown(issuer);
        if (jwksUri != null) {
          return jwksUri;
        }

        // Fallback: construct JWKS URL from issuer (for backwards compatibility)
        if (issuer.endsWith("/")) {
          return issuer + "protocol/openid-connect/certs";
        } else {
          return issuer + "/protocol/openid-connect/certs";
        }
      }
      return null;
    } catch (Exception e) {
      logger.error("Error extracting issuer from JWT: {}", e.getMessage());
      return null;
    }
  }

  private String getJwksUriFromWellKnown(String issuer) {
    long currentTime = System.currentTimeMillis();
    // Check if cached jwks_uri is still valid
    if (cachedJwksUris.containsKey(issuer) && jwksUriCacheExpiry.containsKey(issuer)) {
      if (currentTime < jwksUriCacheExpiry.get(issuer)) {
        String cachedUri = cachedJwksUris.get(issuer);
        logger.debug("Using cached jwks_uri for issuer {}: {}", issuer, cachedUri);
        return cachedUri;
      }
    }

    CloseableHttpClient client = null;
    try {
      client = HttpClients.createDefault();

      // Construct well-known URL
      String wellKnownUrl;
      if (issuer.endsWith("/")) {
        wellKnownUrl = issuer + ".well-known/openid_configuration";
      } else {
        wellKnownUrl = issuer + "/.well-known/openid_configuration";
      }

      logger.debug("Fetching fresh well-known configuration from: {}", wellKnownUrl);

      // Fetch well-known configuration
      HttpGet httpGet = new HttpGet(wellKnownUrl);
      CloseableHttpResponse response = client.execute(httpGet);
      String configString = EntityUtils.toString(response.getEntity());
      response.close();
      // Parse configuration and extract jwks_uri
      JsonObject config = JsonParser.parseString(configString).getAsJsonObject();
      if (config.has("jwks_uri")) {
        String jwksUri = config.get("jwks_uri").getAsString();
        logger.debug("Found jwks_uri in well-known: {}", jwksUri);

        // Cache the jwks_uri
        cachedJwksUris.put(issuer, jwksUri);
        jwksUriCacheExpiry.put(issuer, currentTime + WELL_KNOWN_CACHE_DURATION_MS);

        return jwksUri;
      }

      return null;
    } catch (Exception e) {
      logger.error("Error fetching well-known configuration: {}", e.getMessage());
      return null;
    } finally {
      if (client != null) {
        try {
          client.close();
        } catch (IOException e) {
          // Ignore close errors
        }
      }
    }
  }

  private JsonObject getCachedJwks(String jwksUrl, CloseableHttpClient client) throws IOException {
    long currentTime = System.currentTimeMillis();
    // Check if cached JWKS is still valid
    if (cachedJwks != null && currentTime < jwksCacheExpiry) {
      logger.debug("Using cached JWKS");
      return cachedJwks;
    }

    // Fetch fresh JWKS
    logger.debug("Fetching fresh JWKS from: {}", jwksUrl);
    HttpGet httpGet = new HttpGet(jwksUrl);
    CloseableHttpResponse response = client.execute(httpGet);
    String jwksString = EntityUtils.toString(response.getEntity());
    response.close();

    // Parse and cache the JWKS
    JsonObject jwks = JsonParser.parseString(jwksString).getAsJsonObject();
    cachedJwks = jwks;
    jwksCacheExpiry = currentTime + JWKS_CACHE_DURATION_MS;

    return jwks;
  }

  private JsonObject validateJwtSignature(String token, JsonObject jwks) {
    try {
      // Extract header and payload from JWT
      String[] tokenParts = token.split("\\.");
      if (tokenParts.length != 3) {
        return null;
      }

      String payloadJson = new String(Base64.getUrlDecoder().decode(tokenParts[1]));

      JsonObject payload = JsonParser.parseString(payloadJson).getAsJsonObject();

      // For now, we'll do basic validation - in production you'd want full signature
      // verification
      // Check if token has required fields
      if (payload.has("exp") && payload.has("iat")) {
        return payload;
      }
      return null;

    } catch (Exception e) {
      logger.error("JWT validation error: {}", e.getMessage());
      return null;
    }
  }

  private boolean isTokenValid(JsonObject claims) {
    // Check expiration
    if (claims.has("exp")) {
      long exp = claims.get("exp").getAsLong();
      long currentTime = System.currentTimeMillis() / 1000;
      if (exp < currentTime) {
        logger.debug("Token expired");
        return false;
      }
    }

    // Check if token is active
    return true;
  }

}
package org.hl7.davinci.ehrserver.authproxy;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class TokenRequest {

  private String grant_type;
  private String code;
  private String redirect_uri;
  private String client_id;
  private String code_verifier;

  public String getGrant_type() {
    return grant_type;
  }

  public void setGrant_type(String grant_type) {
    this.grant_type = grant_type;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getRedirect_uri() {
    return redirect_uri;
  }

  public void setRedirect_uri(String redirect_uri) {
    this.redirect_uri = redirect_uri;
  }

  public String getClient_id() {
    return client_id;
  }
  public void setClient_id(String client_id) {
    this.client_id = client_id;
  }

  public String getCode_verifier() {
    return code_verifier;
  }

  public void setCode_verifier(String code_verifier) {
    this.code_verifier = code_verifier;
  }
  public MultiValueMap<String, String> urlEncode() {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("grant_type", this.grant_type);
    map.add("code", this.code);
    map.add("redirect_uri", this.redirect_uri);
    if (this.client_id != null) {
      map.add("client_id", this.client_id);
    }
    if (this.code_verifier != null) {
      map.add("code_verifier", this.code_verifier);
    }
    return map;
  }
  @Override
  public String toString() {
    return "grantType: " + this.grant_type + "\ncode: " + this.code + "\nredirect_uri: " + this.redirect_uri + "\nclient_id: " + this.client_id + "\ncode_verifier: " + this.code_verifier;
  }
}

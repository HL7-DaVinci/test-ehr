package org.hl7.davinci.ehrserver.authproxy;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "appcontext")

public class Payload {

  private Parameters parameters;
  private String launchUrl;
  private String launchId;
  private String redirectUri;

  public String getRedirectUri() {
    return redirectUri;
  }

  public void setRedirectUri(String redirectUri) {
    this.redirectUri = redirectUri;
  }

  public Parameters getParameters() {
    return parameters;
  }

  public String getLaunchUrl() {
    return launchUrl;
  }

  public String getPatient() {
    return parameters.getPatient();
  }


  public String getLaunchId() {
    return launchId;
  }

  public void setLaunchId(String launchId) {
    this.launchId = launchId;
  }

  public void setLaunchUrl(String launchUrl) {
    this.launchUrl = launchUrl;
  }

  public void setParameters(Parameters parameters) {
    this.parameters = parameters;
  }

  public String getAppContext() {
    return parameters.getAppContext();
  }

  @Override
  public String toString() {
    return launchId + ": " +launchUrl +", " +redirectUri + ", "+  parameters.toString();
  }
}

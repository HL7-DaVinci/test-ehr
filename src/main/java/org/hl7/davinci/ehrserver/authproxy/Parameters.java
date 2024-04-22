package org.hl7.davinci.ehrserver.authproxy;

public class Parameters {
  private String patient;
  private String appContext;
  private String fhirContext;


  public String getAppContext() {
    return appContext;
  }

  public String getPatient() {
    return patient;
  }

  public void setAppContext(String appContext) {
    this.appContext = appContext;
  }

  public void setPatient(String patient) {
    this.patient = patient;
  }

  public String getFhirContext() {
    return fhirContext;
  }
  public void setFhirContext(String fhirContext) {
    this.fhirContext = fhirContext;
  }

  @Override
  public String toString() {
    return patient + ", "  + appContext;
  }
}

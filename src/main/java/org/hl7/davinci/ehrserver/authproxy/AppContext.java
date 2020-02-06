package org.hl7.davinci.ehrserver.authproxy;

public class AppContext {

  private String template;
  private String request;
  private String priorauth;
  private String filepath;

  public String getRequest() {
    return request;
  }

  public String getTemplate() {
    return template;
  }

  public String getPriorauth() { return priorauth; }

  public String getFilepath() { return filepath; }

  public void setRequest(String request) {
    this.request = request;
  }

  public void setTemplate(String template) {
    this.template = template;
  }

  public void setPriorauth(String priorauth) {
    this.priorauth = priorauth;
  }

  public void setFilepath(String filepath) {
    this.filepath = filepath;
  }

  @Override
  public String toString() {
    return "template=" + template + "&request=" + request + "&priorauth=" + priorauth + "&filepath=" + filepath;
  }
}

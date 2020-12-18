package org.hl7.davinci.ehrserver.authproxy;


import java.util.UUID;

public class AuthResponse {
  private String launch_id;

  AuthResponse() {
    this.launch_id = UUID.randomUUID().toString();
  }

  public String getlaunch_id() {
    return launch_id;
  }



}

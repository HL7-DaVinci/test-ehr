package org.hl7.davinci.ehrserver.authproxy;

public interface PayloadDAO {

  public void createPayload(Payload payload);

  // get a user with the passed id
  public Payload getPayload(String id);

  // update launch id to be a code
  public void updateCode(String launchId, String code);

  // get the context
  public Payload findContextByCode(String code);

  public void updateRedirect(String launchId, String redirectUri);
}

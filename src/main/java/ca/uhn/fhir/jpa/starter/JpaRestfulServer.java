package ca.uhn.fhir.jpa.starter;

import org.hl7.davinci.ehrserver.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Import(AppProperties.class)
public class JpaRestfulServer extends BaseJpaRestfulServer {

  @Autowired
  AppProperties appProperties;

  private static final long serialVersionUID = 1L;
  static final Logger logger = LoggerFactory.getLogger(JpaRestfulServer.class);

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    if (request.getRequestURI().contains("/_services/smart/Launch")) {
      // redirect calls to /_services/smart/Launch to the root /_services/smart/Launch
      String redirectUrl = Config.get("redirect_post_launch");
      logger.info("JpaRestfulServer::doPost: redirect " + request.getRequestURI() + " to " + redirectUrl);
      response.setHeader("Location", redirectUrl);
      response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, String.join(", ", appProperties.getCors().getAllowed_origin()));
      response.setStatus(307);

    } else {
      // forward on all other get requests
      super.doPost(request, response);
    }
  }
  public JpaRestfulServer() {
    super();
  }


  @Override
  protected void initialize() throws ServletException {
    super.initialize();

    }

}

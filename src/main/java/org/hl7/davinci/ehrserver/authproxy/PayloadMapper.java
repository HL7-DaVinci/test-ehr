package org.hl7.davinci.ehrserver.authproxy;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PayloadMapper implements RowMapper<Payload> {

  @Override
  public Payload mapRow(ResultSet rs, int rowNum) throws SQLException {
    Payload payload = new Payload();
    payload.setLaunchId(rs.getString("launchId"));
    payload.setLaunchUrl(rs.getString("launchUrl"));
    payload.setRedirectUri(rs.getString("redirectUri"));
    Parameters parameters = new Parameters();
    parameters.setPatient(rs.getString("patient"));
    parameters.setAppContext(rs.getString("appContext"));
    payload.setParameters(parameters);
    return payload;
  }
}

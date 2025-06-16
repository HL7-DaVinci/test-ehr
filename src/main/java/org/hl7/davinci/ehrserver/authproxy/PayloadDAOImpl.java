package org.hl7.davinci.ehrserver.authproxy;

import org.hl7.davinci.ehrserver.requestgenerator.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class PayloadDAOImpl implements PayloadDAO {

  static final Logger logger = LoggerFactory.getLogger(PayloadDAOImpl.class);

  @Autowired
  private DataSource dataSource;

  private JdbcTemplate jdbcTemplate;


  @PostConstruct
  public void setJdbcTemplate() throws SQLException {
    System.setProperty("derby.stream.error.file", new File("logs/derby.log").getAbsolutePath());
    jdbcTemplate = new JdbcTemplate(dataSource);
    String dbUrl = "jdbc:derby:directory:target/jpaserver_derby_files;create=true";
    Connection conn = DriverManager.getConnection(dbUrl);
    // create table
    try {
      jdbcTemplate.execute("Create table appcontext (launchId varchar(255) primary key, launchUrl varchar(212) NOT NULL, patient varchar(128), appContext varchar(8192), fhirContext varchar(8192), launchCode varchar(512), redirectUri varchar(512), codeVerifier varchar(128), codeChallenge varchar(128))");

      logger.info("PayloadDAOImpl: AppContext table created in database");

    } catch (Exception e) {
      System.out.println(e);
      logger.info("PayloadDAOImpl: AppContext table already exists, checking for PKCE columns");
      
      // Try to add PKCE columns if they don't exist
      try {
        jdbcTemplate.execute("ALTER TABLE appcontext ADD COLUMN codeVerifier varchar(128)");
        jdbcTemplate.execute("ALTER TABLE appcontext ADD COLUMN codeChallenge varchar(128)");
        logger.info("PayloadDAOImpl: Added PKCE columns to existing table");
      } catch (Exception addColumnException) {
        logger.info("PayloadDAOImpl: PKCE columns already exist or could not be added");
      }
    }
    conn.close();
  }

  @Override
  public void createPayload(Payload payload) {
    String sql = "insert into appcontext (launchId, launchUrl, patient, appContext, fhirContext, codeVerifier, codeChallenge) values (?, ?, ?, ?, ?, ?, ?)";
    jdbcTemplate.update(sql, payload.getLaunchId(), payload.getLaunchUrl(), payload.getPatient(), payload.getAppContext(), payload.getFhirContext(), payload.getCodeVerifier(), payload.getCodeChallenge());
    logger.info("Created payload: " + payload.toString());
  }
  public void createStandalonePayload(Payload payload) {
    String sql = "insert into appcontext (launchId, launchUrl, patient, appContext, fhirContext, codeVerifier, codeChallenge) values (?, ?, ?, ?, ?, ?, ?)";
    jdbcTemplate.update(sql, payload.getLaunchId(), "", "", "", "", payload.getCodeVerifier(), payload.getCodeChallenge());
    logger.info("Created payload: " + payload.toString());
  }
  @Override
  public Payload getPayload(String launchId) {
    String sql = "select * from appcontext where launchId = ?";
    List<Payload> payloads = jdbcTemplate.query(sql, new PayloadMapper(), launchId);
    if(payloads.size()>0) {
      return payloads.get(0);
    } else {
      throw new ResourceNotFoundException();
    }
  }

  @Override
  public void updateCode(String launchId, String launchCode) {
    String sql = "update appcontext set launchCode = ? where launchId = ?";
    jdbcTemplate.update(sql, launchCode, launchId);
    logger.info("Updated Record with ID = " + launchId);
  }
  @Override
  public Payload findContextByCode(String launchCode) {
    String sql = "select * from appcontext where launchCode = ?";
    List<Payload> payloads = jdbcTemplate.query(sql, new PayloadMapper(), launchCode);
    if(payloads.size()>0) {
      return payloads.get(0);
    } else {
      throw new ResourceNotFoundException();
    }

  }
  @Override
  public void updateRedirect(String launchId, String redirectUri) {
    String sql = "update appcontext set redirectUri = ? where launchId = ?";
    jdbcTemplate.update(sql, redirectUri, launchId);
    logger.info("Updated Record with ID = " + launchId);
  }

  @Override
  public void updatePKCE(String launchId, String codeVerifier, String codeChallenge) {
    String sql = "update appcontext set codeVerifier = ?, codeChallenge = ? where launchId = ?";
    jdbcTemplate.update(sql, codeVerifier, codeChallenge, launchId);
    logger.info("Updated PKCE parameters for launch ID = " + launchId);
  }
}


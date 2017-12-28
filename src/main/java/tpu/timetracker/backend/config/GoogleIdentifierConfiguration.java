package tpu.timetracker.backend.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tpu.timetracker.backend.auth.GoogleTokenVerifierTemplate;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class GoogleIdentifierConfiguration {

  @Value("${tputt.google.issuer.https}")
  private String issuerSecure;

  @Value("${tputt.google.issuer.http}")
  private String issuer;

  @Value("${tputt.google.clientId}")
  private String clientId;

  public GoogleIdentifierConfiguration() {
  }

  @Bean
  public GoogleIdTokenVerifier googleIdTokenVerifier(JacksonFactory jacksonFactory, HttpTransport httpTransport) {
    return new GoogleIdTokenVerifier.Builder(httpTransport, jacksonFactory)
        .setIssuers(Arrays.asList(issuer, issuerSecure))
        .setAudience(Collections.singletonList(clientId))
        .build();
  }

  @Bean
  public JacksonFactory jacksonFactory() {
    return new JacksonFactory();
  }

  @Bean
  public HttpTransport httpTransport() {
    return new NetHttpTransport();
  }

  @Bean
  public GoogleTokenVerifierTemplate googleTokenVerifierTemplate(GoogleIdTokenVerifier googleIdTokenVerifier) {
    return new GoogleTokenVerifierTemplate(googleIdTokenVerifier);
  }
}
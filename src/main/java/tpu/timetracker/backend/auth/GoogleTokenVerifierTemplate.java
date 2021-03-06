package tpu.timetracker.backend.auth;

import com.google.api.client.auth.openidconnect.IdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Component
public class GoogleTokenVerifierTemplate {

  private GoogleIdTokenVerifier googleIdTokenVerifier;

  public GoogleTokenVerifierTemplate(GoogleIdTokenVerifier googleIdTokenVerifier) {
    this.googleIdTokenVerifier = googleIdTokenVerifier;
  }

  public GoogleIdToken verify(String idToken) throws GeneralSecurityException, IOException {
    return googleIdTokenVerifier.verify(idToken);
  }

  public boolean verify(GoogleIdToken googleIdToken) throws GeneralSecurityException, IOException {
    return googleIdTokenVerifier.verify(googleIdToken);
  }

  public boolean verify(IdToken idToken) {
    return googleIdTokenVerifier.verify(idToken);
  }
}

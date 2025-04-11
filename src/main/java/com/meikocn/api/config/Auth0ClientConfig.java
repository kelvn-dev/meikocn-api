package com.meikocn.api.config;

import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import com.auth0.net.TokenRequest;
import com.meikocn.api.exception.VinposException;
import java.time.Instant;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class Auth0ClientConfig {

  private final Auth0Config auth0Config;
  private TokenHolder tokenHolder;
  private AuthAPI authAPI;
  private ManagementAPI managementAPI;

  public AuthAPI getAuthAPI() {
    if (Objects.isNull(authAPI)) {
      authAPI =
          AuthAPI.newBuilder(
                  auth0Config.getDomain(),
                  auth0Config.getUserManagement().getClientId(),
                  auth0Config.getUserManagement().getClientSecret())
              .build();
    }
    return authAPI;
  }

  public ManagementAPI getManagementAPI() {
    if (Objects.nonNull(this.tokenHolder)
        && this.tokenHolder.getExpiresAt().getTime() > Instant.now().toEpochMilli()) {
      log.info("Token has not expired, using existing token {}", tokenHolder.toString());
      log.info("Current token expires at {}", tokenHolder.getExpiresAt().getTime());
    } else {
      log.info("Existing access token has expired");
      renewTokenHolder();
    }
    managementAPI =
        ManagementAPI.newBuilder(auth0Config.getDomain(), tokenHolder.getAccessToken()).build();
    return managementAPI;
  }

  private void renewTokenHolder() {
    try {
      TokenRequest tokenRequest = getAuthAPI().requestToken(auth0Config.getManagementAPI());
      tokenHolder = tokenRequest.execute().getBody();
    } catch (Auth0Exception exception) {
      throw new VinposException(exception.getMessage());
    }
  }
}

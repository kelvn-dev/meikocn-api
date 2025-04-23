package com.meikocn.api.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final OAuth2Config oAuth2Config;

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  private final String[] bypassPaths = {
    "/swagger-resources/**",
    "/swagger-ui/**",
    "/v3/api-docs",
    "/v3/api-docs/**",
    "/error/**",
    "/v1/user-action/**",
    "/stomp/**" // To allow the initial HTTP call to stomp handshake endpoint
  };

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors(Customizer.withDefaults()).csrf(AbstractHttpConfigurer::disable);
    http.sessionManagement(
        manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    if (oAuth2Config.isBypassed()) {
      http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());
    } else {
      http.authorizeHttpRequests(
              authorize ->
                  authorize.requestMatchers(bypassPaths).permitAll().anyRequest().authenticated())
          .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));
    }
    return http.build();
  }

  /**
   * Build and configure the JwtDecoder that will be used when we receive a Jwt Token. Here we take
   * in a {@see RSAPublicKey} but you can also supply a JWK uri, or a {@see SecretKey}. By default,
   * the decoder will always verify the signature with the given key and validate the timestamp to
   * check if the JWT is still valid.
   *
   * <p>Our decoder can be customized with several options. We can for instance do custom validation
   * on claims, do rename, add and remove claims, and even change the datatype that the claim is
   * mapped too.
   *
   * <p>All we do below is to add some custom validation to the "issuer" claim and a custom
   * validator to validate the aud claim, remember we must add the default timestamp validation back
   * as we have overridden defaults. Per default a Public key will set the algorithm to RS256. If
   * you want something different you can set this explicitly.
   *
   * @return JwtDecoder
   */
  @Bean
  public JwtDecoder jwtDecoder() {
    final NimbusJwtDecoder decoder =
        NimbusJwtDecoder.withJwkSetUri(oAuth2Config.getJwkSetUri()).build();
    decoder.setJwtValidator(tokenValidator());
    return decoder;
  }

  // group validators together before going to place in JwtDecoder
  /**
   * We can write custom validators to validate different parts of the JWT. Per default, the
   * framework will always validate the timestamp, but we can add validators to enhance security.
   * For instance you should always validate the issuer to make sure that the JWT was issued from a
   * known source. Remember that if we customise the validation we need to re-add the timestamp
   * validator.
   *
   * <p>Here we crate a list of validators. The {@see JwtTimestampValidator} and the {@see
   * JwtIssuerValidator} are from the spring security framework, but we have also added a custom
   * one. Remember if you add a custom list, you must always remember to add timestamp validation or
   * else this will be removed.
   *
   * <p>We then place these in a {@see DelegatingOAuth2TokenValidator} that we can set to our {@see
   * JwtDecoder}.
   *
   * @return Oauth2TokenValidator<Jwt>
   */
  public OAuth2TokenValidator<Jwt> tokenValidator() {
    final List<OAuth2TokenValidator<Jwt>> validators =
        List.of(
            new JwtTimestampValidator(),
            new JwtIssuerValidator(oAuth2Config.getIssuerUri()),
            new JwtClaimValidator<List<String>>(
                OAuth2TokenIntrospectionClaimNames.AUD,
                aud -> aud.contains(oAuth2Config.getAudience())));
    return new DelegatingOAuth2TokenValidator<>(validators);
  }

  // JWT does not contain a scope claim but an authorities claim -> point out our
  // own custom claim
  // instead of the defaults
  /**
   * In the given JWT the "scope" claim is the default property that spring will use to extract and
   * build authorities from. But some issuers will have a different name, for instance
   * "authorities". So if we want to map from something else than the default, we can add our own
   * authentication converter.
   *
   * <p>Per default if the "scope" claim is used, it is called an "Authority". Which means it will
   * get prefixed by "SCOPE_" so a claim can look like ex: "SCOPE_read". But you can change this
   * prefix and add your own, for instance "ROLE_" so you get ROLE_USER".
   *
   * <p>ROLES are usually a group of SCOPES. so SCOPES could be read, write, update certain
   * resources. While a ROLE could be a group of these authorities, so as a USER you are allowed to
   * read and write for example your own information. And read and write your own messages etc.
   *
   * @return a JwtAuthenticationConverter
   */
  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    final JwtGrantedAuthoritiesConverter gac = new JwtGrantedAuthoritiesConverter();
    gac.setAuthoritiesClaimName("permissions");
    gac.setAuthorityPrefix("");

    final JwtAuthenticationConverter jac = new JwtAuthenticationConverter();
    jac.setJwtGrantedAuthoritiesConverter(gac);
    return jac;
  }

  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowCredentials(true);
    corsConfiguration.addAllowedOriginPattern("*");
    corsConfiguration.addAllowedHeader("*");
    corsConfiguration.addAllowedMethod("*");
    source.registerCorsConfiguration("/**", corsConfiguration);
    return new CorsFilter(source);
  }
}

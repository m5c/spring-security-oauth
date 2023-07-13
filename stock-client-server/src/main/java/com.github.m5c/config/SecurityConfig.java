package com.github.m5c.config;

/**
 * This is almost an identical copy of the client clonfiguration provided by the Baeldung example.
 * The only change made is the ID (stock) for the login page URL.
 */

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
public class SecurityConfig {

  /**
   * This filter chain intercepts any request to the client that does not yet contain a supplemental
   * OAuth2 token. If missing the sender is instead forwarded to the login page of the authorization
   * server.
   *
   * @param http
   * @return
   * @throws Exception
   */
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
        .oauth2Login(
            oauth2Login -> oauth2Login.loginPage("/oauth2/authorization/stock-client-oidc"))
        .oauth2Client(withDefaults());
    return http.build();
  }
}
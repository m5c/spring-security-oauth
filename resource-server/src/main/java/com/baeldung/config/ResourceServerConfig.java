package com.baeldung.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ResourceServerConfig {

  /**
   * This filterchain secures the BookStores API endpoints, so an OAuth token is required for
   * specific access. Additional security roles are added on a per-endpoint basis, when regex-url
   * matching is not suffucuent (this is e.g. the case when part of the URL must be matched against
   * the token resource owner).
   *
   * @param http configuration on which we can define pattern matcher and filter rules.
   * @return SecurityFilterChain to apply for all inbound requests to Resource server.
   * @throws Exception
   */
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    // See: https://stackoverflow.com/a/76638755/13805480
    // Changed policy for remaining "anyRequest" to permit all, as we want the remaining endpoints
    // to be unprotected / publicly accessible.

    http
        // First pattern matcher rule: require token for write operations on assortment
        .authorizeHttpRequests((authorize) -> authorize
            // The actual rules...
            .requestMatchers(HttpMethod.PUT, "/bookstore/isbns/{isbn}")
            .hasAuthority("SCOPE_assortment.write"))
        // ...
        // Second pattern matcher rule: require token for write operations on local store stock
        // This one is extended by user matching in the respective endpoint
        .authorizeHttpRequests((authorize) -> authorize
            // The actual rules...
            .requestMatchers(HttpMethod.POST, "/bookstore/stocklocations/{stocklocation}/{isbn}")
            .hasAuthority("SCOPE_stock.write")) // <- the permit all must be in the last rule (earlier definitions always win, so the default action must be the last one to mention.)s
        // ...
        // Also a pattern matcher rule to apply for anything else that has not yet been configured
        .authorizeHttpRequests((authorize) -> authorize
          .anyRequest().permitAll()
        )
        // Finally: grand public access to all remaining endpoints
        .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));

    return http.build();
  }
}
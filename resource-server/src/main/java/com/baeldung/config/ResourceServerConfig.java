package com.baeldung.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
   * matching is not sufficuent (this is e.g. the case when part of the URL must be matched against
   * the token resource owner).
   *
   * @param http configuration on which we can define pattern matcher and filter rules.
   * @return SecurityFilterChain to apply for all inbound requests to Resource server.
   * @throws Exception
   */
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http
        // First pattern matcher rule: require token for write operations on assortment
//        .authorizeHttpRequests((authorize) -> authorize
//            // The actual rules...
//            .requestMatchers(HttpMethod.PUT, "/bookstore/isbns/{isbn}").hasAuthority(
//                "SCOPE_assortment.write")) // technically we have to check user role here, too - so that only admins can enable this delegation, not standard users.
//        // ...
        // Second pattern matcher rule: require token for write operations on local store stock
        // This one is extended by user matching in the respective endpoint
        .authorizeHttpRequests((authorize) -> authorize
            // The actual rules...
            .requestMatchers(HttpMethod.POST, "/bookstore/stocklocations/{stocklocation}/{isbn}")
            .hasAuthority(
                "SCOPE_stock.write"))  // technically we have to check user role here, too - so that only standard users can enable this delegation, not admins.
        // ...
        .authorizeHttpRequests((authorize) -> authorize
            // The actual rules...
            .requestMatchers(HttpMethod.GET, "/bookstore/stocklocations/{stocklocation}/{isbn}")
            .hasAuthority(
                "SCOPE_stock.read"))  // technically we have to check user role here, too - so that only standard users can enable this delegation, not admins.

        // Finally: grant public access to all remaining endpoints
        .authorizeHttpRequests((authorize) -> authorize.anyRequest().permitAll())
        .oauth2ResourceServer(oauth2 -> {
          oauth2.jwt(jwt -> {
            jwt.jwtAuthenticationConverter(new FusedClaimConverter());
          });
        });

    return http.build();
  }
}
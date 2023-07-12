package com.baeldung.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class ResourceServerConfig {

  /**
   * This filterchain secures the BookStores API endpoints, so an OAuth token is required for
   * specific access. Additional security roles are added on a per-endpoint basis, when regex-url
   * matching is not suffucuent (this is e.g. the case when part of the URL must be matched against
   * the token resource owner).
   *
   * @param http
   * @return
   * @throws Exception
   */
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // Note: mvcMatcher is the more extensive version of an antmatcher. The difference is that an mvcMatcher also triggers the sercurity chain rule all resource file extensions.
    // First mandate that the inbound request carries a token (without yet specifying details)

    // TODO: Refine filter chain as described on SO:
    // https://stackoverflow.com/a/76638755/13805480

    // TODO: figure out why this security rule does not check the SCOPE.
    // TODO: I can access this with a different scope and it not clear why...
    // This first httpSecurity configuration mandates an assortment.write scoped token is provided for adding new books to the assortment.
    http.mvcMatcher("/bookstore/isbns/{isbn}").authorizeRequests()
        // ...then refine the previous mvcMatcher...
        .mvcMatchers(HttpMethod.PUT, "/bookstore/isbns/{isbn}")
        // ...and only allow accesss if the token is associated to the assortment.read scope...
        .access("hasAuthority('SCOPE_assortment.write')")
        // finally configure to obtain the scope information used abote to be extracted from the jwt issued but the OAuth2 authorization server.
        .and().oauth2ResourceServer().jwt();
    return http.build();
  }
}
package com.baeldung.config;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenCustomizer;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
public class DefaultSecurityConfig {

  @Bean
  SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
        .formLogin(withDefaults());
    return http.build();
  }

  /**
   * This bean extends the default content of JWTs by an additional ROLE field, which is needed to
   * restrict access on ADMIN endpoints by proxy services invoked by ordinary users.
   * See: https://stackoverflow.com/a/68723813/13805480
   * See: https://www.appsdeveloperblog.com/add-roles-to-jwt-issued-by-new-spring-authorization-server/
   *
   * @return
   */

  // TODO: Add counterpart for this JWT extender to the Resource Server, so it has a notion of roles
  @Bean
  OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
    return context -> {
      if (context.getTokenType() == OAuth2TokenType.ACCESS_TOKEN) {
        Authentication principal = context.getPrincipal();
        Set<String> authorities = principal.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        context.getClaims().claim("roles", authorities);
      }
    };
  }

  @Bean
  UserDetailsService users() {

    // The database knows several users: 2 generic users for comments and catalogue,
    // several for the individual stock locactions
    UserDetails assortmentExtender =
        User.withDefaultPasswordEncoder().username("AssortmentExtender").password("password")
            .roles("ADMIN").build();

    UserDetails montrealStoreManager =
        User.withDefaultPasswordEncoder().username("Montréal").password("password").roles("USER")
            .build();

    UserDetails munchenStoreManager =
        User.withDefaultPasswordEncoder().username("München").password("password").roles("USER")
            .build();

    UserDetails osterhofenStoreManager =
        User.withDefaultPasswordEncoder().username("Osterhofen").password("password").roles("USER")
            .build();

    UserDetails lyonStoreManager =
        User.withDefaultPasswordEncoder().username("Lyon").password("password").roles("USER")
            .build();
    return new InMemoryUserDetailsManager(assortmentExtender, montrealStoreManager,
        munchenStoreManager, osterhofenStoreManager, lyonStoreManager);
  }

}

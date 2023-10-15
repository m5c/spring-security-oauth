package com.baeldung.config;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
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
   * This bean extends the default content of JWTs by an additional "role" claim, listing the
   * resource owners roles (without the ROLE_ prefix) https://stackoverflow.com/a/68723813/13805480
   * See:
   * https://www.appsdeveloperblog.com/add-roles-to-jwt-issued-by-new-spring-authorization-server/
   * The ResourceServer showcases a counterpart to handle that information. See
   * RS.ResourceServerConfig
   *
   * @return OAuth2TokenCustomizer overloaded configuration.
   */
  @Bean
  OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
    return context -> {
      if (context.getTokenType() == OAuth2TokenType.ACCESS_TOKEN) {
        Authentication principal = context.getPrincipal();

        // Here we chop off the prefix, to respect JWT conventions. The ROLE prefix is only relevant
        // once the information is extracted back to the principal authority list.
        Set<String> enumeratedRoles =
            principal.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .map(roleString -> roleString.split("_")[1]).collect(Collectors.toSet());

        context.getClaims().claim("role", enumeratedRoles);
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

    UserDetails marieCurie =
        User.withDefaultPasswordEncoder().username("Curie").password("password").roles("USER")
            .build();

    UserDetails leonardCohen =
        User.withDefaultPasswordEncoder().username("Cohen").password("password").roles("USER")
            .build();

    UserDetails angelaMerkel =
        User.withDefaultPasswordEncoder().username("Merkel").password("password").roles("USER")
            .build();

    UserDetails maxPlank =
        User.withDefaultPasswordEncoder().username("Plank").password("password").roles("USER")
            .build();

    return new InMemoryUserDetailsManager(assortmentExtender, montrealStoreManager,
        munchenStoreManager, osterhofenStoreManager, lyonStoreManager, marieCurie, leonardCohen,
        angelaMerkel, maxPlank);
  }

}

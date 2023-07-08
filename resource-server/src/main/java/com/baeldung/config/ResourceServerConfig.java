package com.baeldung.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class ResourceServerConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Note: mvcMatcher is like an antmatcher, but also triggers for any file extensions to the resource.
        http.mvcMatcher("/bookstore/**")
          .authorizeRequests()
          .mvcMatchers("/bookstore/**")
          .access("hasAuthority('SCOPE_assortment.read')")
          .and()
          .oauth2ResourceServer()
          .jwt();
        return http.build();
    }
}
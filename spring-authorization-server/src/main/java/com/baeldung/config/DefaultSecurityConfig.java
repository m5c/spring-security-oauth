package com.baeldung.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
public class DefaultSecurityConfig {

  @Bean
  SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeRequests(authorizeRequests ->
            authorizeRequests.anyRequest().authenticated()
        )
        .formLogin(withDefaults());
    return http.build();
  }

  @Bean
  UserDetailsService users() {

    // TODO: add user for assortment (write) access.
    // The database knows several users: 2 generic users for comments and catalogue,
    // several for the individual stock locactions
    UserDetails assortmentExtender = User.withDefaultPasswordEncoder()
        .username("AssortmentExtender")
        .password("password")
        .roles("USER") // TODO: figure out what role is appropriate
        .build();

    UserDetails montrealStoreManager = User.withDefaultPasswordEncoder()
        .username("Montréal")
        .password("password")
        .roles("USER")
        .build();

    UserDetails munchenStoreManager = User.withDefaultPasswordEncoder()
        .username("München")
        .password("password")
        .roles("USER")
        .build();

    UserDetails osterhofenStoreManager = User.withDefaultPasswordEncoder()
        .username("Osterhofen")
        .password("password")
        .roles("USER")
        .build();

    UserDetails lyonStoreManager = User.withDefaultPasswordEncoder()
        .username("Lyon")
        .password("password")
        .roles("USER")
        .build();
    return new InMemoryUserDetailsManager(assortmentExtender, montrealStoreManager, munchenStoreManager, osterhofenStoreManager, lyonStoreManager);
  }

}

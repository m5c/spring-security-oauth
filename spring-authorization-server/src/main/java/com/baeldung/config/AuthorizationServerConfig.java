package com.baeldung.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception {
    OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
    return http.formLogin(Customizer.withDefaults()).build();
  }

  /**
   * This configuration bean associates the (proxy) clients that access resources on behalf of
   * resource owners. Technically the list should be extensible at runtime, as the interest of
   * OAuth2 is to add new clients to a microserve, which are not known at deployment time. However,
   * since the interest of this sample project is secruing the resource server API, hard coded
   * registration of proxy clients is acceptable.
   *
   * @return repository configuration listing the associated (proxy) client services.
   */
  @Bean
  public RegisteredClientRepository registeredClientRepository() {
    RegisteredClient assortmentClient =
        RegisteredClient.withId(UUID.randomUUID().toString()).clientId("assortment-client")
            .clientSecret("{noop}secret")
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .redirectUri("http://127.0.0.1:8080/login/oauth2/code/assortment-client-oidc")
            .redirectUri("http://127.0.0.1:8080/authorized").scope(OidcScopes.OPENID)
            .scope("assortment.write").build();

    // TODO figure out why client ID is not unique...
    // Here we added a second (proxy Oauth2) client, that has the priviledge to change
    // local store stocks
    RegisteredClient stockClient =
        RegisteredClient.withId(UUID.randomUUID().toString()).clientId("stock-client")
            .clientSecret("{nooop}secret") // Unsure what this is, but it has to be unique...
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .redirectUri("http://127.0.0.1:8080/login/oauth2/code/stock-client-oidc")
            .redirectUri("http://127.0.0.1:8080/authorized").scope(OidcScopes.OPENID)
            .scope("stock.write").build();

    return new InMemoryRegisteredClientRepository(assortmentClient, stockClient);
//    return new InMemoryRegisteredClientRepository(assortmentClient);
  }

  @Bean
  public JWKSource<SecurityContext> jwkSource() {
    RSAKey rsaKey = generateRsa();
    JWKSet jwkSet = new JWKSet(rsaKey);
    return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
  }

  private static RSAKey generateRsa() {
    KeyPair keyPair = generateRsaKey();
    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
    RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
    return new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString())
        .build();
  }

  private static KeyPair generateRsaKey() {
    KeyPair keyPair;
    try {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
      keyPairGenerator.initialize(2048);
      keyPair = keyPairGenerator.generateKeyPair();
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
    return keyPair;
  }

  @Bean
  public ProviderSettings providerSettings() {
    return ProviderSettings.builder().issuer("http://auth-server:9000").build();
  }
}
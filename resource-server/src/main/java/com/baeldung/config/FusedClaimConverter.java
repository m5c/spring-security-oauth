package com.baeldung.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.stream.Stream;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

/**
 * This is a custom converter to extract all entries of two Jwt claims "scope" and "role". All
 * findings are prefixed with the respective "SCOPE_" and "ROLE_" prefixes, and then wrapped up as a
 * list of authorities, which can be processed by Springs SPeL in @PreAuthorize annotations. This
 * implementation is based on: https://stackoverflow.com/a/58234971/13805480
 */
public class FusedClaimConverter implements Converter<Jwt, AbstractAuthenticationToken> {

  // The fused converter internally fuses the outputs of two converters.
  // One component is the default converter (extracting scp/scope claim information).
  // So we create one instance of this off-the-shelf convert for later use.
  private final JwtGrantedAuthoritiesConverter defaultGrantedAuthoritiesConverter =
      new JwtGrantedAuthoritiesConverter();

  /**
   * This method provides the second component of our custom converter. It is a manual
   * implementation that searches the jwt for a custom "role" claim. If found, all entries are
   * prefixed with "ROLE_" and returned as list of authorities.
   *
   * @param jwt as the json web token to analyze for "role" claim entries.
   * @return collection of granted authorities extracted from the jwt.
   */
  private static Collection<? extends GrantedAuthority> extractResourceRoles(final Jwt jwt) {
    ArrayList<String> resourceAccess = jwt.getClaim(
        "role"); // <- specify here whatever additional jwt claim you wish to convert to authority
    if (resourceAccess != null) {
      // Convert every entry in value list of "role" claim to an Authority
      return resourceAccess.stream().map(x -> new SimpleGrantedAuthority("ROLE_" + x))
          .collect(Collectors.toSet());
    }
    // Fallback: return empty list in case the jwt has no "role" claim.
    return Collections.emptySet();
  }

  /**
   * This is the main converter method to override. In essence here we provide a custom
   * implementation that concatenates the authority lists generated from two respective conterters.
   * One is the off-the-shelf default converter that operates on the "scp"/"scope" claim. The other
   * is the converter for our custom claim.
   *
   * @param source as the json web token to inspect for claims
   * @return list of authorities extracted from token, wrapped up in AbstractAuthenticationToken
   * object.
   */
  @Override
  public AbstractAuthenticationToken convert(final Jwt source) {
    Collection<GrantedAuthority> authorities =
        Stream.concat(defaultGrantedAuthoritiesConverter.convert(source).stream(),
            extractResourceRoles(source).stream()).collect(Collectors.toSet());
    return new JwtAuthenticationToken(source, authorities);
  }
}
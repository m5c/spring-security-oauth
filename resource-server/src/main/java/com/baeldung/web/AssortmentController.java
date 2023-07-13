package com.baeldung.web;

import eu.kartoffelquadrat.bookstoreinternals.AssortmentImpl;
import eu.kartoffelquadrat.bookstoreinternals.BookDetailsImpl;
import java.security.Principal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;
import java.util.Base64;

/**
 * @author Maximilian Schiedermeier
 */
@RestController
@CrossOrigin
public class AssortmentController {

  @GetMapping("/bookstore/isbns")
  public Collection<Long> getEntireAssortment() {
    return AssortmentImpl.getInstance().getEntireAssortment();
  }

  @GetMapping("/bookstore/isbns/{isbn}")
  public BookDetailsImpl getBookDetails(@PathVariable("isbn") Long isbn) {
    return AssortmentImpl.getInstance().getBookDetails(isbn);
  }

  // TODO: Figure out if there is a way to check the ROLE, not the NAME.
//  @PreAuthorize("authentication.name == 'AssortmentExtender'") // <-- works but is a hack.
//  @PreAuthorize("authentication.authorities.contains('ROLE_ADMIN')")
//  @PreAuthorize("hasAnyAuthority('ADMIN')") <= Unclear why this is not extracted from the token...
//  @PreAuthorize("hasRole('ADMIN')") <==== WHY DOES THIS FAIL, the ROLE is in the JWT!!!
//  @PreAuthorize("hasAuthority('ADMIN')")
  // I think the preauthorize does not work, because it uses the prinicpal information, not the JWT information.
  @PutMapping("/bookstore/isbns/{isbn}")
  public void addBookToAssortment(@RequestBody BookDetailsImpl bookDetails, final @AuthenticationPrincipal
  Jwt jwt, Authentication authentication) {

    // TODO: Either fix springs notion of the roles (extracted from jwt), or add manual check for admin role here
//    String encryptedToken = jwt.getTokenValue();
//    String[] tokenChunks = encryptedToken.split("\\.");
//    System.out.println("\nDEBUG TOKEN INFO:\n"+encryptedToken);
//    Base64.Decoder decoder = Base64.getUrlDecoder();
//    String header = new String(decoder.decode(tokenChunks[0]));
//    String payload = new String(decoder.decode(tokenChunks[1]));
//    System.out.println("HEADER: "+header);
//    System.out.println("PAYLOAD: "+payload);
//
//    // Also echk what information has been extracted by spring:
//    Object principal = authentication.getPrincipal();
////    System.out.println("User has authorities: " + userDetails.getAuthorities());

    AssortmentImpl.getInstance().addBookToAssortment(bookDetails);
//    LinkedHashMap userVariables = new JwtUtil().extractAllClaims(jwt)

  }
}

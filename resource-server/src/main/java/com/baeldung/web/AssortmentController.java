package com.baeldung.web;

import eu.kartoffelquadrat.bookstoreinternals.AssortmentImpl;
import eu.kartoffelquadrat.bookstoreinternals.BookDetailsImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;

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
  //    @PreAuthorize("hasRole('ROLE_ADMIN')")
//  @PreAuthorize("authentication.name == 'AssortmentExtender'") // <-- works but is a hack.
//  @PreAuthorize("authentication.authorities.contains('ROLE_ADMIN')")
  @PutMapping("/bookstore/isbns/{isbn}")
  public void addBookToAssortment(@RequestBody BookDetailsImpl bookDetails, final @AuthenticationPrincipal
  Jwt jwt) {
    System.out.println("\nDEBUG TOKEN INFO:\n"+jwt.getTokenValue());
    AssortmentImpl.getInstance().addBookToAssortment(bookDetails);
  }
}

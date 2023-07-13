package com.baeldung.web;

import eu.kartoffelquadrat.bookstoreinternals.BookDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@RestController
public class AssortmentProxyController {


  @Autowired
  private WebClient webClient;


  /**
   * Invokation of this endpoint triggers access request on the Resource Server's (the BookStore's)
   * API to add new books to the assortment. Triggering this with a GET is a REST antipattern, but
   * we keep it for simplicity, so it can be easily triggered by a browser (which simplifies the
   * authorization process by user-agent redirection)
   *
   * @param authorizedClient
   * @return
   */
  @GetMapping(value = "/assortmentextension")
  public String[] getAssortmentExtension(
      @RegisteredOAuth2AuthorizedClient("assortment-client-authorization-code")
      OAuth2AuthorizedClient authorizedClient) {

    // Here we create a new book object to add to the collection.
    BookDetailsImpl book = new BookDetailsImpl(3518368540l, "Homo Faber", "Max Frisch", 1610,
        "Max Frischs 1957 novel \"Homo faber\" describes people in the technological age who believe they can organize life according to the laws of logic and science. According to Frisch, modern man lives past himself and surrenders to the feasibility euphoria of technology.");

    // What before was only one call (get all assortment) are now two:
    // 1) A put to add the new book(s) (secured)
    this.webClient.put().uri("http://127.0.0.1:8090/bookstore/isbns/" + book.getIsbn())
        .contentType(MediaType.APPLICATION_JSON).bodyValue(book)
        .attributes(oauth2AuthorizedClient(authorizedClient)).retrieve().bodyToMono(
            Void.class) // Note: there seems to be no way to stripe the bodyToMono statement, even if there is no return body for this "put" endpoint.
        .block();

    // 2) A get (same as before), to get new list of books (unsecured)
    return this.webClient.get().uri("http://127.0.0.1:8090/bookstore/isbns").retrieve()
        .bodyToMono(String[].class).block();
  }
}
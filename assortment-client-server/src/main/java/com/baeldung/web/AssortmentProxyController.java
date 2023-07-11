package com.baeldung.web;

import eu.kartoffelquadrat.bookstoreinternals.BookDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
   * authorization process by user-agend redirection)
   *
   * @param authorizedClient
   * @return
   */
  @GetMapping(value = "/assortmentextension")
  public String[] getArticles(
      @RegisteredOAuth2AuthorizedClient("articles-client-authorization-code")
      OAuth2AuthorizedClient authorizedClient) {

    // TODO: Create list of sample books to add to assortment
    long isbn1 = 3518368540l;
    BookDetailsImpl book = new BookDetailsImpl(3518368540l, "Homo Faber", "Max Frisch", 1610,
        "Max Frisch's 1957 novel \"Homo faber\" describes people in the technological age who believe they can organize life according to the laws of logic and science. According to Frisch, modern man lives past himself and surrenders to the feasibility euphoria of technology.");

    // What before was only one call (get all assortment) are now two:
    // 1) A put to add the new book(s) (secured)
    this.webClient.put().uri("http://127.0.0.1:8090/bookstore/isbns/" + book.getIsbn())
        .contentType(MediaType.APPLICATION_JSON).bodyValue(book)
        .attributes(oauth2AuthorizedClient(authorizedClient)).retrieve().bodyToMono(String[].class)
        .block();

    // 2) A get (same as before), to get new list of books (unsecured)
    return this.webClient.get().uri("http://127.0.0.1:8090/bookstore/isbns/")
//        .attributes(oauth2AuthorizedClient(authorizedClient)) // TODO: remove security mandate and defuse endpoint!
        .retrieve()
        .bodyToMono(String[].class).block();
  }
}
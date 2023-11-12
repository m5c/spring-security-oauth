package com.github.m5c.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import static org.springframework.util.StringUtils.capitalize;


import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@RestController
public class StockProxyController {

  @Autowired
  private WebClient webClient;


  /**
   * Invokation of this endpoint triggers access request on the Resource Server's (the BookStore's)
   * API to change the amount of harry potter books in store for the location correpsonding the
   * resource owner associated to the token. Triggering this with a GET is a REST antipattern, but
   * we keep it for simplicity, so it can be easily triggered by a browser (which simplifies the
   * authorization process by user-agent redirection)
   *
   * @param authorizedClient
   * @return
   */
  @GetMapping(value = "/stockextension/{storelocation}")
  public int getBumpedUpStock(@RegisteredOAuth2AuthorizedClient("stock-client-authorization-code")
                              OAuth2AuthorizedClient authorizedClient,
                              @PathVariable("storelocation") String storeLocation) {

    // Internally cities (for URLs) follow upper case syntax. Capitalizing to avoid that issue.
    storeLocation = capitalize(storeLocation);

    // Hard coded details of harry potter book:
    long harryPotterIsbn = 9780739360385l;
    String localStockResource =
        "http://127.0.0.1:8090/bookstore/stocklocations/" + storeLocation + "/" + harryPotterIsbn;

    // just for fun, try to access something that is blocked for OAuth2 clients
    String blockedResource =
        "http://127.0.0.1:8090/bookstore/stocklocations";
    String locations = this.webClient.get().uri(blockedResource).retrieve().bodyToMono(String.class).block();
    System.out.println(locations);

    // Similar to the assortment client, we run two requests to the resource server here:
    // 1) Look up amount of harry potter books in the provided location (unsecured)
    int copiesInStock =
        this.webClient.get().uri(localStockResource).retrieve().bodyToMono(Integer.class).block();

    // 2) Add another 100 copies to that location, and update remote resource
    this.webClient.post().uri(localStockResource)
        // TODO, ensure correct body encoding type is used here.
        .contentType(MediaType.APPLICATION_JSON).bodyValue(copiesInStock + 100)
        // Note: The "bodyToMono" seems inevitable, even if there is no return payload.
        .attributes(oauth2AuthorizedClient(authorizedClient)).retrieve().bodyToMono(Void.class)
        .block();

    // Technically we could just return the computed sum, but for transparency we issue a third
    // call to the RS (BookStore), to ensure the number this method returns effectively represents
    // the server side state. Therefore...
    // 3) Return resulting new stock
    return this.webClient.get().uri(localStockResource).retrieve().bodyToMono(Integer.class)
        .block();


  }
}
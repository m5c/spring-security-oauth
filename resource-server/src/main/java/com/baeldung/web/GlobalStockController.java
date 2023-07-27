package com.baeldung.web;

import eu.kartoffelquadrat.bookstoreinternals.GlobalStockImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

/**
 * @author Maximilian Schiedermeier
 */
@RestController
@CrossOrigin
public class GlobalStockController {


  @GetMapping("/bookstore/stocklocations/{stocklocation}/{isbn}")
  public int getStock(@PathVariable("stocklocation") String city, @PathVariable("isbn") Long isbn) {

    return GlobalStockImpl.getInstance().getStock(city, isbn);
  }

  // Old PreAuthorize, that compares account name to city, string match.
  //    @PreAuthorize("authentication.name == #city")
  // Now we're interested in allowing accounts that are associated to city by domain model.
  // See PreAuthorize syntax documentation: https://docs.spring.io/spring-security/site/docs/3.0.x/reference/el-access.html
  // TODO: Write SpEL that resolves city localstock object and traverses to employee list.
  // Possibly I can get the singleton like so: https://stackoverflow.com/q/7585627/13805480
  // @PreAuthorize("T(eu.kartoffelquadrat.bookstoreinternals.GlobalStockImpl).getInstance().isEmployed(#city, authentication.name)")

  // Alternative access method using manual inspection of employee list.
  @PreAuthorize("T(eu.kartoffelquadrat.bookstoreinternals.GlobalStockImpl).getInstance().getAllEmployeesForStore(#city).contains(authentication.name)")
  @PostMapping("/bookstore/stocklocations/{stocklocation}/{isbn}")
  public void setStock(@PathVariable("stocklocation") String city, @PathVariable("isbn") Long isbn,
                       @RequestBody Integer amount) {

    GlobalStockImpl.getInstance().setStock(city, isbn, amount);
  }

  @GetMapping("/bookstore/stocklocations")
  public Collection<String> getStoreLocations() {
    return GlobalStockImpl.getInstance().getStoreLocations();
  }

  @GetMapping("/bookstore/stocklocations/{stocklocation}")
  public Map<Long, Integer> getEntireStoreStock(@PathVariable("stocklocation") String city) {
    return GlobalStockImpl.getInstance().getEntireStoreStock(city);
  }
}

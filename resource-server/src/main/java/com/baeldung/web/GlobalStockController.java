package com.baeldung.web;

import eu.kartoffelquadrat.bookstoreinternals.GlobalStockImpl;
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

    @PostMapping("/bookstore/stocklocations/{stocklocation}/{isbn}")
    public void setStock(@PathVariable("stocklocation") String city, @PathVariable("isbn") Long isbn, @RequestBody Integer amount) {

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

package com.baeldung.web;

import eu.kartoffelquadrat.bookstoreinternals.AssortmentImpl;
import eu.kartoffelquadrat.bookstoreinternals.BookDetailsImpl;
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

    @PutMapping("/bookstore/isbns/{isbn}")
    public void addBookToAssortment(@RequestBody BookDetailsImpl bookDetails) {
        AssortmentImpl.getInstance().addBookToAssortment(bookDetails);
    }
}

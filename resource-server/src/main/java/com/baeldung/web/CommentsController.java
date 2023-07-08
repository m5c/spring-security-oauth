package com.baeldung.web;

import eu.kartoffelquadrat.bookstoreinternals.CommentsImpl;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DAO for comments on books. Singleton.
 *
 * @author Maximilian Schiedermeier
 */
@RestController
@CrossOrigin
public class CommentsController {


    @GetMapping("/bookstore/isbns/{isbn}/comments")
    public Map<Long, String> getAllCommentsForBook(@PathVariable("isbn") long isbn) {
        return CommentsImpl.getInstance().getAllCommentsForBook(isbn);
    }

    @PostMapping("/bookstore/isbns/{isbn}/comments")
    public void addComment(@PathVariable("isbn") long isbn, @RequestBody String comment) {
        CommentsImpl.getInstance().addComment(isbn, comment);
    }

    @DeleteMapping("/bookstore/isbns/{isbn}/comments/{commentid}")
    public void deleteComment(@PathVariable("isbn") long isbn, @PathVariable("commentid") long commentId) {
        CommentsImpl.getInstance().deleteComment(isbn, commentId);
    }

    @DeleteMapping("/bookstore/isbns/{isbn}/comments")
    public void removeAllCommentsForBook(@PathVariable("isbn") long isbn) {
        CommentsImpl.getInstance().removeAllCommentsForBook(isbn);
    }

    @PostMapping("/bookstore/isbns/{isbn}/comments/{commentid}")
    public void editComment(@PathVariable("isbn") long isbn, @PathVariable("commentid") long commentId, @RequestBody String updatedComment) {
        CommentsImpl.getInstance().editComment(isbn, commentId, updatedComment);
    }
}

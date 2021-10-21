package com.example.BookTDD1021;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping({"/books"})
public class BookController {
    @Autowired
    private BookRepository bookRepository;
//    @Autowired
//    private UserRepository userRepository;

    public BookController() {
    }

    @GetMapping
    Iterable<Book> findAll() {
        return this.bookRepository.findAll();
    }

    @GetMapping("/title")
    public BookDetailResponse getMovieDetailByTitle(@RequestParam String title) {
        Optional<Book> book = this.bookRepository.findByTitle(title);
        if (book.isPresent())
            return new BookDetailResponse(HttpStatus.FOUND.value(), "Success", book.get());

        return new BookDetailResponse(HttpStatus.NOT_FOUND.value(), "Book not found", null);
    }

    @PostMapping
    public Book addBook(@RequestBody Book book) {
        return (this.bookRepository.save(book));
    }

    public Book updateName(int id, String title) {
        Book temp = (Book)this.bookRepository.findById(id).get();
        temp.setTitle(title);
        Book updated = (Book)this.bookRepository.save(temp);
        return updated;
    }

    @PutMapping({"/update/{id}/name/{name}"})
    public Book updateTitle(@PathVariable("id") int id, @PathVariable String title) {
        return this.updateName(id, title);
    }

    @DeleteMapping({""})
    public void deleteBook(@PathVariable int id) {
        this.bookRepository.deleteById(id);
    }
}

package com.example.BookTDD1021;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    //get mapping to find all books
    @GetMapping
    Iterable<Book> findAll() {
        return this.bookRepository.findAll();
    }

    //get mapping to find a book by title
    @GetMapping("/title")
    public BookDetailResponse getMovieDetailByTitle(@RequestParam String title) {
        Optional<Book> book = this.bookRepository.findByTitle(title);
        if (book.isPresent())
            return new BookDetailResponse(HttpStatus.FOUND.value(), "Success", book.get());

        return new BookDetailResponse(HttpStatus.NOT_FOUND.value(), "Book not found", null);
    }

    //post mapping to add a book
    @PostMapping
    public Book addBook(@RequestBody Book book) {
        return (this.bookRepository.save(book));
    }


//    @PutMapping({"/update/{id}/name/{name}"})
//    public Book updateTitle(@PathVariable("id") int id, @PathVariable String title) {
//        return this.updateName(id, title);
//    }

//    @DeleteMapping("/id")
//    public BookDetailResponse deleteBook(@PathVariable int id) {
//        this.bookRepository.deleteById(id);
//        return new BookDetailResponse(HttpStatus.FOUND.value(), "Book deleted", null);
//    }

//    @DeleteMapping(value = "/{id}")
//    public ResponseEntity<HttpStatus> removeBook (@PathVariable("id") int id)
//    {
//        //code
//        this.bookRepository.deleteById(id);
//        return new ResponseEntity<HttpStatus>(HttpStatus.ACCEPTED);
//    }

    //delete mapping to delete a book by given id
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable int id) {
        this.bookRepository.deleteById(id);
    }

    //get mapping to find a book by the author name
    @GetMapping("/author")
    public BookDetailResponse getBookDetailByAuthor(@RequestParam String author) {
        Optional<Book> book = this.bookRepository.findByAuthor(author);
        if (book.isPresent())
            return new BookDetailResponse(HttpStatus.FOUND.value(), "Success", book.get());
        return new BookDetailResponse(HttpStatus.NOT_FOUND.value(), "Author not found", null);
    }

    //put mapping to update book title by given id
    @PutMapping("/{id}/{title}")
    @ResponseBody
    public BookDetailResponse updateTitle(@PathVariable("id") int id, @PathVariable String title) {
        //System.out.println("Updating book " + book);
        this.updateName(id, title);
        return new BookDetailResponse(HttpStatus.FOUND.value(), "Book updated", null);
    }

    //method used in put method to change previous title to new title
    public Book updateName(int id, String title) {
        Book temp = (Book)this.bookRepository.findById(id).get();
        temp.setTitle(title);
        Book updated = (Book)this.bookRepository.save(temp);
        return updated;
    }


}

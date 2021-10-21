package com.example.BookTDD1021;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTest {
    @Autowired
    MockMvc mvc;
    ObjectMapper mapper = new ObjectMapper();
    @Autowired
    BookRepository bookRepository;
//    @Autowired
//    UserRepository userRepository;

    public BookControllerTest() {
    }

    public String getJSON(String path) throws Exception {
        Path paths = Paths.get(path);
        return new String(Files.readAllBytes(paths));
    }

    private void createMockData() throws Exception {
        String movieStr = this.getJSON("src/test/resources/onebook.json");
        Book book = (Book)this.mapper.readValue(movieStr, Book.class);
        this.bookRepository.save(book);
    }

    private void createMockManyBooks() throws Exception {
        String bookStr = this.getJSON("src/test/resources/manybooks.json");
        TypeReference<List<Book>> books = new TypeReference<List<Book>>() {
        };
        List<Book> jsonToBookList = (List)this.mapper.readValue(bookStr, books);
        this.bookRepository.saveAll(jsonToBookList);
    }

    @Test
    public void testGetEmptyMovies() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.get("/books", new Object[0])).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().json("[]"));
    }

    @Test
    @Transactional
    @Rollback
    public void testCreateBook() throws Exception {
        String bookStr = this.getJSON("src/test/resources/onebook.json");
        Book book = (Book)this.mapper.readValue(bookStr, Book.class);
        RequestBuilder request = MockMvcRequestBuilders.post("/books", new Object[0]).contentType(MediaType.APPLICATION_JSON).content(bookStr);
        this.mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(book.getId()))).andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is(book.getTitle()))).andExpect(MockMvcResultMatchers.jsonPath("$.author", Matchers.is(book.getAuthor())));
    }

    @Test
    @Transactional
    @Rollback
    public void testGetBooks() throws Exception {
        this.createMockData();
        Book book = (Book)this.bookRepository.findAll().iterator().next();
        this.mvc.perform(MockMvcRequestBuilders.get("/books", new Object[0])).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(book.getId()))).andExpect(MockMvcResultMatchers.jsonPath("$[0].title", Matchers.is(book.getTitle()))).andExpect(MockMvcResultMatchers.jsonPath("$[0].author", Matchers.is(book.getAuthor())));
    }

    @Test
    @Transactional
    @Rollback
    public void testGetManyBooks() throws Exception {
        this.createMockManyBooks();
        List<Book> booksList = (List)this.bookRepository.findAll();
        Book book = (Book)booksList.get(0);
        Book book2 = (Book)booksList.get(1);
        this.mvc.perform(MockMvcRequestBuilders.get("/books", new Object[0])).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(book.getId()))).andExpect(MockMvcResultMatchers.jsonPath("$[0].title", Matchers.is(book.getTitle()))).andExpect(MockMvcResultMatchers.jsonPath("$[0].author", Matchers.is(book.getAuthor()))).andExpect(MockMvcResultMatchers.jsonPath("$[1].id", Matchers.is(book2.getId()))).andExpect(MockMvcResultMatchers.jsonPath("$[1].title", Matchers.is(book2.getTitle()))).andExpect(MockMvcResultMatchers.jsonPath("$[1].author", Matchers.is(book2.getAuthor())));
    }

    @Test
    @Transactional
    @Rollback
    public void testGetExistingBookDetails() throws Exception {
        this.createMockManyBooks();
        List<Book> booksList = (List)this.bookRepository.findAll();
        Book book = (Book)booksList.get(0);
        RequestBuilder request = MockMvcRequestBuilders.get("/books/title", new Object[0]).param("title", new String[]{book.getTitle()});
        this.mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.FOUND.value()))).andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Success"))).andExpect(MockMvcResultMatchers.jsonPath("$.data.id", Matchers.is(book.getId()))).andExpect(MockMvcResultMatchers.jsonPath("$.data.title", Matchers.is(book.getTitle()))).andExpect(MockMvcResultMatchers.jsonPath("$.data.author", Matchers.is(book.getAuthor())));
    }

    @Test
    @Transactional
    @Rollback
    public void testGetNonExistingBookDetails() throws Exception {
        this.createMockManyBooks();
        List<Book> bookList = (List)this.bookRepository.findAll();
        RequestBuilder request = MockMvcRequestBuilders.get("/books/title", new Object[0]).param("title", new String[]{"SUPERMAN"});
        this.mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.NOT_FOUND.value()))).andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Book not found"))).andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.is(Matchers.nullValue())));
    }
}

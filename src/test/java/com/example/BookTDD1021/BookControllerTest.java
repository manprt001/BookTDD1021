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


    public BookControllerTest() {
    }

    public String getJSON(String path) throws Exception {
        Path paths = Paths.get(path);
        return new String(Files.readAllBytes(paths));
    }

    // one book in json file
    private void createMockData() throws Exception {
        String movieStr = this.getJSON("src/test/resources/onebook.json");
        Book book = (Book)this.mapper.readValue(movieStr, Book.class);
        this.bookRepository.save(book);
    }

    //Many books in json file
    private void createMockManyBooks() throws Exception {
        String bookStr = this.getJSON("src/test/resources/manybooks.json");
        TypeReference<List<Book>> books = new TypeReference<List<Book>>() {
        };
        List<Book> jsonToBookList = (List)this.mapper.readValue(bookStr, books);
        this.bookRepository.saveAll(jsonToBookList);
    }

    // function test for checking booklist is empty
    @Test
    public void testGetEmptyBooks() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.get("/books", new Object[0]))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
    }

    // Function test for post mapping adding a book
    //This test case passes when run on its own, but fails when run as a whole
    @Test
    @Transactional
    @Rollback
    public void testAddBook() throws Exception {
        String bookStr = this.getJSON("src/test/resources/onebook.json"); // giving the json path for one book
        Book book = (Book)this.mapper.readValue(bookStr, Book.class);
        RequestBuilder request = MockMvcRequestBuilders.post("/books", new Object[0]).contentType(MediaType.APPLICATION_JSON).content(bookStr);
        this.mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(book.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is(book.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.author", Matchers.is(book.getAuthor())));
    }

    // test get mapping to view one book
    @Test
    @Transactional
    @Rollback
    public void testGetBooks() throws Exception {
        this.createMockData();
        Book book = (Book)this.bookRepository.findAll().iterator().next();
        this.mvc.perform(MockMvcRequestBuilders.get("/books", new Object[0])).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(book.getId()))).andExpect(MockMvcResultMatchers.jsonPath("$[0].title", Matchers.is(book.getTitle()))).andExpect(MockMvcResultMatchers.jsonPath("$[0].author", Matchers.is(book.getAuthor())));
    }

    // test get mapping to view many books
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

    //
    // function test for checking if the book exist by giving tittle
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


    //
    // function test for checking if there is any book by given author exist
    @Test
    @Transactional
    @Rollback
    public void testGetExistingAuthorDetails() throws Exception {
        this.createMockManyBooks();
        List<Book> booksList = (List)this.bookRepository.findAll();
        Book book = (Book)booksList.get(3); //index is 3 for james charles
        RequestBuilder request = MockMvcRequestBuilders.get("/books/author", new Object[0])
                .param("author", new String[]{"James Charles"});
        this.mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.FOUND.value())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Success")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id", Matchers.is(book.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.title", Matchers.is(book.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.author", Matchers.is(book.getAuthor())));
    }

    @Test
    @Transactional
    @Rollback
    public void testGetNonExistingAuthorDetails() throws Exception {
        this.createMockManyBooks();
        List<Book> bookList = (List)this.bookRepository.findAll();
        RequestBuilder request = MockMvcRequestBuilders.get("/books/author", new Object[0])
                .param("author", new String[]{"Manpreet"});
        this.mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.NOT_FOUND.value())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Author not found")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.is(Matchers.nullValue())));
    }

    //    @Test
//    @Transactional
//    @Rollback
//    public void testGetExistingAuthorMany() throws Exception {
//        this.createMockManyBooks();
//        List<Book> booksList = (List)this.bookRepository.findAll();
//        Book book8 = (Book)booksList.get(7);
//        Book book9 = (Book)booksList.get(8);
//        RequestBuilder request = MockMvcRequestBuilders.get("/books/author", new Object[0])
//                .param("author", new String[]{"J.K. Rowling"});
//        this.mvc.perform(request)
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.FOUND.value())))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Success")))
//                .andExpect(MockMvcResultMatchers.jsonPath("$[7].id", Matchers.is(book8.getId())))
//                .andExpect(MockMvcResultMatchers.jsonPath("$[7].title", Matchers.is(book8.getTitle())))
//                .andExpect(MockMvcResultMatchers.jsonPath("$[7].author", Matchers.is(book8.getAuthor())))
//                .andExpect(MockMvcResultMatchers.jsonPath("$[8].id", Matchers.is(book9.getId())))
//                .andExpect(MockMvcResultMatchers.jsonPath("$[8].title", Matchers.is(book9.getTitle())))
//                .andExpect(MockMvcResultMatchers.jsonPath("$[8].author", Matchers.is(book9.getAuthor())));
//    }

     // function test for checking if the book does not exist by giving title
    @Test
    @Transactional
    @Rollback
    public void testGetNonExistingBookDetails() throws Exception {
        this.createMockManyBooks();
        List<Book> bookList = (List)this.bookRepository.findAll();
        RequestBuilder request = MockMvcRequestBuilders.get("/books/title", new Object[0])
                .param("title", new String[]{"SUPERMAN"});
        this.mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.NOT_FOUND.value())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Book not found")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.is(Matchers.nullValue())));
    }

//    @Test
//    @Transactional
//    @Rollback
//    public void testDeleteBook() throws Exception {
////        this.createMockManyBooks();
////        List<Book> bookList = (List)this.bookRepository.findAll();
////        Book book3 = (Book)bookList.get(2);
////        String deleteid = String.valueOf(book3.getId());
////        RequestBuilder request = MockMvcRequestBuilders.delete("/books/id", new Object[0])
////                .param("id", deleteid);
////        this.mvc.perform(request)
////                .andExpect(MockMvcResultMatchers.status().isOk())
////                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.FOUND.value())))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Book deleted")))
////                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.is(Matchers.nullValue())));
//        mvc.perform( MockMvcRequestBuilders.delete("/{id}", 1) )
//                .andExpect(MockMvcResultMatchers.status().isAccepted());}


    // test function for deleting a book
    //This test case passes when run on its own, but fails when run as a whole
    @Test
    @Transactional
    @Rollback
    public void testDeleteBook() throws Exception {
        this.createMockData();
        Book book = (Book)this.bookRepository.findAll().iterator().next();
        this.mvc.perform(MockMvcRequestBuilders.delete("/books/{id}", 1))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


//    @Test
//    @Transactional
//    @Rollback
//    public void testUpdateName() throws Exception{
//        this.createMockManyBooks();
//        List<Book> bookList = (List)this.bookRepository.findAll();
//        Book book3 = (Book)bookList.get(2);
//        mvc.perform( MockMvcRequestBuilders
//                .put("/update/{id}/name/{name}",2)
//                .param("title", "Cafe"))
//                //.content(asJsonString(new Book"id2","title2","author2")))
//                //.contentType(MediaType.APPLICATION_JSON)
//                //.accept(MediaType.APPLICATION_JSON)
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.FOUND.value())))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Success")))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id", Matchers.is(book3.getId())))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.data.title", Matchers.is(book3.getTitle())))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.data.author", Matchers.is(book3.getAuthor())));
//    }


    // test function for updating the book title by giving book id
    //This test case passes when run on its own, but fails when run as a whole
    @Test
    @Transactional
    @Rollback
    public void testUpdateTitle() throws Exception {
        this.createMockManyBooks();
        List<Book> bookList = (List)this.bookRepository.findAll();
        Book book1 = (Book)bookList.get(0);
        mvc.perform(MockMvcRequestBuilders.put("/books/1/A Notion of Love"))
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .content("{\"id\": 1,\n" +
//                        "  \"title\": \"A Notion of Love\",\n" +
//                        "  \"author\": \"Abbie Wiliams\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.FOUND.value())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Book updated")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.is(Matchers.nullValue())));

    }

}




package com.example.books.controller;

import com.example.books.dto.CreateBookDTO;
import com.example.books.model.Author;
import com.example.books.model.Book;
import com.example.books.repository.AuthorRepository;
import com.example.books.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BookControllerTest {

    private MockMvc mockMvc;
    private BookRepository bookRepo;
    private AuthorRepository authorRepo;
    private ObjectMapper objectMapper;

    private Author author;
    private Book book;

    @BeforeEach
    void setUp() {
        bookRepo = mock(BookRepository.class);
        authorRepo = mock(AuthorRepository.class);
        objectMapper = new ObjectMapper();

        BookController controller = new BookController(bookRepo, authorRepo);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        author = new Author();
        author.setId(1L);
        author.setName("Victor Hugo");

        book = new Book();
        book.setId(10L);
        book.setTitle("Les Misérables");
        book.setAuthor(author);
    }

    @Test
    void shouldFindAllBooks() throws Exception {
        when(bookRepo.findAll()).thenReturn(List.of(book));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(10)))
                .andExpect(jsonPath("$[0].title", is("Les Misérables")));
    }

    @Test
    void shouldFindBookById() throws Exception {
        when(bookRepo.findById(10L)).thenReturn(Optional.of(book));

        mockMvc.perform(get("/api/books/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Les Misérables")));
    }

    @Test
    void shouldCreateBook() throws Exception {
        CreateBookDTO dto = new CreateBookDTO("Notre-Dame de Paris");
        Book saved = new Book();
        saved.setId(11L);
        saved.setTitle(dto.title());
        saved.setAuthor(author);

        when(authorRepo.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepo.save(ArgumentMatchers.any(Book.class))).thenReturn(saved);

        mockMvc.perform(post("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(11)))
                .andExpect(jsonPath("$.title", is("Notre-Dame de Paris")));
    }

    @Test
    void shouldUpdateBook() throws Exception {
        CreateBookDTO dto = new CreateBookDTO("Le Dernier Jour d’un Condamné");

        when(bookRepo.findById(10L)).thenReturn(Optional.of(book));
        when(bookRepo.save(ArgumentMatchers.any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/api/books/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Le Dernier Jour d’un Condamné")));
    }

    @Test
    void shouldDeleteBook() throws Exception {
        doNothing().when(bookRepo).deleteById(10L);

        mockMvc.perform(delete("/api/books/10"))
                .andExpect(status().isOk());

        verify(bookRepo, times(1)).deleteById(10L);
    }
}

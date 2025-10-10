package com.example.books.controller;

import com.example.books.dto.AuthorDTO;
import com.example.books.dto.BookDTO;
import com.example.books.dto.CreateAuthorDTO;
import com.example.books.model.Author;
import com.example.books.repository.AuthorRepository;
import com.example.books.repository.BookRepository;
import com.example.books.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthorController.class,
        excludeAutoConfiguration = org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorRepository authorRepo;

    @MockBean
    private BookRepository bookRepo;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void shouldCreateAuthor() throws Exception {
        Author saved = new Author();
        saved.setId(1L);
        saved.setName("Victor Hugo");

        Mockito.when(authorRepo.save(any(Author.class))).thenReturn(saved);

        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Victor Hugo\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Victor Hugo"));
    }

    @Test
    void shouldFindAuthorById() throws Exception {
        Author a = new Author();
        a.setId(1L);
        a.setName("Albert Camus");

        Mockito.when(authorRepo.findById(1L)).thenReturn(Optional.of(a));

        mockMvc.perform(get("/api/authors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Albert Camus"));
    }
}

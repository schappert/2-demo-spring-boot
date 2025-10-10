package com.example.books.controller;

import com.example.books.dto.*;
import com.example.books.model.Author;
import com.example.books.repository.AuthorRepository;
import com.example.books.repository.BookRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {
    private final AuthorRepository authorRepo;
    private final BookRepository bookRepo;

    public AuthorController(AuthorRepository authorRepo, BookRepository bookRepo) {
        this.authorRepo = authorRepo;
        this.bookRepo = bookRepo;
    }

    @GetMapping
    public List<AuthorDTO> findAll() {
        return authorRepo.findAll().stream()
                .map(a -> new AuthorDTO(
                        a.getId(),
                        a.getName(),
                        a.getBooks().stream()
                                .map(b -> new BookDTO(b.getId(), b.getTitle()))
                                .toList()
                ))
                .collect(Collectors.toList());
    }

    @PostMapping
    public AuthorDTO create(@RequestBody CreateAuthorDTO dto) {
        Author author = new Author();
        author.setName(dto.name());
        Author saved = authorRepo.save(author);
        return new AuthorDTO(saved.getId(), saved.getName(), List.of());
    }

    @GetMapping("/{id}")
    public AuthorDTO findById(@PathVariable Long id) {
        Author a = authorRepo.findById(id).orElseThrow();
        return new AuthorDTO(
                a.getId(),
                a.getName(),
                a.getBooks().stream()
                        .map(b -> new BookDTO(b.getId(), b.getTitle()))
                        .toList()
        );
    }

    @PutMapping("/{id}")
    public AuthorDTO update(@PathVariable Long id, @RequestBody CreateAuthorDTO dto) {
        Author a = authorRepo.findById(id).orElseThrow();
        a.setName(dto.name());
        Author saved = authorRepo.save(a);
        return new AuthorDTO(
                saved.getId(),
                saved.getName(),
                saved.getBooks().stream()
                        .map(b -> new BookDTO(b.getId(), b.getTitle()))
                        .toList()
        );
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        authorRepo.deleteById(id);
    }

    @GetMapping("/search")
    public List<AuthorDTO> searchAuthors(@RequestParam String name) {
        return authorRepo.findByNameContainingIgnoreCase(name).stream()
                .map(a -> new AuthorDTO(a.getId(), a.getName(), List.of()))
                .toList();
    }
}

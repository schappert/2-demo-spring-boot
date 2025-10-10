package com.example.books.controller;

import com.example.books.dto.*;
import com.example.books.model.*;
import com.example.books.repository.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookRepository bookRepo;
    private final AuthorRepository authorRepo;

    public BookController(BookRepository bookRepo, AuthorRepository authorRepo) {
        this.bookRepo = bookRepo;
        this.authorRepo = authorRepo;
    }

    @GetMapping
    public List<BookDTO> findAll() {
        return bookRepo.findAll().stream()
                .map(b -> new BookDTO(b.getId(), b.getTitle()))
                .collect(Collectors.toList());
    }

    @PostMapping("/{authorId}")
    public BookDTO create(@PathVariable Long authorId, @RequestBody CreateBookDTO dto) {
        Author author = authorRepo.findById(authorId).orElseThrow();
        Book book = new Book();
        book.setTitle(dto.title());
        book.setAuthor(author);
        Book saved = bookRepo.save(book);
        return new BookDTO(saved.getId(), saved.getTitle());
    }

    @GetMapping("/{id}")
    public BookDTO findById(@PathVariable Long id) {
        Book b = bookRepo.findById(id).orElseThrow();
        return new BookDTO(b.getId(), b.getTitle());
    }

    @PutMapping("/{id}")
    public BookDTO update(@PathVariable Long id, @RequestBody CreateBookDTO dto) {
        Book b = bookRepo.findById(id).orElseThrow();
        b.setTitle(dto.title());
        Book saved = bookRepo.save(b);
        return new BookDTO(saved.getId(), saved.getTitle());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bookRepo.deleteById(id);
    }

    @GetMapping("/search")
    public List<BookDTO> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author
    ) {
        return bookRepo.searchBooks(title, author).stream()
                .map(b -> new BookDTO(b.getId(), b.getTitle()))
                .toList();
    }
}

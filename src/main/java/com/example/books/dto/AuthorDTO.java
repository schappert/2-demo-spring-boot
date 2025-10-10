package com.example.books.dto;

import java.util.List;

public record AuthorDTO(
        Long id,
        String name,
        List<BookDTO> books
) {}

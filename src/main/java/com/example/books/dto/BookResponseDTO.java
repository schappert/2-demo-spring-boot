package com.example.books.dto;

public record BookResponseDTO(
        Long id,
        String title,
        Long authorId,
        String authorName
) {}

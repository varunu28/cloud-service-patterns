package com.varunu28.bookservice.model;

public record CreateBookRequest(String title, String author, String genre, int publicationYear) {
}

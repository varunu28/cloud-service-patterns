package com.varunu28.bookservice.model;

import java.util.UUID;

public record Book(UUID id, String title, String author, String genre, int publicationYear) {
}

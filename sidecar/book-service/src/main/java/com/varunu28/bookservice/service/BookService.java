package com.varunu28.bookservice.service;

import com.varunu28.bookservice.model.Book;
import com.varunu28.bookservice.model.CreateBookRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BookService {

    private final Map<UUID, Book> bookStore = new ConcurrentHashMap<>();

    public BookService() {
        addSeedBook("The Pragmatic Programmer", "David Thomas", "Technology", 1999);
        addSeedBook("Clean Code", "Robert C. Martin", "Technology", 2008);
        addSeedBook("Designing Data-Intensive Applications", "Martin Kleppmann", "Technology", 2017);
        addSeedBook("The Hitchhiker's Guide to the Galaxy", "Douglas Adams", "Science Fiction", 1979);
    }

    private void addSeedBook(String title, String author, String genre, int year) {
        UUID id = UUID.randomUUID();
        bookStore.put(id, new Book(id, title, author, genre, year));
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(bookStore.values());
    }

    public Optional<Book> getBookById(UUID id) {
        return Optional.ofNullable(bookStore.get(id));
    }

    public Book createBook(CreateBookRequest request) {
        UUID id = UUID.randomUUID();
        Book book = new Book(id, request.title(), request.author(), request.genre(), request.publicationYear());
        bookStore.put(id, book);
        return book;
    }

    public Optional<Book> deleteBook(UUID id) {
        return Optional.ofNullable(bookStore.remove(id));
    }
}

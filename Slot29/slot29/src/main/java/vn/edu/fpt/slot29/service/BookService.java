package vn.edu.fpt.slot29.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.slot29.domain.Book;
import vn.edu.fpt.slot29.repository.IBookRepository;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class BookService implements IBookService {
    private final IBookRepository books;
    public BookService(IBookRepository books) { this.books = books; }
    @Override @Transactional public Book createBook(Book book) { return books.save(book); }
    @Override public Book getBook(Long id) {
        return books.findById(id).orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));
    }
    @Override public List<Book> getBooks() { return books.findAll(); }
}

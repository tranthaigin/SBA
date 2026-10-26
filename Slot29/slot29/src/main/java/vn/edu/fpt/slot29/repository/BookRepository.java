package vn.edu.fpt.slot29.repository;

import org.springframework.stereotype.Repository;
import vn.edu.fpt.slot29.dao.BookDAO;
import vn.edu.fpt.slot29.domain.Book;
import java.util.*;

@Repository
public class BookRepository implements IBookRepository {
    private final BookDAO dao;
    public BookRepository(BookDAO dao) { this.dao = dao; }
    @Override public Book save(Book book) { return dao.save(book); }
    @Override public Optional<Book> findById(Long id) { return dao.findById(id); }
    @Override public List<Book> findAll() { return dao.findAll(); }
    @Override public long count() { return dao.count(); }
}

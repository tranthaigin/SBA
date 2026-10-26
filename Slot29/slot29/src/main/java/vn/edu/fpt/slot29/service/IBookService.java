package vn.edu.fpt.slot29.service;

import vn.edu.fpt.slot29.domain.Book;
import java.util.List;

public interface IBookService {
    Book createBook(Book book);
    Book getBook(Long id);
    List<Book> getBooks();
}

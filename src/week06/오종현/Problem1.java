package week06.오종현;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

public class Problem1 {

    static class Book {
        private final Long id;
        private final String title;
        private final String author;

        public Book(Long id, String title, String author) {
            this.id = id;
            this.title = title;
            this.author = author;
        }

        public Long getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getAuthor() {
            return author;
        }

        @Override
        public String toString() {
            return "Book{id=" + id + ", title='" + title + "', author='" + author + "'}";
        }
    }

    interface BookRepository {
        void save(Book book);

        Book findById(Long id);
    }

    interface BookService {
        void register(Book book);

        Book findBook(Long id);
    }

    @Component
    static class MemoryBookRepository implements BookRepository {
        private final Map<Long, Book> store = new HashMap<>();

        @Override
        public void save(Book book) {
            store.put(book.getId(), book);
        }

        @Override
        public Book findById(Long id) {
            return store.get(id);
        }
    }

    @Component
    static class BookServiceImpl implements BookService {

        private final BookRepository bookRepository;

        @Autowired
        public BookServiceImpl(BookRepository bookRepository) {
            this.bookRepository = bookRepository;
        }

        @Override
        public void register(Book book) {
            bookRepository.save(book);
        }

        @Override
        public Book findBook(Long id) {
            return bookRepository.findById(id);
        }
    }

    @Configuration
    @ComponentScan(basePackageClasses = Problem1.class)
    static class AutoAppConfig {
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ac =
                new AnnotationConfigApplicationContext(AutoAppConfig.class);

        BookService bookService = ac.getBean(BookService.class);

        Book book = new Book(1L, "스프링 핵심 원리", "김영한");
        bookService.register(book);

        Book found = bookService.findBook(1L);

        System.out.println("등록한 도서: " + book.getTitle());
        System.out.println("조회한 도서: " + found.getTitle());
        System.out.println("일치 여부: " + book.getTitle().equals(found.getTitle()));

        ac.close();
    }
}
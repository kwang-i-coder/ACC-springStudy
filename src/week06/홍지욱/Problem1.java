package week06.홍지욱;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * ┌──────────────────────────────────────────────────────────────────┐
 * │  [실습 문제 1]  컴포넌트 스캔 + 의존관계 자동 주입       (난이도: 쉬움)  │
 * │  섹션 6 · 섹션 7                                                  │
 * └──────────────────────────────────────────────────────────────────┘
 */
public class Problem1 {

    // ──────────────────────────────────────────────────────────────────────
    // 1. 지원 코드
    // ──────────────────────────────────────────────────────────────────────

    /** 도서 도메인 */
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

    /** 도서 저장소 인터페이스 */
    interface BookRepository {
        void save(Book book);

        Book findById(Long id);
    }

    /** 도서 서비스 인터페이스 */
    interface BookService {
        void register(Book book);

        Book findBook(Long id);
    }

    // ──────────────────────────────────────────────────────────────────────
    // TODO 1 - @Component 추가
    // ──────────────────────────────────────────────────────────────────────
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

    // ──────────────────────────────────────────────────────────────────────
    // TODO 2 - BookServiceImpl 완성
    // ──────────────────────────────────────────────────────────────────────
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

    // ──────────────────────────────────────────────────────────────────────
    // TODO 3 - @Configuration + @ComponentScan 추가
    // ──────────────────────────────────────────────────────────────────────
    @Configuration
    @ComponentScan
    static class AutoAppConfig {
        // 비어 있어도 괜찮습니다!
    }

    // ──────────────────────────────────────────────────────────────────────
    // main
    // ──────────────────────────────────────────────────────────────────────
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
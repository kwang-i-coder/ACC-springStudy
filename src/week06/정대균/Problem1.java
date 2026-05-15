package week06.정대균;

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
 *
 * 배경:
 *   지금까지는 AppConfig에 @Bean을 하나하나 직접 등록했습니다.
 *   이번 실습에서는 @ComponentScan + @Component + @Autowired를 사용해
 *   스프링이 자동으로 빈을 등록하고 의존관계를 주입하도록 바꿔봅시다.
 *
 * 도메인: 온라인 서점의 도서(Book) 관리 서비스
 *
 * TODO 목록:
 *   TODO 1 - MemoryBookRepository에 @Component 추가
 *   TODO 2 - BookServiceImpl 완성 (@Component + @Autowired 생성자 주입)
 *   TODO 3 - AutoAppConfig에 @Configuration + @ComponentScan 추가
 */
public class Problem1 {

    // ──────────────────────────────────────────────────────────────────────
    // 1. 지원 코드 (수정 금지)
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

        public Long getId()      { return id; }
        public String getTitle() { return title; }
        public String getAuthor(){ return author; }

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
    // TODO 1
    // ──────────────────────────────────────────────────────────────────────
    // MemoryBookRepository에 @Component를 붙여서
    // 컴포넌트 스캔 시 스프링이 자동으로 빈으로 등록하게 하세요.
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
    // TODO 2
    // ──────────────────────────────────────────────────────────────────────
    // BookServiceImpl을 완성하세요.
    //   ① @Component를 붙여 자동 빈 등록 대상으로 만드세요.
    //   ② BookRepository 필드를 선언하되 final 키워드를 사용하세요.
    //   ③ @Autowired를 붙인 생성자를 작성해 의존관계를 주입받으세요.
    //   ④ register()와 findBook() 메서드를 구현하세요.
    //
    // 힌트: 생성자가 딱 1개면 @Autowired를 생략해도 자동 주입됩니다.
    //       하지만 이번엔 명시적으로 붙여봅시다!
    // ──────────────────────────────────────────────────────────────────────
    @Component
    static class BookServiceImpl implements BookService {

        // TODO ②: private final BookRepository bookRepository;
        private final BookRepository bookRepository;
        // TODO ③: @Autowired 생성자 작성
        @Autowired
        public BookServiceImpl(BookRepository bookRepository) {
            this.bookRepository = bookRepository;
        }

        @Override
        public void register(Book book) {
            // TODO ④: bookRepository.save(book) 호출
            bookRepository.save(book);
        }

        @Override
        public Book findBook(Long id) {
            // TODO ④: return bookRepository.findById(id)
            return bookRepository.findById(id);
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    // TODO 3
    // ──────────────────────────────────────────────────────────────────────
    // AutoAppConfig에 아래 두 가지를 적용하세요.
    //   ① @Configuration  - 스프링 설정 클래스임을 선언
    //   ② @ComponentScan  - @Component가 붙은 클래스를 자동으로 스캔해 빈 등록
    //
    // 참고: @Bean 메서드는 작성하지 않아도 됩니다.
    //       @ComponentScan이 @Component 클래스들을 알아서 찾아 등록해줍니다.
    // ──────────────────────────────────────────────────────────────────────
    @Configuration
    @ComponentScan
    static class AutoAppConfig {
        // 비어 있어도 괜찮습니다!
    }

    // ──────────────────────────────────────────────────────────────────────
    // main: 먼저 실행해서 오류를 확인한 뒤, TODO를 채워서 고쳐보세요.
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

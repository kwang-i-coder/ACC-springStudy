package week03.안시현;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 문제 3: JPA 엔티티 매핑 + AOP 실행 시간 측정 [어려움]
 *
 * Part A - Member 엔티티 완성:
 *  - @Entity, @Id, @GeneratedValue(strategy = GenerationType.IDENTITY) 추가
 *  - name 필드에 @Column(name = "username") 추가
 *
 * Part B - TimeTraceAop 완성:
 *  - @Aspect, @Component 추가
 *  - @Around 로 hello.hellospring 패키지 전체에 적용
 *  - 실행 시간을 "[클래스명.메서드명] 실행 시간: Xms" 형태로 출력
 *
 * 힌트 A: @Entity 클래스는 JPA 가 관리하는 객체입니다.
 * 힌트 B: ProceedingJoinPoint.proceed() 로 실제 메서드를 실행합니다.
 *         System.currentTimeMillis() 로 시작/종료 시간을 측정하세요.
 */
public class Problem3 {

    // ──────────────────────────────────────────────────────────────────────
    // Part A: JPA 엔티티 매핑
    // TODO: 아래 Member 클래스에 필요한 애노테이션을 추가하세요.
    // ──────────────────────────────────────────────────────────────────────

    // TODO: @Entity 추가
    static class Member {

        // TODO: @Id, @GeneratedValue(strategy = GenerationType.IDENTITY) 추가
        private Long id;

        // TODO: @Column(name = "username") 추가
        private String name;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    // ──────────────────────────────────────────────────────────────────────
    // Part B: AOP 실행 시간 측정
    // TODO: 아래 TimeTraceAop 클래스를 완성하세요.
    // ──────────────────────────────────────────────────────────────────────

    // TODO: @Aspect, @Component 추가
    static class TimeTraceAop {

        // TODO: @Around("execution(* hello.hellospring..*(..))") 추가
        public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
            long start = System.currentTimeMillis();
            System.out.println("START: " + joinPoint.toString());
            try {
                return joinPoint.proceed();
            } finally {
                long finish = System.currentTimeMillis();
                long timeMs = finish - start;
                // TODO: "[클래스명.메서드명] 실행 시간: Xms" 형태로 출력하도록 수정하세요.
                System.out.println("END: " + joinPoint.toString() + " " + timeMs + "ms");
            }
        }
    }
}

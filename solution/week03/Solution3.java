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
 * 문제 3 정답: JPA 엔티티 매핑 + AOP 실행 시간 측정
 */
public class Solution3 {

    // ──────────────────────────────────────────────────────────────────────
    // Part A 정답: JPA 엔티티 매핑
    // ──────────────────────────────────────────────────────────────────────

    @Entity
    static class Member {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "username")
        private String name;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    // ──────────────────────────────────────────────────────────────────────
    // Part B 정답: AOP 실행 시간 측정
    // ──────────────────────────────────────────────────────────────────────

    @Aspect
    @Component
    static class TimeTraceAop {

        @Around("execution(* hello.hellospring..*(..))")
        public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
            long start = System.currentTimeMillis();
            try {
                return joinPoint.proceed();
            } finally {
                long finish = System.currentTimeMillis();
                long timeMs = finish - start;
                String className = joinPoint.getSignature().getDeclaringTypeName();
                String methodName = joinPoint.getSignature().getName();
                System.out.println("[" + className + "." + methodName + "] 실행 시간: " + timeMs + "ms");
            }
        }
    }
}

package week06.solution;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * ┌──────────────────────────────────────────────────────────────────┐
 * │  [실습 문제 2]  빈 생명주기 콜백 + 빈 스코프             (난이도: 중간)  │
 * │  섹션 8 · 섹션 9                                                  │
 * └──────────────────────────────────────────────────────────────────┘
 *
 * Part A  (섹션 8 - 빈 생명주기 콜백)
 *   스프링 빈이 초기화·소멸될 때 특정 메서드를 자동으로 호출하도록
 *   @PostConstruct 와 @PreDestroy 를 사용합니다.
 *
 * Part B  (섹션 9 - 빈 스코프)
 *   프로토타입 스코프 빈을 싱글톤 빈 안에서 올바르게 사용하려면
 *   ObjectProvider 가 필요한 이유를 직접 체험합니다.
 *
 * TODO 목록:
 *   TODO A-1 - CafeConnection.init()에 @PostConstruct 추가
 *   TODO A-2 - CafeConnection.close()에 @PreDestroy 추가
 *   TODO B-1 - OrderCounter에 @Scope("prototype") 추가
 *   TODO B-2 - CafeOrderService.processOrder()를 ObjectProvider로 완성
 */
public class Problem2 {

    // ──────────────────────────────────────────────────────────────────────
    // 1. 지원 코드 (수정 금지)
    // ──────────────────────────────────────────────────────────────────────

    // ════════════════════════════════════════════════════════════════════════
    //  Part A: 빈 생명주기 콜백 (섹션 8)
    // ════════════════════════════════════════════════════════════════════════

    /**
     * 카페 POS 서버와의 연결을 담당하는 클래스.
     *
     * 스프링 빈이 완전히 준비되면(의존관계 주입 완료 후) connect()가 호출되어야 하고,
     * 스프링 컨테이너가 종료될 때 disconnect()가 호출되어 연결이 안전하게 닫혀야 합니다.
     *
     * 문제: 지금은 init()과 close()가 자동으로 호출되지 않습니다.
     *       @PostConstruct / @PreDestroy 를 붙여서 자동 호출되게 하세요.
     */
    @Component
    static class CafeConnection {
        private boolean connected = false;

        private void connect() {
            connected = true;
            System.out.println("[connect] POS 서버 연결 완료");
        }

        private void disconnect() {
            connected = false;
            System.out.println("[disconnect] POS 서버 연결 해제");
        }

        public void sendOrder(String item) {
            if (!connected) throw new IllegalStateException("POS 서버에 연결되어 있지 않습니다.");
            System.out.println("[POS 전송] 주문 항목: " + item);
        }

        public boolean isConnected() { return connected; }

        // ──────────────────────────────────────────────────────────────────
        // TODO A-1
        // ──────────────────────────────────────────────────────────────────
        // 빈 초기화 완료 직후 이 메서드가 자동으로 호출되도록
        // @PostConstruct 애노테이션을 붙이세요.
        // ──────────────────────────────────────────────────────────────────
        public void init() {
            System.out.println("[init] CafeConnection 초기화");
            connect();
        }

        // ──────────────────────────────────────────────────────────────────
        // TODO A-2
        // ──────────────────────────────────────────────────────────────────
        // 빈 소멸 직전에 이 메서드가 자동으로 호출되도록
        // @PreDestroy 애노테이션을 붙이세요.
        // ──────────────────────────────────────────────────────────────────
        public void close() {
            System.out.println("[close] CafeConnection 종료");
            disconnect();
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Part B: 빈 스코프 (섹션 9)
    // ════════════════════════════════════════════════════════════════════════

    /**
     * 주문 1건당 독립적으로 생성되어야 하는 카운터.
     *
     * 현재는 싱글톤으로 동작해서 모든 주문이 같은 카운터를 공유합니다.
     * @Scope("prototype")을 붙여서 요청할 때마다 새 인스턴스가 생성되게 하세요.
     */
    // ──────────────────────────────────────────────────────────────────────
    // TODO B-1
    // ──────────────────────────────────────────────────────────────────────
    // OrderCounter가 스프링 컨테이너에 요청할 때마다 새로 생성되도록
    // @Scope("prototype") 을 추가하세요.
    // ──────────────────────────────────────────────────────────────────────
    @Component
    static class OrderCounter {
        private int count = 0;

        public void increment() { count++; }
        public int getCount()   { return count; }

        @PostConstruct
        public void init() {
            System.out.println("[OrderCounter] 새 카운터 생성: " + this);
        }
    }

    /**
     * 주문 처리 서비스 (싱글톤).
     *
     * 주문이 들어올 때마다 새로운 OrderCounter를 사용해야 합니다.
     * 하지만 싱글톤 빈은 생성 시점에만 의존관계 주입을 받기 때문에,
     * 단순히 OrderCounter를 @Autowired로 주입받으면 항상 같은 인스턴스를 사용하게 됩니다.
     *
     * 해결책: ObjectProvider<OrderCounter>를 사용해 필요할 때마다 새 인스턴스를 꺼내세요.
     */
    @Component
    static class CafeOrderService {

        // ──────────────────────────────────────────────────────────────────
        // TODO B-2
        // ──────────────────────────────────────────────────────────────────
        // ① ObjectProvider<OrderCounter> 타입의 필드를 선언하세요.
        //    (필드명 예시: counterProvider)
        //
        // ② @Autowired 생성자를 작성해 ObjectProvider를 주입받으세요.
        //
        // ③ processOrder() 안에서:
        //    - counterProvider.getObject()로 새 OrderCounter를 꺼내세요.
        //    - increment()를 호출하세요.
        //    - getCount()를 반환하세요.
        //
        // 주의: getObject()를 호출할 때마다 새 프로토타입 빈이 생성됩니다.
        //       OrderCounter를 필드에 직접 @Autowired 주입받으면 어떻게 될지 생각해보세요.
        // ──────────────────────────────────────────────────────────────────

        // TODO ①: private final ObjectProvider<OrderCounter> counterProvider;

        // TODO ②: @Autowired 생성자 작성

        public int processOrder(String itemName) {
            System.out.println("[주문 처리] " + itemName);
            // TODO ③: counterProvider.getObject()로 새 OrderCounter를 꺼내
            //          increment() 호출 후 getCount() 반환
            return -1; // 완성 전 임시 반환값
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    // 설정 클래스 (수정 금지)
    // ──────────────────────────────────────────────────────────────────────
    @Configuration
    @ComponentScan
    static class CafeAppConfig { }

    // ──────────────────────────────────────────────────────────────────────
    // main: TODO를 모두 완성한 후 아래 주석을 해제하고 실행해보세요.
    // ──────────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        // AnnotationConfigApplicationContext ac =
        //         new AnnotationConfigApplicationContext(CafeAppConfig.class);
        //
        // // ── Part A 확인 ─────────────────────────────────────────────────
        // CafeConnection conn = ac.getBean(CafeConnection.class);
        // System.out.println("연결 상태: " + conn.isConnected()); // true 여야 함
        // conn.sendOrder("아메리카노");
        //
        // // ── Part B 확인 ─────────────────────────────────────────────────
        // CafeOrderService orderService = ac.getBean(CafeOrderService.class);
        //
        // int count1 = orderService.processOrder("카페라떼");
        // int count2 = orderService.processOrder("에스프레소");
        //
        // System.out.println("주문 1 카운트: " + count1); // 1 이어야 함
        // System.out.println("주문 2 카운트: " + count2); // 1 이어야 함 (새 프로토타입!)
        //
        // ac.close(); // @PreDestroy 호출 확인
    }
}

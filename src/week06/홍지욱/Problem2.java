package week06.홍지욱;

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
 */
public class Problem2 {

    // ════════════════════════════════════════════════════════════════════════
    //  Part A: 빈 생명주기 콜백
    // ════════════════════════════════════════════════════════════════════════

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
            if (!connected) {
                throw new IllegalStateException("POS 서버에 연결되어 있지 않습니다.");
            }
            System.out.println("[POS 전송] 주문 항목: " + item);
        }

        public boolean isConnected() {
            return connected;
        }

        @PostConstruct
        public void init() {
            System.out.println("[init] CafeConnection 초기화");
            connect();
        }

        @PreDestroy
        public void close() {
            System.out.println("[close] CafeConnection 종료");
            disconnect();
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Part B: 빈 스코프
    // ════════════════════════════════════════════════════════════════════════

    @Scope("prototype")
    @Component
    static class OrderCounter {
        private int count = 0;

        public void increment() {
            count++;
        }

        public int getCount() {
            return count;
        }

        @PostConstruct
        public void init() {
            System.out.println("[OrderCounter] 새 카운터 생성: " + this);
        }
    }

    @Component
    static class CafeOrderService {

        private final ObjectProvider<OrderCounter> counterProvider;

        @Autowired
        public CafeOrderService(ObjectProvider<OrderCounter> counterProvider) {
            this.counterProvider = counterProvider;
        }

        public int processOrder(String itemName) {
            System.out.println("[주문 처리] " + itemName);

            OrderCounter orderCounter = counterProvider.getObject();
            orderCounter.increment();

            return orderCounter.getCount();
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    // 설정 클래스
    // ──────────────────────────────────────────────────────────────────────

    @Configuration
    @ComponentScan
    static class CafeAppConfig {
    }

    // ──────────────────────────────────────────────────────────────────────
    // main
    // ──────────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ac =
                new AnnotationConfigApplicationContext(CafeAppConfig.class);

        // ── Part A 확인 ─────────────────────────────────────────────────
        CafeConnection conn = ac.getBean(CafeConnection.class);
        System.out.println("연결 상태: " + conn.isConnected());
        conn.sendOrder("아메리카노");

        // ── Part B 확인 ─────────────────────────────────────────────────
        CafeOrderService orderService = ac.getBean(CafeOrderService.class);

        int count1 = orderService.processOrder("카페라떼");
        int count2 = orderService.processOrder("에스프레소");

        System.out.println("주문 1 카운트: " + count1);
        System.out.println("주문 2 카운트: " + count2);

        ac.close();
    }
}
package week04.김민서;

import java.util.HashMap;
import java.util.Map;

/**
 * 문제 2: AppConfig 와 관심사의 분리 (OCP, DIP)
 */
public class Problem2 {

    // ──────────────────────────────────────────────────────────────────────
    // 1. 지원 코드 (수정 금지)
    // ──────────────────────────────────────────────────────────────────────

    enum Grade { BASIC, VIP }

    static class Member {
        private Long id;
        private String name;
        private Grade grade;

        public Member(Long id, String name, Grade grade) {
            this.id = id;
            this.name = name;
            this.grade = grade;
        }

        public Long getId() { return id; }
        public Grade getGrade() { return grade; }
        public String getName() { return name; }
    }

    interface MemberRepository {
        void save(Member member);
        Member findById(Long memberId);
    }

    static class MemoryMemberRepository implements MemberRepository {
        private static Map<Long, Member> store = new HashMap<>();

        @Override
        public void save(Member member) {
            store.put(member.getId(), member);
        }

        @Override
        public Member findById(Long memberId) {
            return store.get(memberId);
        }
    }

    interface DiscountPolicy {
        int discount(Member member, int price);
    }

    static class FixDiscountPolicy implements DiscountPolicy {
        private int discountFixAmount = 1000;

        @Override
        public int discount(Member member, int price) {
            if (member.getGrade() == Grade.VIP) return discountFixAmount;
            return 0;
        }
    }

    static class RateDiscountPolicy implements DiscountPolicy {
        private int discountPercent = 10;

        @Override
        public int discount(Member member, int price) {
            if (member.getGrade() == Grade.VIP) return price * discountPercent / 100;
            return 0;
        }
    }

    static class Order {
        private Long memberId;
        private String itemName;
        private int itemPrice;
        private int discountPrice;

        public Order(Long memberId, String itemName, int itemPrice, int discountPrice) {
            this.memberId = memberId;
            this.itemName = itemName;
            this.itemPrice = itemPrice;
            this.discountPrice = discountPrice;
        }

        public int calculatePrice() { return itemPrice - discountPrice; }
        public int getDiscountPrice() { return discountPrice; }

        @Override
        public String toString() {
            return "Order{memberId=" + memberId +
                    ", itemName='" + itemName + '\'' +
                    ", itemPrice=" + itemPrice +
                    ", discountPrice=" + discountPrice + '}';
        }
    }

    interface OrderService {
        Order createOrder(Long memberId, String itemName, int itemPrice);
    }

    // ──────────────────────────────────────────────────────────────────────
    // Part A: OrderServiceImpl 생성자 주입 방식으로 리팩터링
    // ──────────────────────────────────────────────────────────────────────

    static class OrderServiceImpl implements OrderService {


        private final MemberRepository memberRepository;
        private final DiscountPolicy discountPolicy;

        // 생성자를 통해 외부(AppConfig)에서 주입받습니다.
        public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
            this.memberRepository = memberRepository;
            this.discountPolicy = discountPolicy;
        }

        @Override
        public Order createOrder(Long memberId, String itemName, int itemPrice) {
            Member member = memberRepository.findById(memberId);
            int discountPrice = discountPolicy.discount(member, itemPrice);
            return new Order(memberId, itemName, itemPrice, discountPrice);
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    // Part B & C: AppConfig 클래스 작성 및 정책 변경
    // ──────────────────────────────────────────────────────────────────────

    static class AppConfig {

        public MemberRepository memberRepository() {
            return new MemoryMemberRepository();
        }

        public DiscountPolicy discountPolicy() {

            return new RateDiscountPolicy();
        }

        public OrderService orderService() {
            // 인젝션을 통해 관계를 맺어줍니다
            return new OrderServiceImpl(memberRepository(), discountPolicy());
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    // 검증을 위한 메인 메서드
    // ──────────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        AppConfig appConfig = new AppConfig();

        // 데이터 준비
        MemberRepository memberRepository = appConfig.memberRepository();
        Member member = new Member(1L, "민서", Grade.VIP);
        memberRepository.save(member);

        // 서비스 사용
        OrderService orderService = appConfig.orderService();
        Order order = orderService.createOrder(1L, "맥북", 2000000);

        System.out.println(order);
        System.out.println("최종 결제 금액: " + order.calculatePrice());

        /* [Part C 답변]
           질문: 정책을 바꿀 때 OrderServiceImpl 코드를 단 한 줄이라도 수정했나요?
           정답:  AppConfig(구성 영역)만 수정했습니다.
        */
    }
}


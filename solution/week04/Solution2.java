import java.util.HashMap;
import java.util.Map;

/**
 * 문제 2 정답: AppConfig 와 관심사의 분리
 */
public class Solution2 {

    // ── 지원 코드 ───────────────────────────────────────────────────────────

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
        public void save(Member member) { store.put(member.getId(), member); }

        @Override
        public Member findById(Long memberId) { return store.get(memberId); }
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

    // ── Part A 정답: OrderServiceImpl 생성자 주입으로 리팩터링 ────────────────

    static class OrderServiceImpl implements OrderService {
        private final MemberRepository memberRepository;  // 인터페이스에만 의존 ✅
        private final DiscountPolicy discountPolicy;       // 인터페이스에만 의존 ✅

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

    // ── Part B 정답: AppConfig (공연 기획자 - 객체 생성 + 연결 담당) ──────────

    static class AppConfig {

        public MemberRepository memberRepository() {
            return new MemoryMemberRepository();
        }

        public DiscountPolicy discountPolicy() {
            // return new FixDiscountPolicy();   // 정액 할인 → 아래로 교체
            return new RateDiscountPolicy();     // 정률 할인 (10%) 으로 변경
        }

        public OrderService orderService() {
            return new OrderServiceImpl(memberRepository(), discountPolicy());
        }
    }
}

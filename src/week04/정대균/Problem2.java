package week04.정대균;

import java.util.HashMap;
import java.util.Map;

/**
 * 문제 2: AppConfig 와 관심사의 분리 (OCP, DIP)
 *
 * 현재 OrderServiceImpl 은 인터페이스(DiscountPolicy)뿐 아니라,
 * 구체 클래스(FixDiscountPolicy)에도 직접 의존하는 'DIP 위반' 상태입니다.
 *
 * Part A: OrderServiceImpl 리팩터링
 * - new FixDiscountPolicy() 부분을 제거하세요.
 * - MemberRepository 와 DiscountPolicy 를 모두 생성자로 주입받도록 변경하세요.
 *
 * Part B: AppConfig (공연 기획자) 클래스 작성
 * - memberRepository() : MemoryMemberRepository 반환
 * - discountPolicy()   : FixDiscountPolicy 반환
 * - orderService()     : OrderServiceImpl 을 생성하여 반환 (의존성 주입)
 *
 * Part C: 🌟 OCP (개방-폐쇄 원칙) 체험하기 🌟
 * - 기획자가 할인 정책을 정액(1000원)에서 정률(10%)로 변경해달라고 합니다.
 * - AppConfig 의 discountPolicy() 반환값을 RateDiscountPolicy 로 변경해 보세요.
 * - [질문]: 정책을 바꿀 때 OrderServiceImpl 코드를 단 한 줄이라도 수정했나요?
 * NO!
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

    interface MemberService {
        void join(Member member);
        Member findMember(Long memberId);
    }

    // ──────────────────────────────────────────────────────────────────────
    // Part A: OrderServiceImpl 생성자 주입 방식으로 리팩터링
    // ──────────────────────────────────────────────────────────────────────

    static class OrderServiceImpl implements OrderService {

        // TODO: 아래 DIP 위반 코드를 지우고, 생성자 주입 방식으로 바꾸세요.
        private final MemberRepository memberRepository;
        private final DiscountPolicy discountPolicy;

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
         } // Part C 에서 여기만 RateDiscountPolicy 로 바꿔보세요!
         public OrderService orderService() {
             return new OrderServiceImpl(memberRepository(), discountPolicy());
         }
     }
}

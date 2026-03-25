import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 문제 1 정답: 스프링 빈 수동 등록
 */
public class Solution1 {

    // ──────────────────────────────────────────────────────────────────────
    // 지원 클래스 (Supporting classes)
    // ──────────────────────────────────────────────────────────────────────

    interface MemberRepository {
        void save(String name);
        String findByName(String name);
    }

    static class MemoryMemberRepository implements MemberRepository {
        private final java.util.Map<String, String> store = new java.util.HashMap<>();

        @Override
        public void save(String name) {
            store.put(name, name);
        }

        @Override
        public String findByName(String name) {
            return store.get(name);
        }
    }

    static class MemberService {
        private final MemberRepository memberRepository;

        public MemberService(MemberRepository memberRepository) {
            this.memberRepository = memberRepository;
        }

        public void join(String name) {
            memberRepository.save(name);
            System.out.println("회원 가입: " + name);
        }
    }

    static class MemberValidator {
        private final MemberRepository memberRepository;

        public MemberValidator(MemberRepository memberRepository) {
            this.memberRepository = memberRepository;
        }

        public boolean isDuplicate(String name) {
            return memberRepository.findByName(name) != null;
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    // 정답: SpringConfig 설정 클래스
    // ──────────────────────────────────────────────────────────────────────

    @Configuration
    static class SpringConfig {

        @Bean
        public MemberRepository memberRepository() {
            return new MemoryMemberRepository();
        }

        @Bean
        public MemberService memberService() {
            return new MemberService(memberRepository());
        }

        @Bean
        public MemberValidator memberValidator() {
            return new MemberValidator(memberRepository());
        }
    }
}

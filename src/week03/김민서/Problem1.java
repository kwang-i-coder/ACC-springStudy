package week03.김민서;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 문제 1: 스프링 빈 수동 등록 [쉬움]
 *
 * 조건:
 *  - @Configuration을 사용하여 설정 클래스로 만들 것
 *  - MemberService와 MemoryMemberRepository를 @Bean으로 등록할 것
 *  - MemberService는 MemberRepository를 생성자로 주입받을 것
 *  - MemberValidator도 @Bean으로 등록하고 MemberRepository를 생성자로 주입받을 것
 */
// TODO: 아래 클래스에 적절한 애노테이션을 추가하고, 빈 등록 메서드를 작성하세요.
public class Problem1 {

    // ──────────────────────────────────────────────────────────────────────
    // 아래 코드는 수정하지 마세요 (지원 클래스 / Supporting classes)
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
    // TODO: 여기에 SpringConfig 설정 클래스를 작성하세요
    //       (아래 주석을 참고하여 @Configuration, @Bean 을 사용하세요)
    // ──────────────────────────────────────────────────────────────────────

    @Configuration
    static class SpringConfig {

        @Bean
        public MemberRepository memberRepository(){
            return new MemoryMemberRepository();
        }

        @Bean
        public MemberService memberService(){
            return new MemberService(memberRepository());
        }

        @Bean
        public MemberValidator memberValidator(){
            return new MemberValidator(memberRepository());
        }
    }
}

package week04.김민서;

import java.util.HashMap;
import java.util.Map;

/**
 * 문제 1: 회원 도메인 구현 및 테스트
 */
public class Problem1 {

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
        public String getName() { return name; }
        public Grade getGrade() { return grade; }
    }

    interface MemberService {
        void join(Member member);
        Member findMember(Long memberId);
    }

    // ──────────────────────────────────────────────────────────────────────
    // 2. 직접 구현해 보세요! (TODO 1 ~ 3)
    // ──────────────────────────────────────────────────────────────────────

    // TODO 1: MemberRepository 인터페이스
    interface MemberRepository {
        void save(Member member);
        Member findById(Long memberId);
    }

    // TODO 2: MemoryMemberRepository 클래스
    static class MemoryMemberRepository implements MemberRepository {
        // 메모리 저장소이므로 공유되는 Map을 생성합니다.
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

    // TODO 3: MemberServiceImpl 클래스
    static class MemberServiceImpl implements MemberService {
        // 인터페이스에만 의존하도록 설계합니다.
        private final MemberRepository memberRepository;

        // 생성자 주입 (Constructor Injection)
        public MemberServiceImpl(MemberRepository memberRepository) {
            this.memberRepository = memberRepository;
        }

        @Override
        public void join(Member member) {
            memberRepository.save(member);
        }

        @Override
        public Member findMember(Long memberId) {
            return memberRepository.findById(memberId);
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    // 3. 실행 및 검증 (TODO 4)
    // ──────────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        // TODO 4: 주석 해제 후 실행 확인

        MemberRepository memberRepository = new MemoryMemberRepository();
        MemberService memberService = new MemberServiceImpl(memberRepository);

        Member member = new Member(1L, "SpringBoodong", Grade.VIP);
        memberService.join(member);

        Member findMember = memberService.findMember(1L);

        System.out.println("가입한 멤버 이름: " + findMember.getName());
        System.out.println("조회된 멤버 이름: " + member.getName());
        System.out.println("이름 일치 여부: " + member.getName().equals(findMember.getName()));
    }
}

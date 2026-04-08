import java.util.HashMap;
import java.util.Map;

/**
 * 문제 1 정답: 회원 도메인 구현
 */
public class Solution1 {

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
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Grade getGrade() { return grade; }
        public void setGrade(Grade grade) { this.grade = grade; }
    }

    interface MemberService {
        void join(Member member);
        Member findMember(Long memberId);
    }

    // ── 정답 ────────────────────────────────────────────────────────────────

    // TODO 1 정답: MemberRepository 인터페이스
    interface MemberRepository {
        void save(Member member);
        Member findById(Long memberId);
    }

    // TODO 2 정답: MemoryMemberRepository
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

    // TODO 3 정답: MemberServiceImpl (생성자 주입)
    static class MemberServiceImpl implements MemberService {
        private final MemberRepository memberRepository;

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
}

package week04.하성준;

import java.util.HashMap;
import java.util.Map;

/**
 * 문제 1: 회원 도메인 구현 및 테스트
 *
 * 기획팀의 요청에 따라 회원 데이터베이스가 아직 확정되지 않았습니다.
 * 유연한 설계를 위해 인터페이스를 먼저 만들고, 임시로 메모리 저장소를 사용해 봅시다.
 *
 * TODO 1. MemberRepository 인터페이스 작성
 * - void save(Member member)
 * - Member findById(Long memberId)
 *
 * TODO 2. MemoryMemberRepository (MemberRepository 구현체) 작성
 * - HashMap<Long, Member> 를 사용해 메모리에 저장
 * - save, findById 구현
 *
 * TODO 3. MemberServiceImpl (MemberService 구현체) 작성
 * - MemberRepository 를 생성자로 주입받도록 설계 (인터페이스에만 의존!)
 * - join(), findMember() 구현
 *
 * TODO 4. main 메서드 주석을 해제하고 정상적으로 회원이 가입/조회되는지 확인하세요.
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
    public interface MemberRepository {
        void save(Member member);
        Member findById(Long memberId);
    }

    // TODO 2: MemoryMemberRepository 클래스
    public static class MemoryMemberRepository implements MemberRepository {
        private static Map<Long, Member> store = new HashMap<> ();

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
    public static class MemberServiceImpl implements MemberService {
        private final MemberRepository memberRepository;

        MemberServiceImpl (MemberRepository memberRepository) {
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
        // 아래 주석을 해제하고 실행 결과가 맞게 나오는지 확인해 보세요!
        // 회원 이름은 제가 임의로 해둔거로 수정 해주셔야 합니다!

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

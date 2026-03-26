import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * 문제 2: 회원 웹 MVC - 등록 및 조회 컨트롤러 [중간]
 *
 * 아래 MemberForm이 주어져 있다.
 * 다음 세 가지 기능을 하는 MemberController를 완성하시오.
 *
 *  - GET  /members/new  →  회원 가입 폼 페이지("members/createMemberForm") 반환
 *  - POST /members/new  →  이름이 공백이면 폼으로 돌아가고,
 *                          정상이면 회원 저장 후 홈("/")으로 리다이렉트
 *  - GET  /members      →  전체 회원 목록과 총 인원 수를 Model에 담아
 *                          "members/memberList" 반환
 *
 * 힌트: @GetMapping, @PostMapping, Model, redirect:/ 를 활용하세요.
 */
// TODO: 아래 MemberController 클래스를 완성하세요.
public class Problem2 {

    // ──────────────────────────────────────────────────────────────────────
    // 아래 코드는 수정하지 마세요 (지원 클래스 / Supporting classes)
    // ──────────────────────────────────────────────────────────────────────

    static class MemberForm {
        private String name;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    static class Member {
        private Long id;
        private String name;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    static class MemberService {
        private final List<Member> members = new ArrayList<>();
        private long nextId = 1;

        public void join(String name) {
            Member m = new Member();
            m.setId(nextId++);
            m.setName(name);
            members.add(m);
        }

        public List<Member> findMembers() {
            return members;
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    // TODO: 여기에 MemberController 클래스를 작성하세요
    // ──────────────────────────────────────────────────────────────────────

    @Controller
    static class MemberController{
        private final MemberService memberService = new MemberService();

        @GetMapping(value = "/members/new")
        public String get1(){
            return "members/createMemberForm";
        }

        @PostMapping(value = "/members/new")
        public String post1(MemberForm memberForm){
            String name = memberForm.getName();
            if (name.isEmpty()){
                return "members/createMemberForm";
            }

            memberService.join(name);

            return "redirect:/";
        }

        @GetMapping(value = "/members")
        public String get2(Model model){
            List<Member> members = memberService.findMembers();
            model.addAttribute("members", members);
            model.addAttribute("memberCount", members.size());
            return "members/memberList";
        }

    }
}

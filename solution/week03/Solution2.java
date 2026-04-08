import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * 문제 2 정답: 회원 웹 MVC - 등록 및 조회 컨트롤러
 */
public class Solution2 {

    // ──────────────────────────────────────────────────────────────────────
    // 지원 클래스 (Supporting classes)
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
    // 정답: MemberController
    // ──────────────────────────────────────────────────────────────────────

    @Controller
    static class MemberController {

        private final MemberService memberService = new MemberService();

        @GetMapping("/members/new")
        public String createForm() {
            return "members/createMemberForm";
        }

        @PostMapping("/members/new")
        public String create(MemberForm form) {
            if (form.getName() == null || form.getName().isBlank()) {
                return "members/createMemberForm";
            }
            memberService.join(form.getName());
            return "redirect:/";
        }

        @GetMapping("/members")
        public String list(Model model) {
            List<Member> members = memberService.findMembers();
            model.addAttribute("members", members);
            model.addAttribute("count", members.size());
            return "members/memberList";
        }
    }
}

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

    static class MemberForm {
        private Long id;
        private String name;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    static class MemberService {
        private final List<MemberForm> members = new ArrayList<>();
        private long nextId = 1;

        public void join(String name) {
            MemberForm m = new MemberForm();
            m.setId(nextId++);
            m.setName(name);
            members.add(m);
        }

        public List<MemberForm> findMembers() {
            return members;
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    // TODO: 여기에 MemberController 클래스를 작성하세요
    // ──────────────────────────────────────────────────────────────────────

     @Controller
     static class MemberController {

         private final MemberService memberService = new MemberService();

         // TODO 1: GET /members/new
         @GetMapping("/members/new")
         public String form(){
             return "members/createMemberForm";
         }

         // TODO 2: POST /members/new  (공백 이름은 폼으로 돌아가기)
         @PostMapping("/members/new")
         public String signUp(MemberForm member) {
             if (!member.getName().isEmpty()) {
                 memberService.join(member.getName());
             }
             return "redirect:/";
         }

         // TODO 3: GET /members  (members 목록 + 총 count 도 model 에 담기)
         @GetMapping("/members")
         public String list(Model model){
             List<MemberForm> members = memberService.findMembers();
             int cnt = members.size();
             model.addAttribute("count", cnt);
             model.addAttribute("members", members);
             return "members/memberList";
         }
     }
}

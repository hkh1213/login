package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@Slf4j
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final SessionManager sessionManager;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm loginForm){
        return "login/loginForm";
    }

//    @PostMapping("/login")
    public String login(@Validated @ModelAttribute("loginForm") LoginForm loginForm, BindingResult bindingResult
    , HttpServletResponse response){
        if(bindingResult.hasErrors()){
            return "login/loginForm";
        }
        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());
        if (loginMember==null){
            bindingResult.reject("loginFail","아이디 또는 비밀번호가 잘못됐습니다.");
            return "login/loginForm";
        }
        //로그인 성공처리 -> Cookie  생성 후 리스폰스객체에 담아 사용자에게 전달
        Cookie cookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
        response.addCookie(cookie);
        
        return "redirect:/";
    }

//    @PostMapping("/login")
    public String loginV2(@Validated @ModelAttribute("loginForm") LoginForm loginForm, BindingResult bindingResult
        , HttpServletResponse response){
        if(bindingResult.hasErrors()){
            return "login/loginForm";
        }
        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());
        if (loginMember==null){
            bindingResult.reject("loginFail","아이디 또는 비밀번호가 잘못됐습니다.");
            return "login/loginForm";
        }
        //로그인 성공처리
        //세션 관리자를 통해 세션을 생성 및 관리
        sessionManager.createSession(loginMember,response);

        return "redirect:/";
    }
    @PostMapping("/login")
    public String loginV3(@Validated @ModelAttribute("loginForm") LoginForm loginForm, BindingResult bindingResult
        , HttpServletRequest request, @RequestParam(defaultValue = "/",value = "redirectURL") String redirectURL){
        if(bindingResult.hasErrors()){
            return "login/loginForm";
        }

        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());
        if (loginMember==null){
            bindingResult.reject("loginFail","아이디 또는 비밀번호가 잘못됐습니다.");
            return "login/loginForm";
        }
        //로그인 성공처리
        //세션이 있으면 세션 반환, 없으면 신규 세션 반환
        HttpSession session = request.getSession(); //HttpSession 의 getSession(false) -> 세션이 없을 때 신규 세션 반환 X
        //세션에 로그인 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER,loginMember);
        
        return "redirect:"+redirectURL;
    }

//    @PostMapping("/logout")
    public String logout(HttpServletResponse response){
        expireCookie(response);
        return "redirect:/";
    }
//    @PostMapping("/logout")
    public String logoutV2(HttpServletRequest request){
        sessionManager.expire(request);
        return "redirect:/";
    }
    @PostMapping("/logout")
    public String logoutV3(HttpServletRequest request){
        HttpSession session=request.getSession(false);
        if (session!=null){
            session.invalidate();
        }
        return "redirect:/";
    }

    // 쿠키 없애는 메서드
    private void expireCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("memberId", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}

package hello.login.web.session;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@RestController
public class SessionInfoController {

    @GetMapping("session-info")
    public String sessionInfo(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if(session==null)
            return "no session";

        session.getAttributeNames().asIterator()
            .forEachRemaining(n->log.info("session name={},value={}",n,session.getAttribute(n)));

        log.info("sessionId={}",session.getId());
        log.info("getMaxInactiveInterval={}",session.getMaxInactiveInterval());
        log.info("creationTime={}",session.getCreationTime());
        log.info("lasAccessTime={}",session.getLastAccessedTime());
        log.info("isNew={}",session.isNew());

        return "session print";

    }
}

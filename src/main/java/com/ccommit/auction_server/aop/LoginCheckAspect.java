package com.ccommit.auction_server.aop;

import com.ccommit.auction_server.util.SessionUtil;
import com.ccommit.auction_server.exception.UserAccessDeniedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoginCheckAspect {
    private final HttpServletRequest request;
    private static final Logger logger = LogManager.getLogger(LoginCheckAspect.class);

    public LoginCheckAspect(HttpServletRequest request) {
        this.request = request;
    }

    @Around("@annotation(com.ccommit.auction_server.aop.LoginCheck) && @annotation(loginCheck)")
    public Object checkUserLogin(ProceedingJoinPoint joinPoint, LoginCheck loginCheck) throws Throwable {
        // 세션 객체 가져오기
        HttpSession session = request.getSession();

        boolean isPresent = false;
        Long id = null;
        for (int i = 0; i < loginCheck.types().length; i++) {
            switch (loginCheck.types()[i].toString()) {
                case "USER":
                    if (id == null)
                        id = SessionUtil.getUserLoginId(session);
                    break;
                case "UNAUTHORIZED_USER":
                    if (id == null)
                        id = SessionUtil.getUnauthorizedUserLoginId(session);
                    break;
                case "ADMIN":
                    if (id == null)
                        id = SessionUtil.getAdminLoginId(session);
                    break;
                default:
                    break;
            }
        }
        // 값이 없으면 로그인이 필요한 예외를 던짐
        if (id == null) {
            logger.warn("권한 부족");
            throw new UserAccessDeniedException("COMMON_ACCESS_DENIED", "권한 부족");
        }
        Object[] args = joinPoint.getArgs();
        args[0] = id;
        // 값이 있으면 해당 ID 값을 리턴
        return joinPoint.proceed(args);
    }

}
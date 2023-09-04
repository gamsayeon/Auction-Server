package com.example.auction_server.aop;

import com.example.auction_server.enums.UserType;
import com.example.auction_server.exception.LoginRequiredException;
import com.example.auction_server.exception.UserAccessDeniedException;
import com.example.auction_server.util.SessionUtil;
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

    @Around("@annotation(com.example.auction_server.aop.LoginCheck) && @annotation(loginCheck)")
    public Object checkUserLogin(ProceedingJoinPoint joinPoint, LoginCheck loginCheck) throws Throwable {
        // 세션 객체 가져오기
        HttpSession session = request.getSession();

        // 세션에서 값을 읽어옴
        Long id = SessionUtil.getLoginId(session);

        // 값이 없으면 로그인이 필요한 예외를 던짐
        if (id == null) {
            throw new LoginRequiredException("ERR_COMMON_2");
        }

        boolean isPresent = false;
        String loginType = SessionUtil.getLoginUserType(session);
        for (int i = 0; i < loginCheck.types().length; i++) {
            switch (loginCheck.types()[i].toString()) {
                case "USER":
                    if (loginType == UserType.USER.name())
                        isPresent = true;
                    break;
                case "ADMIN":
                    if (loginType == UserType.ADMIN.name())
                        isPresent = true;
                    break;
                case "UNAUTHORIZED_USER":
                    if (loginType == UserType.UNAUTHORIZED_USER.name())
                        isPresent = true;
                    break;
                default:
                    break;
            }
        }
        if (isPresent == false) {
            logger.warn("권한 부족");
            throw new UserAccessDeniedException("ERR_COMMON_3", loginType);
        }
        Object[] args = joinPoint.getArgs();
        args[0] = id;
        // 값이 있으면 해당 ID 값을 리턴
        return joinPoint.proceed(args);
    }

}
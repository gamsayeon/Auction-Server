package com.ccommit.auction_server.util;

import com.ccommit.auction_server.enums.UserType;
import jakarta.servlet.http.HttpSession;

public class SessionUtil {

    private static final String USER = "USER";
    private static final String ADMIN = "ADMIN";
    private static final String UNAUTHORIZED_USER = "UNAUTHORIZED_USER";


    private SessionUtil() {
    }

    public static void setLoginSession(HttpSession session, Long id, UserType userType) {
        switch (userType) {
            case USER:
                session.setAttribute(USER, id);
                break;
            case UNAUTHORIZED_USER:
                session.setAttribute(UNAUTHORIZED_USER, id);
                break;
            case ADMIN:
                session.setAttribute(ADMIN, id);
                break;
            default:
                break;
        }
    }

    public static Long getUserLoginId(HttpSession session) {
        Long id = (Long) session.getAttribute(USER);
        return id;
    }

    public static Long getUnauthorizedUserLoginId(HttpSession session) {
        Long id = (Long) session.getAttribute(UNAUTHORIZED_USER);
        return id;
    }

    public static Long getAdminLoginId(HttpSession session) {
        Long id = (Long) session.getAttribute(ADMIN);
        return id;
    }

    public static void clear(HttpSession session) {
        session.invalidate();
    }
}
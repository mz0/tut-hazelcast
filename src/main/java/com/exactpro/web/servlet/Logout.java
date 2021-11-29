package com.exactpro.web.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.exactpro.web.servlet.AuthFilter.LOGIN_NAME_KEY;

@WebServlet(urlPatterns = {"/logout"})
public class Logout extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(Logout.class);

    @Override
    public void service(ServletRequest sRx, ServletResponse sTx) {
        HttpSession session = ((HttpServletRequest) sRx).getSession(false);
        if (session == null) {
            log.warn("Logout attempt with null session");
        } else {
            String loginNameAttr = (String) session.getAttribute(LOGIN_NAME_KEY);
            log.trace("Logout user '{}", loginNameAttr);
            if (loginNameAttr == null) {
                log.warn("Logout attempt with null login-name attribute");
            } else {
                session.removeAttribute(LOGIN_NAME_KEY);
                try {
                    ((HttpServletResponse) sTx).sendRedirect(((HttpServletRequest) sRx).getContextPath() + "/");
                } catch (IOException e) {
                    log.warn("post-logout redirect failed");
                }
            }
        }
    }
}

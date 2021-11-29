package com.exactpro.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import static com.exactpro.web.servlet.AuthFilter.LOGIN_NAME_KEY;

@ManagedBean(name = "auth")
@ViewScoped
public class Auth {
    private static final Logger log = LoggerFactory.getLogger(Auth.class);
    @ManagedProperty(value = "#{userInfo}")
    User userInfo;

    public void tryLogin() {
        log.info("login usr {} with password {}", userInfo.getName(), userInfo.getPassword());
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        HttpSession session = (HttpSession) ctx.getSession(false);
        if (session == null) {
            log.error("No (null) session found");
        } else if (!userInfo.getName().isEmpty() /* TODO real check */){
            session.setAttribute(LOGIN_NAME_KEY, userInfo.getName());
            HttpServletRequest req = (HttpServletRequest) ctx.getRequest();
            req.changeSessionId();
            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            try {
                context.redirect(context.getRequestContextPath() + "/home.jsp");
            } catch (IOException e) {
                log.error("Redirect error", e);
            }
        }
    }

    public void setUserInfo(User uInfo) {
        userInfo = uInfo;
    }

    public User getUserInfo() {
        return userInfo;
    }
}

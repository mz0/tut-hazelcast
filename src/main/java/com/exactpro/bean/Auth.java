package com.exactpro.bean;

import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

import static com.exactpro.web.AuthClusterBuilder.CLUSTER_MAPPER_KEY;
import static com.exactpro.web.servlet.AuthFilter.LOGIN_NAME_KEY;

@ManagedBean(name = "auth")
@ViewScoped
public class Auth {
    private static final Logger log = LoggerFactory.getLogger(Auth.class);
    @ManagedProperty(value = "#{userInfo}")
    LoginFormFields loginFormFields;

    public void tryLogin() {
        log.info("user '{}' logs in", loginFormFields.getName());
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        HttpSession session = (HttpSession) ctx.getSession(false);
        try {
            if (session == null) {
                log.error("No (null) session found");
                ((HttpServletResponse) ctx.getResponse()).sendError(403, "Authorisation failed");
            } else if (isLoggedIn((ServletContext) ctx.getContext(), session.getId())) {
                flagAuthOK(session, loginFormFields.getName());
                HttpServletRequest req = (HttpServletRequest) ctx.getRequest();
                req.changeSessionId();
                ctx.redirect(ctx.getRequestContextPath() + "/home.jsp");
            }
        } catch (IOException e) {
            log.error("Response or Redirect error", e);
        }
    }

    private boolean isLoggedIn(ServletContext ctx, String sessionId) {
        Map<Object, Object> alreadyLoggedIn =
            ((HazelcastInstance) ctx.getAttribute(CLUSTER_MAPPER_KEY)).getMap(sessionId);
        String alreadyLoggedInAs = (String) alreadyLoggedIn.get(LOGIN_NAME_KEY);
        if (alreadyLoggedInAs != null) {

        } else {

        }
        log.info((alreadyLoggedInAs == null ? "cluster user with session id {} not found" : "User already logged in as {}"), loginFormFields.getName());
        return !loginFormFields.getName().isEmpty(); /* TODO real check */
    }

    /**
     * Let filter know this login was authenticated<br>
     * by putting the necessary details into this client's Session
     * @param session this client's Session to mark as authenticated
     * @param loginName authenticated as this user
     */
    private void flagAuthOK(HttpSession session, String loginName) {
        session.setAttribute(LOGIN_NAME_KEY, loginName);
    }

    public void setLoginFormFields(LoginFormFields uInfo) {
        loginFormFields = uInfo;
    }

    public LoginFormFields getLoginFormFields() {
        return loginFormFields;
    }
}

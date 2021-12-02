package com.exactpro.bean;

import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import static com.exactpro.web.AuthClusterBuilder.CLUSTER_MAPPER_KEY;
import static com.exactpro.web.AuthClusterBuilder.JNDI_DS_RESOURCE_NAME;
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
			try {
				DataSource authDS = (DataSource) (new InitialContext()).lookup(JNDI_DS_RESOURCE_NAME);
				if (checkUser(authDS, loginFormFields.getName(), loginFormFields.getPassword())) {
					// todo cluster login
					return true;
				}
			} catch (NamingException n) {
				log.error("Naming exception shoul never happen in {}", Auth.class.getName());
			}
        }
        log.info((alreadyLoggedInAs == null ? "cluster user with session id {} not found" : "User already logged in as {}"), loginFormFields.getName());
        return !loginFormFields.getName().isEmpty(); /* TODO real check */
    }

	private boolean checkUser(DataSource authDS, String name, String password) {
		/* see user.sql */
		String query = "SELECT salt, password FROM user WHERE login = ?";
		try (Connection conn = authDS.getConnection(); PreparedStatement tmt = conn.prepareStatement(query)) {
			return true;
		} catch (SQLException e) {
			log.error("Cannot authenticate user '{}'", name, e);
		}
		return false;
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

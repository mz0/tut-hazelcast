package com.exactpro.bean;

import com.exactpro.compat.PasswordHash;
import com.exactpro.web.User;
import com.exactpro.web.util.SavedRequest;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import static com.exactpro.web.AuthClusterBuilder.CLUSTER_MAPPER_KEY;
import static com.exactpro.web.AuthClusterBuilder.JNDI_DS_RESOURCE_NAME;
import static com.exactpro.web.servlet.AuthFilter.LOGIN_NAME_KEY;
import static com.exactpro.web.servlet.AuthFilter.SAVED_REQUEST_KEY;

@ManagedBean(name = "auth")
@ViewScoped
public class Auth {
	private static final Logger log = LoggerFactory.getLogger(Auth.class);

	private final ExternalContext fCtx;
	private final HttpSession session;
	private final ServletContext ctx;
	private final HttpServletRequest req;

	public Auth() {
		fCtx = FacesContext.getCurrentInstance().getExternalContext();
		ctx = (ServletContext) fCtx.getContext();
		session = (HttpSession) fCtx.getSession(false);
		req = (HttpServletRequest) fCtx.getRequest();
	}

	@ManagedProperty(value = "#{userInfo}")
	LoginFormFields loginFormFields;

	public void tryLogin() {
		log.info("user '{}' logs in", loginFormFields.getName());
		try {
			if (session == null) {
				log.error("No (null) session found");
				((HttpServletResponse) fCtx.getResponse()).sendError(403, "Authorisation failed");
			} else if (isAuthOk()) {
				flagAuthOK(session, loginFormFields.getName());
				fCtx.redirect(fCtx.getRequestContextPath() + redirectUrl(session));
			}
		} catch (IOException e) {
			log.error("Response or Redirect error", e);
		}
	}

	private String redirectUrl(HttpSession session) {
		SavedRequest sav = (SavedRequest) session.getAttribute(SAVED_REQUEST_KEY);
		return (sav != null && !Objects.equals(sav.getMethod(), "POST"))
			? sav.getRequestUrl()
			: "/home.jsp";
	}

	private boolean isAuthOk() {
		try {
			DataSource authDS = (DataSource) (new InitialContext()).lookup(JNDI_DS_RESOURCE_NAME);
			String checkResult = checkPasswordError(authDS, loginFormFields.getName(), loginFormFields.getPassword());
			switch (checkResult) {
				case "":
					String newId = req.changeSessionId();
					log.trace("Changing sessionID to {}", newId);
					clusterLogin(loginFormFields.getName(), session.getId());
					return true;
				default:
					// TODO push checkResult to Client's login form
					log.error("{} login error: {}", loginFormFields.getName(), checkResult);
					return false;
			}
		} catch (NamingException n) {
			throw new RuntimeException("'" + JNDI_DS_RESOURCE_NAME + "' DataSource lookup failed", n);
		}
	}

	private void clusterLogin(String name, String sessionId) {
		((HazelcastInstance) ctx.getAttribute(CLUSTER_MAPPER_KEY)).getMap(sessionId).set(LOGIN_NAME_KEY, name);
		log.info("{} login Ok", name);
	}

	/**
	 * Returns empty error string on OK check, or a simple error message.
	 *
	 * @param authDS   the DataSource for the DB with {@code user} table
	 * @param name     login-id to check
	 * @param password check this against the hash in DB
	 * @return "" if OK, "Error" if user not found,<br>
	 * or password hash is no match, "DB Error" when no check can't be performed.
	 */
	private String checkPasswordError(DataSource authDS, String name, String password) {
		/* see user.sql */
		String query = "SELECT login, salt, password FROM user WHERE login = ?";
		try (Connection conn = authDS.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, name);
			try (ResultSet resultSet = stmt.executeQuery()) {
				if (resultSet.next()) {
					User user = userFromRow(resultSet);
					log.trace("Checking hash for name '{}' with salt '{}'", name, user.getSalt());
					return PasswordHash.getShshaHash(password, user.getSalt()).equals(user.getPassword())
						? "" : "Error";
				} else {
					return "Error";
				}
			}
		} catch (SQLException e) {
			log.error("Cannot authenticate user '{}'", name, e);
			return "DB error";
		}
	}

	private User userFromRow(ResultSet resultSet) throws SQLException {
		User user = new User();
		user.setLogin(resultSet.getString("login"));
		user.setPassword(resultSet.getString("password"));
		user.setSalt(resultSet.getString("salt"));
		return user;
	}

	/**
	 * Let filter know this login was authenticated<br>
	 * by putting the necessary details into this client's Session
	 *
	 * @param session   this client's Session to mark as authenticated
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

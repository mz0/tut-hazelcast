package com.exactpro.web.servlet;

import com.exactpro.web.util.SavedRequest;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import static com.exactpro.web.AuthClusterBuilder.CLUSTER_MAPPER_KEY;
import static com.exactpro.web.AuthClusterBuilder.JNDI_DS_RESOURCE_NAME;

public class AuthFilter implements Filter {
	private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);

	public static final String TOKEN_KEY = "SSO-ID";
	public static final String LOGIN_NAME_KEY = "name";
	public static final String SAVED_REQUEST_KEY = "redirect.SavedRequest";


	protected String loginUrl;
	private String contextLoginUrl;
	private String ignorePrefix;
	private Pattern ignorePattern = Pattern.compile("^/javax.faces.resource/.*|^/resources/.*|^/api/.*|^/favicon.ico");

	private void traceSessionId(HttpServletRequest req) {
		if (req.getSession(false) == null) {
			log.trace("Session is null");
		} else {
			log.trace("SessionID: {}", req.getSession(false).getId());
		}
	}

	@Override
	public void doFilter(ServletRequest rxs, ServletResponse txs, FilterChain fc) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) rxs;
		//traceSessionId(req);
		if (isIgnored(req.getRequestURI()) || isAuthenticated(req) || isLogin(req)) {
			if (log.isTraceEnabled()) {
				log.trace("Passing request: " + req.getRequestURI());
			}
		} else {
			HttpSession session = req.getSession(true); // make a new one if not found
			if (log.isTraceEnabled()) {
				log.trace("{} needs authentication", req.getRequestURI());
			}
			checkClusterLoginOrRedirect(session, req, (HttpServletResponse) txs);
		}
		fc.doFilter(rxs, txs); // pass the request along the filter chain
	}

	private boolean isIgnored(String uri) {
		return ignorePattern.matcher(uri).find();
	}

	private boolean isLogin(HttpServletRequest req) {
		return req.getRequestURI().startsWith(contextLoginUrl);
	}

	private boolean isAuthenticated(HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		return session != null && session.getAttribute(LOGIN_NAME_KEY) != null;
	}

	private void checkClusterLoginOrRedirect(HttpSession session, HttpServletRequest request, HttpServletResponse tx) {
		if (clusterLoginOk(maybeToken(request), request.getServletContext(), session)) {
			return;
		}
		session.setAttribute(SAVED_REQUEST_KEY, new SavedRequest(request));
		tx.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		tx.setHeader("Location", tx.encodeRedirectURL(contextLoginUrl));
	}

	private boolean clusterLoginOk(Optional<String> ssoId, ServletContext ctx, HttpSession session) {
		if (!ssoId.isPresent()) {
			log.debug("Cookie {} not found", TOKEN_KEY);
			return false;
		}
		log.trace("Cookie {} = {}", TOKEN_KEY, ssoId.get());
		Map<Object, Object> alreadyLoggedIn =
			((HazelcastInstance) ctx.getAttribute(CLUSTER_MAPPER_KEY)).getMap(ssoId.get());
		String alreadyLoggedInAs = (String) alreadyLoggedIn.get(LOGIN_NAME_KEY);
		if (alreadyLoggedInAs == null) {
			log.warn("No '{}' attribute found for ssoID {}", LOGIN_NAME_KEY, ssoId);
		} else {
			flagAuthOK(session, alreadyLoggedInAs, ssoId.get());
		}
		return alreadyLoggedInAs != null;
	}

	@Override
	public void init(FilterConfig fConfig) throws ServletException {
		log.info("AuthFilter init start");
		ignorePrefix = nonEmpty(fConfig.getInitParameter("ignore-url"));
		String contextPath = fConfig.getServletContext().getContextPath();
		log.info("AuthFilter context path: '{}'", contextPath);
		loginUrl = fConfig.getServletContext().getInitParameter("login-url");
		if (loginUrl == null) {
			throw new ServletException("login-url parameter missing in web.xml");
			// all requests will fail with HTTP ERROR 503 Service Unavailable
		}
		log.info("Login URL: {}", loginUrl);
		log.info("Ignore URL(s): {}", listIgnored());
		contextLoginUrl = String.format("%s/%s", contextPath, loginUrl).replace("//", "/");
		checkDB();
	}


	private void checkDB() throws ServletException {
		try {
			checkDbConnection((DataSource) (new InitialContext()).lookup(JNDI_DS_RESOURCE_NAME));
		} catch (NamingException n) {
			throw new ServletException("cannot resolve JNDI Resource " + JNDI_DS_RESOURCE_NAME, n);
		} catch (SQLException e) {
			throw new ServletException("Database connection error", e);
		}
	}

	private void checkDbConnection(DataSource ds) throws SQLException {
		try (Connection conn = ds.getConnection(); Statement stmt = conn.createStatement()) {
			stmt.execute("SELECT 1");
		}
	}

	private String nonEmpty(String s) {
		return (s == null || s.isEmpty() || s.equals("/"))
			? java.util.UUID.randomUUID().toString()
			: s;
	}

	private Optional<String> maybeToken(HttpServletRequest request) {
		return readCookie(request, TOKEN_KEY);
	}

	private static Optional<String> readCookie(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie c : cookies) {
				if (c.getName().equals(cookieName)) {
					return Optional.of(c.getValue());
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * Let filter know this login was authenticated<br>
	 * by putting the necessary details into this client's Session
	 *  @param session this client's Session to mark as authenticated
	 * @param loginName authenticated as this user
	 * @param ssoId the key to shared Map Entry
	 */
	public static void flagAuthOK(HttpSession session, String loginName, String ssoId) {
		session.setAttribute(LOGIN_NAME_KEY, loginName);
		session.setAttribute(TOKEN_KEY, ssoId);
	}

	private String listIgnored() {
		return "[" + ignorePrefix + "]";
	}
}

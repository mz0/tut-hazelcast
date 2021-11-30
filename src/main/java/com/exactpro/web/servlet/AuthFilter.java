package com.exactpro.web.servlet;

import com.exactpro.web.util.SavedRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AuthFilter implements Filter {
	private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);

	public static final String LOGIN_NAME_KEY = "name";
	public static final String SAVED_REQUEST_KEY = "redirect.SavedRequest";

	protected FilterConfig filterConfig;
	protected String loginUrl;
	private String contextLoginUrl;
	private String ignorePrefix;

	@Override
	public void doFilter(ServletRequest rxs, ServletResponse txs, FilterChain fc) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) rxs;
		if (isIgnored(req.getRequestURI()) || isAuthenticated(req) || isLogin(req)) {
			if (log.isTraceEnabled()) {
				log.trace("Passing request: " + req.getRequestURI());
			}
			fc.doFilter(rxs, txs); // pass the request along the filter chain
		} else {
			HttpSession session = req.getSession(true); // make a new one if not found
			if (log.isTraceEnabled()) {
				log.trace("{} needs authentication", req.getRequestURI());
			}
			saveRequestAndRedirect(session, req, (HttpServletResponse) txs);
		}
	}

	private boolean isIgnored(String uri) {
		return uri.startsWith(ignorePrefix);
	}

	private boolean isLogin(HttpServletRequest req) {
		return req.getRequestURI().startsWith(contextLoginUrl);
	}

	private boolean isAuthenticated(HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		return session != null && session.getAttribute(LOGIN_NAME_KEY) != null;
	}

	private void saveRequestAndRedirect(HttpSession session, HttpServletRequest request, HttpServletResponse tx) {
		session.setAttribute(SAVED_REQUEST_KEY, new SavedRequest(request));
		tx.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		tx.setHeader("Location", tx.encodeRedirectURL(contextLoginUrl));
	}

	@Override
	public void init(FilterConfig fConfig) throws ServletException {
		log.info("AuthFilter init start");
		this.filterConfig = fConfig;
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
	}

	private String nonEmpty(String s) {
		return (s == null || s.isEmpty() || s.equals("/"))
			? java.util.UUID.randomUUID().toString()
			: s;
	}

	private String listIgnored() {
		return "[" + ignorePrefix + "]";
	}
}

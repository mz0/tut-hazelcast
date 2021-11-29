package com.exactpro.web.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AuthFilter implements Filter {
    public static final String LOGIN_NAME_KEY = "name";
	private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);
	protected FilterConfig filterConfig;
	protected String loginUrl;
	private String ignorePrefix;

	@Override
	public void doFilter(ServletRequest rxs, ServletResponse txs, FilterChain fc) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) rxs;

		if (!ignorePrefix.isEmpty() && !req.getRequestURI().startsWith(ignorePrefix)) {
			HttpSession session = req.getSession(false);
			if (log.isTraceEnabled()) {
				log.trace("Session " + (session == null ? "== null" : "found"));
			}

			if (session == null) { // this is the first (non-API) request from that client

			}
		} else {
			if (log.isTraceEnabled()) {
				log.trace("Passing API call: " + req.getRequestURI());
			}
		}
		// pass the request along the filter chain
		fc.doFilter(rxs, txs);
	}

	@Override
	public void init(FilterConfig fConfig) throws ServletException {
		this.filterConfig = fConfig;
		ignorePrefix = fConfig.getInitParameter("ignore-url");
		loginUrl = fConfig.getServletContext().getInitParameter("login-url");
		if (loginUrl == null) {
			throw new ServletException("login-url parameter missing in web.xml");
			// all requests will fail with HTTP ERROR 503 Service Unavailable
		}
	}

	@Override
	public void destroy() {}
}

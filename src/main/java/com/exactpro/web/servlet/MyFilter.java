package com.exactpro.web.servlet;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class MyFilter implements Filter {
	protected FilterConfig filterConfig;
	protected String loginUrl;

	@Override
	public void doFilter(ServletRequest rxs, ServletResponse txs, FilterChain fc) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) rxs;
		String ipAddress = req.getRemoteAddr();
		// pass the request along the filter chain
		fc.doFilter(rxs, txs);
	}

	@Override
	public void init(FilterConfig fConfig) throws ServletException {
		this.filterConfig = fConfig;
		loginUrl = fConfig.getServletContext().getInitParameter("login-url");
		if (loginUrl == null) {
			throw new ServletException("login-url parameter missing for " + this.getClass() + " in web.xml");
		}
	}

	@Override
	public void destroy() {}
}

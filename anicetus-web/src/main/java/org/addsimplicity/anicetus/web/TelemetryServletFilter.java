/**
 * Copyright 2008-2009 Dan Pritchett
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 */
package org.addsimplicity.anicetus.web;

import java.io.IOException;
import java.util.Enumeration;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author Dan Pritchett
 * 
 */
public class TelemetryServletFilter implements Filter {
	private static final String s_PARENT_NAME = "x-anicetus-parent-guid";

	private ServletContext m_servletContext;
	private String m_sessionContextName;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 * javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filter) throws IOException,
			ServletException {
		ApplicationContext curCtx = WebApplicationContextUtils.getWebApplicationContext(m_servletContext);

		TelemetryHttpContext curHttpCtx = (TelemetryHttpContext) curCtx.getBean(m_sessionContextName);
		TelemetryHttpSession curSess = (TelemetryHttpSession) curHttpCtx.getSession();
		setRequestOnSession(curSess, (HttpServletRequest) request);
		TelemetryProxyResponse proxyResp = new TelemetryProxyResponse((HttpServletResponse) response, curSess);

		filter.doFilter(request, proxyResp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig config) throws ServletException {
		m_servletContext = config.getServletContext();
		m_sessionContextName = config.getInitParameter("SessionContextName");
		if (m_sessionContextName == null) {
			m_sessionContextName = "sessionContext";
		}
	}

	@SuppressWarnings("unchecked")
	private void setRequestOnSession(TelemetryHttpSession session, HttpServletRequest request) {
		session.setMethod(request.getMethod());
		session.setProtocol(request.getProtocol());
		if (request.getContentType() != null) {
			session.setContentType(request.getContentType(), HeaderType.Request);
		}
		session.setRequestURL(request.getRequestURI());

		Enumeration<String> pnames = request.getParameterNames();
		while (pnames.hasMoreElements()) {
			String name = pnames.nextElement();
			session.setParameter(name, request.getParameter(name));
		}

		Enumeration<String> hnames = request.getHeaderNames();
		while (hnames.hasMoreElements()) {
			String name = hnames.nextElement();
			String value = request.getHeader(name);
			if (value != null) {
				session.setHeader(name, request.getHeader(name), HeaderType.Request);
			}
		}

		String parent = request.getHeader(s_PARENT_NAME);
		if (parent == null) {
			parent = request.getParameter(s_PARENT_NAME);
		}

		if (parent != null) {
			try {
				UUID parentId = UUID.fromString(parent);
				session.setParentId(parentId);
			}
			catch (IllegalArgumentException iae) {
				// TODO - Exception handler
			}

		}
	}

}

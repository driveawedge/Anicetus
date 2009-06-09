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
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.addsimplicity.anicetus.entity.CompletionStatus;

/**
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * 
 */
class TelemetryProxyResponse extends HttpServletResponseWrapper {
	/**
	 * @author Dan Pritchett
	 * 
	 */
	class ProxyOutputStream extends ServletOutputStream {
		private final ServletOutputStream m_parent;
		private long m_sent;

		ProxyOutputStream(ServletOutputStream parent) {
			super();
			m_parent = parent;
			m_sent = 0;
		}

		@Override
		public void close() throws IOException {
			super.close();
			m_session.setResponseSize(m_sent);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.io.OutputStream#write(int)
		 */
		@Override
		public void write(int b) throws IOException {
			m_parent.write(b);
			m_sent += 1;
		}

	}

	private final TelemetryHttpSession m_session;
	private ProxyOutputStream m_output;

	TelemetryProxyResponse(HttpServletResponse parentResponse, TelemetryHttpSession session) {
		super(parentResponse);
		m_session = session;
	}

	@Override
	public void addDateHeader(String name, long date) {
		super.addDateHeader(name, date);
		m_session.addHeader(name, Long.toString(date), HeaderType.Response);
	}

	@Override
	public void addHeader(String name, String value) {
		super.addHeader(name, value);
		m_session.addHeader(name, value, HeaderType.Response);
	}

	@Override
	public void addIntHeader(String name, int value) {
		super.addIntHeader(name, value);
		m_session.addHeader(name, Integer.toString(value), HeaderType.Response);
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (m_output == null) {
			m_output = new ProxyOutputStream(super.getOutputStream());
		}

		return m_output;
	}

	@Override
	public void sendError(int code) throws IOException {
		super.sendError(code);
		applyStatus(code, null);
	}

	@Override
	public void sendError(int code, String status) throws IOException {
		super.sendError(code, status);
		applyStatus(code, status);
	}

	@Override
	public void sendRedirect(String url) throws IOException {
		super.sendRedirect(url);
		applyStatus(302, null);
		m_session.setRedirectURL(url);
	}

	@Override
	public void setCharacterEncoding(String encoding) {
		super.setCharacterEncoding(encoding);
		m_session.setCharacterEncoding(encoding);
	}

	@Override
	public void setContentLength(int length) {
		super.setContentLength(length);
		m_session.setResponseSize(length);
	}

	@Override
	public void setContentType(String type) {
		super.setContentType(type);
		m_session.setContentType(type, HeaderType.Response);
	}

	@Override
	public void setDateHeader(String name, long date) {
		super.setDateHeader(name, date);
		m_session.setHeader(name, Long.toString(date), HeaderType.Response);
	}

	@Override
	public void setHeader(String name, String value) {
		super.setHeader(name, value);
		m_session.setHeader(name, value, HeaderType.Response);
	}

	@Override
	public void setIntHeader(String name, int value) {
		super.setIntHeader(name, value);
		m_session.setHeader(name, Integer.toString(value), HeaderType.Response);
	}

	@Override
	public void setLocale(Locale locale) {
		super.setLocale(locale);
		m_session.setLocale(locale.toString());
	}

	@Override
	public void setStatus(int code) {
		super.setStatus(code);
		applyStatus(code, null);
	}

	@Override
	public void setStatus(int code, String status) {
		super.setStatus(code, status);
		applyStatus(code, status);
	}

	private void applyStatus(int code, String message) {
		if (code >= 400) {
			m_session.setStatus(CompletionStatus.Failure);
		}
		else {
			m_session.setStatus(CompletionStatus.Success);
		}
		m_session.setHttpStatusCode(code);

		if (message != null) {
			m_session.setHttpStatusMessage(message);
		}
	}
}

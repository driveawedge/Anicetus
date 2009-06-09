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

import java.util.Collections;
import java.util.Set;

import org.addsimplicity.anicetus.entity.EntityTypeRegistry;
import org.addsimplicity.anicetus.entity.TelemetrySession;

/**
 * The HTTP session extends the standard telemetry session to add support for
 * HTTP specific semantics. HTTP adds support for the following extensions:
 * 
 * Headers - HTTP request and response headers with the specific headers
 * controlled by include/exclude list.
 * 
 * Parameters - HTTP request parameters with the specific parameters controlled
 * by include/exclude list.
 * 
 * Protocol/Method - The additional request information.
 * 
 * Response Infomration - Status code, content type, response size, and redirect
 * URL if sent.
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * 
 */
public class TelemetryHttpSession extends TelemetrySession {
	static {
		EntityTypeRegistry.addSearchPackage(TelemetryHttpSession.class.getPackage().getName());
	}

	private final String s_HEADER_PREFIX = "HTTP";
	private final String s_PARAM_PREFIX = "PARAM";
	private final String s_DELIM = ".";

	private Set<String> m_includeHeaders;
	private Set<String> m_excludeHeaders = Collections.emptySet();
	private Set<String> m_includeParameters;
	private Set<String> m_excludeParameters = Collections.emptySet();

	public TelemetryHttpSession() {
		super();
	}

	public void addHeader(String name, String value, HeaderType type) {
		if (!includeHeader(name)) {
			return;
		}

		String hname = makeHeaderName(name, type);
		if (containsKey(hname)) {
			Object mystery = get(hname);
			if (mystery instanceof String[]) {
				String[] list = (String[]) mystery;
				String[] next = new String[list.length + 1];
				System.arraycopy(list, 0, next, 0, list.length);
				next[next.length - 1] = value;
				put(hname, next);
			}
			else {
				String[] next = new String[2];
				next[0] = (String) mystery;
				next[1] = value;
				put(hname, next);
			}

		}
		else {
			setHeader(name, value, type);
		}
	}

	public String getCharacterEncoding() {
		return (String) get(TelemetryHttpSessionFields.CharacterEncoding.name());
	}

	public String getContentType(HeaderType direction) {
		return (String) get(makeHeaderName("Content-Type", direction));
	}

	public Set<String> getExcludeHeaders() {
		return m_excludeHeaders;
	}

	public Set<String> getExcludeParameters() {
		return m_excludeParameters;
	}

	public String getHeader(String name, HeaderType type) {
		String hname = makeHeaderName(name, type);

		Object mystery = get(hname);
		String result = null;

		if (mystery instanceof String) {
			result = (String) mystery;
		}
		else if (mystery instanceof String[]) {
			StringBuffer sb = new StringBuffer();
			for (String s : (String[]) mystery) {
				if (sb.length() > 0) {
					sb.append(",");
				}
				sb.append(s);
			}

			result = sb.toString();
		}

		return result;
	}

	public String[] getHeaderValues(String name, HeaderType type) {
		String hname = makeHeaderName(name, type);

		Object mystery = get(hname);
		String[] result = null;

		if (mystery instanceof String) {
			result = new String[1];
			result[0] = (String) mystery;
		}
		else if (mystery instanceof String[]) {
			result = (String[]) mystery;
		}

		return result;
	}

	public int getHttpStatusCode() {
		return (Integer) get(TelemetryHttpSessionFields.HttpStatusCode.name());
	}

	public String getHttpStatusMessage() {
		return (String) get(TelemetryHttpSessionFields.HttpStatusMessage);
	}

	public Set<String> getIncludeHeaders() {
		return m_includeHeaders;
	}

	public Set<String> getIncludeParameters() {
		return m_includeParameters;
	}

	public String getLocale() {
		return (String) get(TelemetryHttpSessionFields.Locale.name());
	}

	public String getMethod() {
		return (String) get(TelemetryHttpSessionFields.Method.name());
	}

	public String getParameter(String name) {
		return (String) get(makeParamName(name));
	}

	public String getProtocol() {
		return (String) get(TelemetryHttpSessionFields.Protocol.name());
	}

	public String getRedirectURL() {
		return (String) get(TelemetryHttpSessionFields.RedirectURL.name());
	}

	public String getRequestURL() {
		return (String) get(TelemetryHttpSessionFields.RequestURL.name());
	}

	public long getResponseSize() {
		return (Long) get(makeHeaderName("Content-Length", HeaderType.Response));
	}

	public void setCharacterEncoding(String encoding) {
		put(TelemetryHttpSessionFields.CharacterEncoding.name(), encoding);
	}

	public void setContentType(String type, HeaderType direction) {
		put(makeHeaderName("Content-Type", direction), type);
	}

	public void setExcludeHeaders(Set<String> excludeHeaders) {
		m_excludeHeaders = excludeHeaders;
	}

	public void setExcludeParameters(Set<String> excludeParameters) {
		m_excludeParameters = excludeParameters;
	}

	public void setHeader(String name, String value, HeaderType type) {
		if (!includeHeader(name)) {
			return;
		}

		put(makeHeaderName(name, type), value);
	}

	public void setHttpStatusCode(int code) {
		put(TelemetryHttpSessionFields.HttpStatusCode.name(), code);
	}

	public void setHttpStatusMessage(String message) {
		put(TelemetryHttpSessionFields.HttpStatusMessage.name(), message);
	}

	public void setIncludeHeaders(Set<String> includeHeaders) {
		m_includeHeaders = includeHeaders;
		if (m_includeHeaders != null && m_includeHeaders.size() == 0) {
			m_includeHeaders = null;
		}
	}

	public void setIncludeParameters(Set<String> includeParameters) {
		m_includeParameters = includeParameters;
		if (m_includeParameters != null && m_includeParameters.size() == 0) {
			m_includeParameters = null;
		}
	}

	public void setLocale(String locale) {
		put(TelemetryHttpSessionFields.Locale.name(), locale);
	}

	public void setMethod(String method) {
		put(TelemetryHttpSessionFields.Method.name(), method);
	}

	public void setParameter(String name, String value) {
		if (!includeParam(name)) {
			return;
		}

		put(makeParamName(name), value);
	}

	public void setProtocol(String protocol) {
		put(TelemetryHttpSessionFields.Protocol.name(), protocol);
	}

	public void setRedirectURL(String url) {
		put(TelemetryHttpSessionFields.RedirectURL.name(), url);
	}

	public void setRequestURL(String url) {
		put(TelemetryHttpSessionFields.RequestURL.name(), url);
	}

	public void setResponseSize(long size) {
		put(makeHeaderName("Content-Length", HeaderType.Response), size);
	}

	private boolean includeField(String name, Set<String> includes, Set<String> excludes) {
		if (excludes.contains(name.toUpperCase())) {
			return false;
		}

		if (includes != null) {
			return includes.contains(name);
		}

		return true;
	}

	private boolean includeHeader(String name) {
		return includeField(name, m_includeHeaders, m_excludeHeaders);
	}

	private boolean includeParam(String name) {
		return includeField(name, m_includeParameters, m_excludeParameters);
	}

	private String makeHeaderName(String name, HeaderType type) {
		StringBuffer sb = new StringBuffer(s_HEADER_PREFIX);
		sb.append(s_DELIM);
		sb.append(type.name());
		sb.append(name);

		return sb.toString();
	}

	private String makeParamName(String name) {
		StringBuffer sb = new StringBuffer(s_PARAM_PREFIX);
		sb.append(s_DELIM);
		sb.append(name);

		return sb.toString();
	}
}

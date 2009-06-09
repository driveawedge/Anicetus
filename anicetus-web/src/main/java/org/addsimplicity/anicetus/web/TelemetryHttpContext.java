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

import org.addsimplicity.anicetus.TelemetryContext;
import org.addsimplicity.anicetus.entity.TelemetrySession;

/**
 * The http context extends the telemetry context to create telemetry session
 * that is specific to the HTTP protocol. The HTTP protocol has both headers and
 * parameters that are logged based on white list and black list definitions.
 * This factory allows those to be set globally and propogated to each session
 * created. The way those are applied are defined on the http session object.
 * 
 * This object should be created at the start of every request and disposed at
 * the end of each request. This is handled automatically by using the Spring
 * Web context life cycle manager. The scope of this bean should be set to
 * "request" to get the desired effect.
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * 
 */
public class TelemetryHttpContext extends TelemetryContext {
	private Set<String> m_includeHeaders;
	private Set<String> m_excludeHeaders = Collections.emptySet();
	private Set<String> m_includeParameters;
	private Set<String> m_excludeParameters = Collections.emptySet();

	/**
	 * Construct the http context object.
	 */
	public TelemetryHttpContext() {
		super();
	}

	/**
	 * Get the global exclude headers in effect.
	 * 
	 * @return the global exclude headers list.
	 * @see org.addsimplicity.anicetus.web.TelemetryHttpSession#getExcludeHeaders()
	 */
	public Set<String> getExcludeHeaders() {
		return m_excludeHeaders;
	}

	/**
	 * Get the global exclude parameters in effect.
	 * 
	 * @return the global exclude parameters list.
	 * @see org.addsimplicity.anicetus.web.TelemetryHttpSession#getExcludeParameters()
	 */
	public Set<String> getExcludeParameters() {
		return m_excludeParameters;
	}

	/**
	 * Get the global include headers in effect.
	 * 
	 * @return the global header list.
	 * @see org.addsimplicity.anicetus.web.TelemetryHttpSession#getIncludeHeaders()
	 */
	public Set<String> getIncludeHeaders() {
		return m_includeHeaders;
	}

	/**
	 * Get the global include parameters in effect.
	 * 
	 * @return the global parameter list.
	 * @see org.addsimplicity.anicetus.web.TelemetryHttpSession#getIncludeParameters()
	 */
	public Set<String> getIncludeParameters() {
		return m_includeParameters;
	}

	/**
	 * Set the global exclude headers that will be set on each session created.
	 * 
	 * @param excludeHeaders
	 *          The global exclude header list.
	 * @see org.addsimplicity.anicetus.web.TelemetryHttpSession#setExcludeHeaders(Set)
	 */
	public void setExcludeHeaders(Set<String> excludeHeaders) {
		m_excludeHeaders = excludeHeaders;
	}

	/**
	 * Set the global exclude parameters that will be used on each session
	 * created.
	 * 
	 * @param excludeParameters
	 *          The global exclude parameter list.
	 * @see org.addsimplicity.anicetus.web.TelemetryHttpSession#setExcludeParameters(Set)
	 */
	public void setExcludeParameters(Set<String> excludeParameters) {
		m_excludeParameters = excludeParameters;
	}

	/**
	 * Set the global include header set that will be used on each session
	 * created.
	 * 
	 * @param includeHeaders
	 *          The global include header set.
	 * @see org.addsimplicity.anicetus.web.TelemetryHttpSession#setIncludeHeaders(Set)
	 */
	public void setIncludeHeaders(Set<String> includeHeaders) {
		m_includeHeaders = includeHeaders;
	}

	/**
	 * Set the global include parameters that will be used on each session.
	 * 
	 * @param includeParameters
	 *          The global include parameter set.
	 * @see org.addsimplicity.anicetus.web.TelemetryHttpSession#setIncludeParameters(Set)
	 */
	public void setIncludeParameters(Set<String> includeParameters) {
		m_includeParameters = includeParameters;
	}

	/**
	 * Create a http telemetry session and set the include/exclude lists.
	 * 
	 * @return the newly created session.
	 */
	@Override
	protected TelemetrySession createSession() {
		TelemetryHttpSession session = new TelemetryHttpSession();
		session.setIncludeHeaders(m_includeHeaders);
		session.setExcludeHeaders(m_excludeHeaders);
		session.setIncludeParameters(m_includeParameters);
		session.setExcludeParameters(m_excludeParameters);

		return session;
	}

}

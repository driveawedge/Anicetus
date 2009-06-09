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

package org.addsimplicity.anicetus.entity;

/**
 * A session defines the execution of something meaningful by the application.
 * In the case of HTTP based web applications, a session would be the duration
 * of a request. In most cases, the session will be easily understood. Every
 * application should have a boundary that represents a meaningful unit of work
 * that can be tracked as a telemetry session.
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * 
 */
public class TelemetrySession extends ExecInfo {
	static {
		EntityTypeRegistry.addClassShortName(TelemetrySession.class, "SE");
	}

	/**
	 * Create a session without a parent.
	 */
	public TelemetrySession() {
		super();
	}
}

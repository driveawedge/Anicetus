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
 * A state telemetry is a statement of a fact. Applications will use the state
 * telemetry to update the bus with facts that might be interesting for
 * monitoring or managing. For example, the number of progress of a long running
 * map/reduce could be reported periodically with state telemetry.
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * 
 */
public class TelemetryState extends SubTypedInfo {
	static {
		EntityTypeRegistry.addClassShortName(TelemetryState.class, "ST");
	}

	/**
	 * Construct a state without a parent.
	 */
	public TelemetryState() {
		super();
	}

	/**
	 * Construct a state with the specified artifact as the parent.
	 * 
	 * @param parent
	 */
	public TelemetryState(GlobalInfo parent) {
		super(parent);
	}

}

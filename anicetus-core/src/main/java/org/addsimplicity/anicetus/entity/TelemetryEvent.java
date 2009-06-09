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
 * An event is something that occurs of significance during the execution of the
 * application. The TelemetryEvent is used to capture this and publish it on the
 * bus. Events may be contained by a parent session or transaction or may be
 * stand alone.
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * 
 */
public class TelemetryEvent extends SubTypedInfo {
	static {
		EntityTypeRegistry.addClassShortName(TelemetryEvent.class, "EV");
	}

	/**
	 * Construct an event without a parent.
	 */
	public TelemetryEvent() {
		super();
	}

	/**
	 * Construct an event with the specified artifact as the parent.
	 * 
	 * @param parent
	 *          The artifact that is the parent of the new event.
	 */
	public TelemetryEvent(GlobalInfo parent) {
		super(parent);
	}
}

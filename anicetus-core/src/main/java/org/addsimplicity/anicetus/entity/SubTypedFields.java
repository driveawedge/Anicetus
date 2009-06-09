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
 * The SubTypedFields define the telemetry properties that are added by the
 * event and state entities.
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * @see org.addsimplicty.anicetus.entity.TelemetryEvent
 * @see org.addsimplicty.anicetus.entity.TelemetryState
 * 
 */
public enum SubTypedFields {
	/**
	 * The sub-type of the entity as defined by the application.
	 */
	Type(JsonConstants.SubType)

	;

	static {
		for (SubTypedFields field : SubTypedFields.values()) {
			EntityTypeRegistry.addJsonPropertyMapping(field.name(), field.getJsonKey());
		}

	}

	private String m_jsonKey;

	private SubTypedFields(String jsonKey) {
		m_jsonKey = jsonKey;
	}

	public String getJsonKey() {
		return m_jsonKey;
	}
}

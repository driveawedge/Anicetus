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
 * The GlobalInfoFields provides the distinguished names for the properties
 * provided by GlobalInfo.
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * @see org.addsimplicity.anicetus.entity.GlobalInfo
 */
public enum GlobalInfoFields {
	/**
	 * A GUID to identify each telemetry artifact.
	 */
	EntityId(JsonConstants.EntityIdentifier),

	/**
	 * The process and potentially thread that is executing the code generating
	 * telmetry.
	 */
	ExecutionContext(JsonConstants.ExecutionContext),

	/**
	 * An application specified message associated with this telemetry artifact.
	 */
	Message(JsonConstants.Message),

	/**
	 * The GUID of the parent that is the parent of the current telemetry
	 * artifact.
	 */
	ParentId(JsonConstants.ParentEntity),

	/**
	 * An identifier for the system where the telemetry is being generated.
	 * Typically a host name or IP address.
	 */
	ReportingNode(JsonConstants.ReportingNode),

	/**
	 * The time this telemetry artifact was created. Expressed as milliseconds
	 * since January 1, 1970 00:00:00 GMT.
	 */
	TimeStamp(JsonConstants.TimeStamp)

	;

	static {
		for (GlobalInfoFields field : GlobalInfoFields.values()) {
			EntityTypeRegistry.addJsonPropertyMapping(field.name(), field.getJsonKey());
		}
	}

	private String m_jsonKey;

	private GlobalInfoFields(String jsonKey) {
		m_jsonKey = jsonKey;
	}

	public String getJsonKey() {
		return m_jsonKey;
	}

}

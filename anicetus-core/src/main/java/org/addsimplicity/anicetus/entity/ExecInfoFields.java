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
 * The ExecInfoFields defines the properties that are added by ExecInfo
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * @see org.addsimplicity.anicetus.entity.ExecInfo
 * 
 */
public enum ExecInfoFields {
	/**
	 * The execution duration of the container in nanoseconds.
	 */
	Duration(JsonConstants.ExecutionTime),

	/**
	 * The name of the operation associated with the session or transaction.
	 */
	OperationName(JsonConstants.OperationName),

	/**
	 * The execution status as defined by CompletionStatus.
	 * 
	 * @see org.addsimplicity.anicetus.entity.CompletionStatus
	 */
	Status(JsonConstants.CompletionStatus)

	;

	static {
		for (ExecInfoFields field : ExecInfoFields.values()) {
			EntityTypeRegistry.addJsonPropertyMapping(field.name(), field.getJsonKey());
		}
	}

	private String m_jsonKey;

	private ExecInfoFields(String jsonKey) {
		m_jsonKey = jsonKey;
	}

	public String getJsonKey() {
		return m_jsonKey;
	}

}

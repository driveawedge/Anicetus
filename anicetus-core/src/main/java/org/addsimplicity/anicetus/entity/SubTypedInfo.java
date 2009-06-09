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
 * The sub-typed information is used by event and state entities to allow the
 * application to specify an additional type descriminator.
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * 
 */
public abstract class SubTypedInfo extends GlobalInfo {

	/**
	 * Construct a sub-type without a parent.
	 */
	public SubTypedInfo() {
	}

	/**
	 * Construct a sub-type with the specified parent.
	 * 
	 * @param parent
	 *          The parent of this sub-type.
	 */
	public SubTypedInfo(GlobalInfo parent) {
		super(parent);
	}

	/**
	 * The type is an arbitrary name specified by the application.
	 * 
	 * @return the event type.
	 */
	public String getType() {
		return (String) get(SubTypedFields.Type.name());
	}

	/**
	 * The type is an arbitrary name specified by the application.
	 * 
	 * @param type
	 *          The sub-type.
	 */
	public void setType(String type) {
		put(SubTypedFields.Type.name(), type);
	}

}

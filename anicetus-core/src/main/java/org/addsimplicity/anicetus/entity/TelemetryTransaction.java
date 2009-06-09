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
 * A transaction captures the details of an interaction with a component of the
 * application. These are typically external resources such as a database or
 * service but are not strictly required to be external.
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * 
 */
public class TelemetryTransaction extends ExecInfo {
	static {
		EntityTypeRegistry.addClassShortName(TelemetryTransaction.class, "TR");
	}

	/**
	 * Construct a transaction without a parent.
	 */
	public TelemetryTransaction() {
		super();
	}

	/**
	 * Construct a transaction with the specified artifact as the parent.
	 * 
	 * @param parent
	 */
	public TelemetryTransaction(GlobalInfo parent) {
		super(parent);
	}

	/**
	 * Parameters may be optional specified and associated with the operation.
	 * 
	 * @return the parameters associated with the operation.
	 */
	public Object[] getParameters() {
		return (Object[]) get(TransactionFields.Parameters.name());
	}

	/**
	 * Get the identifier of the primary resource being manipulated by this
	 * transaction.
	 * 
	 * @return the resource identifier.
	 */
	public String getResourceId() {
		return (String) get(TransactionFields.ResourceId.name());
	}

	/**
	 * Set optional parameters associated with the operation.
	 * 
	 * @param params
	 *          The list of parameters associated with the operation.
	 */
	public void setParameters(Object[] params) {
		put(TransactionFields.Parameters.name(), params);
	}

	/**
	 * Set a resource identifier of the primary resource acted upon by this
	 * transaction.
	 * 
	 * @param resource
	 *          The identifier of the primary resource.
	 */
	public void setResourceId(String resource) {
		put(TransactionFields.ResourceId.name(), resource);
	}
}

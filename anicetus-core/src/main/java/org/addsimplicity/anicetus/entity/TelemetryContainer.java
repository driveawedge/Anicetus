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

import java.util.Collection;

/**
 * TelemetryContainer defines an artifact as being capable of having children
 * directly embedded within them. Typically these are artifacts that have a
 * spanning execution time and might reference other resources during their
 * execution.
 * 
 * @author Dan Pritchett
 * 
 */
public interface TelemetryContainer {
	/**
	 * Add a child to this container. The child will have the container
	 * automatically set as the parent.
	 * 
	 * @param child
	 *          The child to add.
	 */
	void addChild(GlobalInfo child);

	/**
	 * Get an immutable collection of the children of this container.
	 * 
	 * @return the children of this container.
	 */
	Collection<GlobalInfo> getChildren();
}

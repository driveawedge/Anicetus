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
package org.addsimplicity.anicetus.io;

/**
 * The missing property exception occurs when a json string maps to a java
 * object and a property in the json string is not settable on the object. It is
 * non-fatal.
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * 
 */
public class MissingPropertyException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Construct an empty exception.
	 */
	public MissingPropertyException() {
	}

	/**
	 * Construct a message with the specified string.
	 * 
	 * @param message
	 *          The message string.
	 */
	public MissingPropertyException(String message) {
		super(message);
	}

	/**
	 * Construct an exception given the missing property name and the type.
	 * 
	 * @param name
	 *          The name of the property.
	 * @param type
	 *          The object type.
	 */
	public MissingPropertyException(String name, Object type) {
		super("Name: " + name + ", Class: " + type.getClass().getName());
	}

	/**
	 * Construct an exception with the specified message and cause exception.
	 * 
	 * @param message
	 *          The exception message.
	 * @param cause
	 *          The triggering exception.
	 */
	public MissingPropertyException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Construct a message with the specified cause.
	 * 
	 * @param cause
	 *          The triggering exception.
	 */
	public MissingPropertyException(Throwable cause) {
		super(cause);
	}

}

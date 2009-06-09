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
 * The exception handler interface provides a notification mechanism for
 * applications to handle exceptions that occur during the publishing of
 * telemetry to the bus.
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * 
 */
public interface ExceptionHandler {

	/**
	 * The exceptionCaught method will be invoked if an exception occurs during
	 * telemetry publication. Note that if this method throws an exception, the
	 * side effects are indeterminate as the caller of this method is already
	 * inside of a catch block.
	 * 
	 * @param exception
	 *          The exception that was caught.
	 */
	void exceptionCaught(Throwable exception);
}

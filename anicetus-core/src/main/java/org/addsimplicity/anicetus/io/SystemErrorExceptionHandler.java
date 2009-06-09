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
 * The default exception handler. It is registered automatically in all cases
 * where an exception handler can be provided.
 * 
 * @author Dan Pritchett (driveawedge@yahoo.com)
 * 
 */
public class SystemErrorExceptionHandler implements ExceptionHandler {

	/**
	 * Dump the exception to System.err.
	 * 
	 * @param exception
	 *          The exception that was caught.
	 */
	public void exceptionCaught(Throwable exception) {
		exception.printStackTrace();
	}

}
